package com.hs.support.socketio.Impl;

import com.hs.socketio.IdSession;
import com.hs.socketio.IdSessionManager;
import com.hs.socketio.MessagePusher;
import com.hs.support.SocketIOContext;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatMessage;
import com.hs.support.socketio.OnlineSupportSocketPusherService;
import com.hs.support.socketio.message.ReceiveMsg;
import com.hs.support.socketio.message.ResReceiveMsg;
import com.hs.support.socketio.message.ResReceiveUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.IdempotentReceiver;
import org.springframework.stereotype.Service;
import party.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OnlineSupportSocketPusherServiceImpl implements OnlineSupportSocketPusherService {

    private static final Logger logger = LoggerFactory.getLogger(OnlineSupportSocketPusherService.class);

    @Autowired
    private MessagePusher messagePusher;

    @Autowired
    IdSessionManager idSessionManager;


    @Override
    public void supportReceiveMessage(SupportChatMessage message, IdSession idSession, Party party) {

        try {
            ResReceiveMsg packet=new ResReceiveMsg();

            ReceiveMsg msg=new ReceiveMsg();
            msg.setMsgId(message.getId().toString());
            msg.setDirection(message.getDirection());
            msg.setCmd(message.getCmd());
            msg.setType(message.getType());
            if (message.getType().equals(SupportChatMessage.MSG_TYPE_TEXT))
                msg.setContent(message.getContent());
            else if (message.getType().equals(SupportChatMessage.MSG_TYPE_IMG))
                msg.setContent(message.getImg());

            msg.setSendTime(message.getCreatedTime());

            if(party!=null){
                msg.setNickName(party.getNickname());
                msg.setAvatar(party.getAvatar());
            }

            packet.setMsg(msg);

            //向客户端发送消息
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReceiveMsg.EVENTNAME, idSession.getClient().getSessionId(), packet);

        } catch (Throwable t) {

            logger.error("[程序错误] OnlineSupportSocketPusherService.supportReceiveMessage(SupportChatMessage supportChatMessage, IdSession idSession, Party party) fail", t);
        }

    }

    @Override
    public void supportUnReadMessage(List<SupportChatMessage> supportChatMessage, IdSession idSession, Party party) {
        try {
            ResReceiveMsg packet=new ResReceiveMsg();
            List<ReceiveMsg> msgList=new ArrayList<>();

            for(SupportChatMessage message:supportChatMessage){
                ReceiveMsg msg=new ReceiveMsg();
                msg.setMsgId(message.getId().toString());
                msg.setDirection(message.getDirection());
                msg.setCmd(message.getCmd());
                msg.setType(message.getType());
                if (message.getType().equals(SupportChatMessage.MSG_TYPE_TEXT))
                    msg.setContent(message.getContent());
                else if (message.getType().equals(SupportChatMessage.MSG_TYPE_IMG))
                    msg.setContent(message.getImg());
                msg.setSendTime(message.getCreatedTime());

                if(party!=null){
                    msg.setNickName(party.getNickname());
                    msg.setAvatar(party.getAvatar());
                }
                msgList.add(msg);
            }

            packet.setMsgList(msgList);

            //向客户端发送消息
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReceiveMsg.EVENTNAME, idSession.getClient().getSessionId(), packet);

        } catch (Throwable t) {

            logger.error("[程序错误] OnlineSupportSocketPusherService.supportUnReadMessage(List<SupportChatMessage> supportChatMessage, IdSession idSession, Party party) fail", t);
        }
    }

    @Override
    public void supportReceiveUser(SupportChatInfo info, IdSession idSession) {
        try {
            ResReceiveUser packet=new ResReceiveUser();

            packet.setChatId(info.getId());
            packet.setAvatar(info.getAvatar());
            packet.setIp(info.getIp());
            packet.setLastMsg(info.getLastMsg());
            packet.setLastTime(info.getLastTime());
            packet.setNoLoginId(info.getNoLoginId());
            packet.setPartyId(info.getPartyId());
            packet.setUnReadNum(info.getAccountManagerUnreadNum());

            //向客户端发送消息
            messagePusher.pushMessage(SocketIOContext.NAMESPACE, ResReceiveUser.EVENTNAME, idSession.getClient().getSessionId(), packet);

        } catch (Throwable t) {

            logger.error("[程序错误] OnlineSupportSocketPusherService.supportReceiveUser(SupportChatInfo supportChatInfo, IdSession idSession) fail", t);
        }
    }
}
