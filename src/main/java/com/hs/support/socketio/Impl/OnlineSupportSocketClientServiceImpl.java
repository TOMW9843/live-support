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

import javax.crypto.MacSpi;
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
            if (Constants.CS_ROLE.contains(party.getRole())) {
                idSession.setAttribute("party", party);
                customerServerOnlineMap.put(idSession.getClient().getSessionId().toString(), idSession);
            }
            /**
             * 如果是用户
             */
            else {

                List<SupportChatMessage> msgs = supportChatMessageService.unReadMsg(null, party.getId(), SupportChatMessage.RECEIVE_DIR);
                if (msgs != null) {
                    onlineSupportSocketPusherService.supportUnReadMessage(msgs, idSession, party);
                }

            }
        }
        //未登录用户的未读消息提示
        else if (idSession.getNoLoginId() != null) {

            List<SupportChatMessage> msgs = supportChatMessageService.unReadMsg(idSession.getNoLoginId(), null, SupportChatMessage.RECEIVE_DIR);
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
            if (party != null && Constants.CS_ROLE.contains(party.getRole())) {
                chatMessage = onlineSupportSocketMessageService.createCustomerServiceMessage(message, party);
            }
            //玩家发送消息
            else {

                chatMessage = onlineSupportSocketMessageService.createUserChatMessage(idSession, party, message);
                isUser = true;
            }

            SupportChatInfo chatInfo = null;
            //发送消息
            if (chatMessage != null) {

                if (isUser && idSession.getPartyId() == null) {
                    //关联未登录Id
                    idSessionManager.touchNoLoginIdSession(SocketIOContext.NAMESPACE, message.getNoLoginId(), idSession);
                }

                if (isUser) {
                    chatInfo = sendToCustomServer(chatMessage, idSession, party);
                } else {
                    chatInfo = sendToUser(chatMessage, idSession);
                }
            }

            //发送用户列表数据更新
            for (Map.Entry<String, IdSession> kv : customerServerOnlineMap.entrySet()) {

                onlineSupportSocketPusherService.supportReceiveUser(chatInfo, kv.getValue());
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
    private SupportChatInfo sendToCustomServer(SupportChatMessage chatMessage, IdSession session, Party party) {


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

        return chatInfo;

    }


    /**
     * 发送给玩家的消息
     *
     * @param chatMessage 聊天信息
     */
    private SupportChatInfo sendToUser(SupportChatMessage chatMessage, IdSession idSession) {

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

        return chatInfo;

    }


    @Override
    public void read(ReqReadMsg message) {

        IdSession idSession = idSessionManager.getSession(SocketIOContext.NAMESPACE, message.getClient().getSessionId());
        if (idSession == null)
            return;
        if (idSession.getPartyId() == null && message.getNoLoginId() == null)
            return;
        try {

            SupportChatInfo chatInfo = null;
            //客服读取
            if (message.getDirection().equals(SupportChatMessage.SEND_DIR)
                    && customerServerOnlineMap.containsKey(idSession.getClient().getSessionId().toString())) {
                chatInfo = message.getPartyId() == null ? supportChatInfoService.findByNoLoginId(message.getNoLoginId()) : supportChatInfoService.findByPartyId(message.getPartyId());
                if (chatInfo != null)
                    chatInfo.setAccountManagerUnreadNum(0);
            } else if (message.getDirection().equals(SupportChatMessage.RECEIVE_DIR)) {
                chatInfo = idSession.getPartyId() == null ? supportChatInfoService.findByNoLoginId(message.getNoLoginId()) : supportChatInfoService.findByPartyId(idSession.getPartyId());
                if (chatInfo != null)
                    chatInfo.setUserUnreadNum(0);
            }

            if (chatInfo != null) {
                supportChatInfoService.update(chatInfo);

                //清除未读缓存
                supportChatMessageService.readMsg(chatInfo.getNoLoginId(), chatInfo.getPartyId(), message.getDirection());

                //发送已读确认
                messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReadMsg.EVENTNAME, idSession.getClient().getSessionId(), new ResReadMsg());
            }


        } catch (Exception e) {
            logger.error("OnlineSupportSocketClientService.read(ReqReadMsg message)消息发送失败--noLoginId:{},partyId:{}", message.getNoLoginId(), message.getPartyId());
            logger.error("OnlineSupportSocketClientService.read(ReqReadMsg message)消息发送失败--error:{},trace:{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReadMsg.EVENTNAME, idSession.getClient().getSessionId(), new ResReadMsg("500", "已读回执失败"));
        }

    }

    @Override
    public void del(String msgIdStr) {

        String[] ids = msgIdStr.split(",");
        if (ids.length > 0) {

            List<String> msgIds = Arrays.asList(ids);

            //假删除消息
            supportChatMessageService.delMsg(msgIds);

            //通知客户和用户回撤

            ResRevocationMsg msg = new ResRevocationMsg();
            msg.setMsgIds(msgIds);

            //回撤广播
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResRevocationMsg.EVENTNAME, msg);

        }

    }


}
