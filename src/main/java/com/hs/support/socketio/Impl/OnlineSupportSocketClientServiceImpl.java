package com.hs.support.socketio.Impl;

import com.hs.socketio.IdSession;
import com.hs.socketio.IdSessionManager;
import com.hs.socketio.MessagePusher;
import com.hs.support.*;
import com.hs.support.socketio.OnlineSupportSocketClientService;
import com.hs.support.socketio.OnlineSupportSocketMessageService;
import com.hs.support.socketio.OnlineSupportSocketPusherService;
import com.hs.support.socketio.message.*;
import framework.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import party.Party;
import party.PartyService;
import security.token.SecurityTokenService;
import security.token.Token;

import javax.servlet.http.Part;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class OnlineSupportSocketClientServiceImpl implements OnlineSupportSocketClientService {
    private static final Logger logger = LoggerFactory.getLogger(OnlineSupportSocketClientService.class);

    @Autowired
    IdSessionManager idSessionManager;

    @Autowired
    SecurityTokenService tokenService;

    @Autowired
    RedisService redisService;

    @Autowired
    PartyService partyService;

    @Autowired
    SupportChatInfoService supportChatInfoService;

    @Autowired
    SupportChatMessageService supportChatMessageService;

    @Autowired
    OnlineSupportSocketMessageService onlineSupportSocketMessageService;

    @Autowired
    OnlineSupportSocketPusherService onlineSupportSocketPusherService;

    @Autowired
    private MessagePusher messagePusher;

    /**
     * 客服上线记录字典
     */
    private ConcurrentMap<String, IdSession> customerServerOnlineMap = new ConcurrentHashMap<>();


    @Override
    public void connect(ConnectMessage message) {
        IdSession idSession = idSessionManager.getSession(SocketIOContext.NAMESPACE, message.getClient().getSessionId());
        if (idSession.getPartyId() != null) {
            Party party = partyService.get(idSession.getPartyId());
            /**
             * 如果是客服
             */
            if (party.getRole().equals(Constants.CS_ROLE)) {
                idSession.setAttribute("party", party);
                customerServerOnlineMap.put(idSession.getClient().getSessionId().toString(), idSession);
            }
            /**
             * 如果是用户
             */
            else {

                List<SupportChatMessage> msgs = supportChatMessageService.unReadMsg(null, party.getId(),SupportChatMessage.RECEIVE_DIR);
                if (msgs != null) {
                    onlineSupportSocketPusherService.supportUnReadMessage(msgs, idSession, party);
                }

            }
        }
        //未登录用户的未读消息提示
        else if (idSession.getNoLoginId() != null) {

            List<SupportChatMessage> msgs = supportChatMessageService.unReadMsg(idSession.getNoLoginId(), null,SupportChatMessage.RECEIVE_DIR);
            if (msgs != null) {
                onlineSupportSocketPusherService.supportUnReadMessage(msgs, idSession, null);
            }

        }

    }

    @Override
    public void disconnect(DisconnectMessage message) {

        customerServerOnlineMap.remove(message.getClient().getSessionId().toString());
    }

    @Override
    public void user(ReqUser message) {
        Token token = tokenService.find(message.getToken());
        if (token != null) {

            IdSession idSession = idSessionManager.getSession(SocketIOContext.NAMESPACE, message.getClient().getSessionId());
            idSession.setToken(token.getToken());
            idSessionManager.refresh(SocketIOContext.NAMESPACE, token.getPartyId(), idSession);
        }
    }

    @Override
    public void send(ReqSendMsg message) {

        IdSession idSession = idSessionManager.getSession(SocketIOContext.NAMESPACE, message.getClient().getSessionId());
        if (idSession == null)
            return;
        if (idSession.getPartyId() == null && message.getNoLoginId() == null)
            return;

        Party party = partyService.get(idSession.getPartyId());
        SupportChatMessage chatMessage = null;
        boolean isUser = false;
        try {
            //客服
            if (party != null && party.getRole().equals(Constants.CS_ROLE)) {
                chatMessage = onlineSupportSocketMessageService.createCustomerServiceMessage(message, party);
            }
            //玩家发送消息
            else {

                chatMessage = onlineSupportSocketMessageService.createUserChatMessage(idSession, party, message);
                isUser = true;
            }


            //发送消息
            if (chatMessage != null) {

                if (isUser && idSession.getPartyId() == null) {
                    //关联未登录Id
                    idSessionManager.touchNoLoginIdSession(SocketIOContext.NAMESPACE, message.getNoLoginId(), idSession);
                }

                if (isUser) {
                    sendToCustomServer(chatMessage, idSession, party);
                } else {
                    sendToUser(chatMessage, idSession);
                }
            }


        } catch (Exception e) {
            logger.error("OnlineSupportSocketClientService.send(ReqSendMsg message)消息发送失败--noLoginId:{},partyId:{}", message.getNoLoginId(), message.getPartyId());
            logger.error("OnlineSupportSocketClientService.send(ReqSendMsg message)消息发送失败--error:{},trace:{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReceiveMsg.EVENTNAME, idSession.getClient().getSessionId(), new ResReceiveMsg("500", "消息发送失败"));
        }

    }


    /**
     * 发送给客服的消息
     *
     * @param chatMessage 聊天信息
     * @param session     会话
     */
    private void sendToCustomServer(SupportChatMessage chatMessage, IdSession session, Party party) {


        SupportChatInfo chatInfo = party == null ? supportChatInfoService.findByNoLoginId(session.getNoLoginId()) : supportChatInfoService.findByPartyId(party.getId());

        chatInfo.setAccountManagerUnreadNum(chatInfo.getAccountManagerUnreadNum() + 1);
        chatInfo.setLastMsg(chatMessage.getContent());
        chatInfo.setLastTime(chatMessage.getCreatedTime());
        if (party != null) {
            chatInfo.setAvatar(party.getAvatar());
            chatInfo.setNickName(party.getNickname());
        }
        supportChatInfoService.update(chatInfo);

        //发给自己
        onlineSupportSocketPusherService.supportReceiveMessage(chatMessage, session, party);

        //发给所有在线客服
        for (Map.Entry<String, IdSession> kv : customerServerOnlineMap.entrySet()) {

            onlineSupportSocketPusherService.supportReceiveMessage(chatMessage, kv.getValue(), party);
        }


    }


    /**
     * 发送给玩家的消息
     *
     * @param chatMessage 聊天信息
     */
    private void sendToUser(SupportChatMessage chatMessage, IdSession idSession) {

        SupportChatInfo chatInfo = chatMessage.getPartyId() == null ? supportChatInfoService.findByNoLoginId(chatMessage.getNoLoginId()) : supportChatInfoService.findByPartyId(chatMessage.getPartyId());
        chatInfo.setUserUnreadNum(chatInfo.getUserUnreadNum() + 1);
        chatInfo.setLastMsg(chatMessage.getContent());
        chatInfo.setLastTime(chatMessage.getCreatedTime());
        chatInfo.setLastResponder(idSession.getPartyId());
        supportChatInfoService.update(chatInfo);

        //发给所有在线客服
        for (Map.Entry<String, IdSession> kv : customerServerOnlineMap.entrySet()) {

            onlineSupportSocketPusherService.supportReceiveMessage(chatMessage, kv.getValue(), (Party) idSession.getAttribute("party"));
        }

        IdSession userSession = chatMessage.getPartyId() == null ? idSessionManager.getSession(SocketIOContext.NAMESPACE, chatMessage.getNoLoginId()) :
                idSessionManager.getSession(SocketIOContext.NAMESPACE, chatMessage.getPartyId());
        //如果玩家在线
        if (userSession != null) {
            Party party = partyService.get(chatMessage.getPartyId());
            onlineSupportSocketPusherService.supportReceiveMessage(chatMessage, userSession, party);
        }


    }


    @Override
    public void read(ReqReadMsg message) {

        IdSession idSession = idSessionManager.getSession(SocketIOContext.NAMESPACE, message.getClient().getSessionId());
        if (idSession == null)
            return;
        if (idSession.getPartyId() == null && message.getNoLoginId() == null)
            return;
        try {

            SupportChatInfo chatInfo = message.getPartyId() == null ? supportChatInfoService.findByNoLoginId(message.getNoLoginId()) : supportChatInfoService.findByPartyId(message.getPartyId());
            String direction="";
            //客服
            if(customerServerOnlineMap.containsKey(idSession.getClient().getSessionId().toString())){
                chatInfo.setAccountManagerUnreadNum(0);
                direction=SupportChatMessage.SEND_DIR;
            }else {
                chatInfo.setUserUnreadNum(0);
                direction=SupportChatMessage.RECEIVE_DIR;
            }
            supportChatInfoService.update(chatInfo);

            //清除未读缓存
            supportChatMessageService.readMsg(message.getNoLoginId(),message.getPartyId(),direction);

            //发送已读确认
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReadMsg.EVENTNAME,idSession.getClient().getSessionId(),new ResReadMsg());

        } catch (Exception e) {
            logger.error("OnlineSupportSocketClientService.read(ReqReadMsg message)消息发送失败--noLoginId:{},partyId:{}", message.getNoLoginId(), message.getPartyId());
            logger.error("OnlineSupportSocketClientService.read(ReqReadMsg message)消息发送失败--error:{},trace:{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReadMsg.EVENTNAME, idSession.getClient().getSessionId(), new ResReadMsg("500", "已读回执失败"));
        }

    }


}
