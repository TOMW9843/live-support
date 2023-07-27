package com.hs.support.socketio.message;

import com.hs.socketio.support.RequestMessage;

public class ReqUser extends RequestMessage {

    public static final String EVENTNAME = "user";

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
