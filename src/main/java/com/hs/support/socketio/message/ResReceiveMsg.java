package com.hs.support.socketio.message;

import com.hs.socketio.support.ResponseMessage;

public class ResReceiveMsg extends ResponseMessage {
    public static final String EVENTNAME = "supportReceiveMsg";
    private String msgId;

    private String direction;
}
