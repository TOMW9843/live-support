package com.hs.support.socketio.message;

import com.hs.socketio.support.RequestMessage;

public class ReqReadMsg extends RequestMessage {

    public static final String EVENTNAME = "supportReadMsg";

    private Long partyId;

    private String noLoginId;

    private String direction;

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
