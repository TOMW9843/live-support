package com.hs.support.socketio.message;

import com.corundumstudio.socketio.SocketIOClient;
import com.hs.socketio.support.RequestMessage;

public class ConnectMessage extends RequestMessage {

    public static final String EVENTNAME = "connect";

    public ConnectMessage(String event, SocketIOClient client) {
        super(event, client);
    }
}
