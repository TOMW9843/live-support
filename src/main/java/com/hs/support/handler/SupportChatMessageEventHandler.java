package com.hs.support.handler;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.hs.socketio.IdSession;
import com.hs.socketio.IdSessionManager;
import com.hs.socketio.support.MessageEventHandler;
import com.hs.socketio.support.MessageQueue;
import com.hs.support.SocketIOContext;
import com.hs.support.socketio.message.ConnectMessage;
import com.hs.support.socketio.message.DisconnectMessage;
import com.hs.support.socketio.message.ReqUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Component(value = "supportChatMessageEventHandler")
@ConditionalOnClass(SocketIOServer.class)
public class SupportChatMessageEventHandler extends MessageEventHandler {


    @Autowired
    private IdSessionManager idSessionManager;

    @Autowired
    @Qualifier("supportChatMessageQueue")
    private MessageQueue supportChatMessageQueue;

    @Override
    @OnConnect
    public void onConnect(SocketIOClient client) {

        IdSession session = new IdSession(client);
        idSessionManager.addSession(SocketIOContext.NAMESPACE, client.getSessionId(), session);

        supportChatMessageQueue.add(new ConnectMessage(ConnectMessage.EVENTNAME,client));

    }

    @Override
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {

        idSessionManager.removeSession(SocketIOContext.NAMESPACE, client.getSessionId());

        supportChatMessageQueue.add(new DisconnectMessage(DisconnectMessage.EVENTNAME,client));
    }


    @OnEvent(value = "user")
    public void user(SocketIOClient client, AckRequest ackRequest, String data) {

        JSONObject obj = JSONObject.parseObject(data);
        ReqUser message= JSONObject.toJavaObject(obj, ReqUser.class);
        message.setEvent(ReqUser.EVENTNAME);
        message.setClient(client);
        supportChatMessageQueue.add(message);
    }




}
