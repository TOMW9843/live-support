package com.hs.support.socketio.message;

import java.io.Serializable;

public class ReceiveMsg implements Serializable {
    private static final long serialVersionUID = -6879635706370459579L;

    private String partyId;

    private String noLoginId;

    private String uid;

    private String msgId;

    private String direction;

    private String cmd;

    private String nickName;

    private String avatar;

    private String type;

    private String content;

    private Long sendTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
        this.noLoginId = noLoginId;
    }
}
