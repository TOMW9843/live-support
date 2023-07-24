package com.hs.support.socketio.message;

import com.hs.socketio.support.RequestMessage;

public class ReqReadMsg extends RequestMessage {

    public static final String EVENTNAME = "supportReadMsg";

    private Long partyId;

    private String noLoginId;

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
        this.noLoginId = noLoginId;
    }
}
