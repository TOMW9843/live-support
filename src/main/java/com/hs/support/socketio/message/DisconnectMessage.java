package com.hs.support.socketio.message;

import com.corundumstudio.socketio.SocketIOClient;
import com.hs.socketio.support.RequestMessage;

public class DisconnectMessage extends RequestMessage {

    public static final String EVENTNAME = "disconnect";
    public DisconnectMessage(String event, SocketIOClient client)
    {
        super(event, client);
    }
}
