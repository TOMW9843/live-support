package com.hs.support.socketio.message;

import com.hs.socketio.support.RequestMessage;

/**
 * 消息发送
 */
public class ReqSendMsg extends RequestMessage {

    public static final String EVENTNAME = "supportSendMsg";

    private Long partyId;

    private String noLoginId;


    private String cmd;


    private String type;

    private String content;

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

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
