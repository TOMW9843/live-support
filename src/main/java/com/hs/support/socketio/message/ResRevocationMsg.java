package com.hs.support.socketio.message;

import com.hs.socketio.support.ResponseMessage;

import java.util.List;

public class ResRevocationMsg extends ResponseMessage {

    private static final long serialVersionUID = -6757679255726606550L;

    public static final String EVENTNAME = "supportReceiveDelMsg";

    private List<String> msgIds;

    public List<String> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<String> msgIds) {
        this.msgIds = msgIds;
    }
}
