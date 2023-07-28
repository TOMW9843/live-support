package com.hs.support.socketio.message;

import com.hs.socketio.support.ResponseMessage;

public class ResReceiveUser extends ResponseMessage {
    private static final long serialVersionUID = -3594081287042085747L;

    public static final String EVENTNAME = "supportReceiveUser";

    private Long chatId;

    private Long partyId;

    private String noLoginId;

    private String nickName;

    private String avatar;

    private Integer unReadNum;

    private String lastMsg;

    private Long lastTime;

    private String ip;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

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

    public Integer getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(Integer unReadNum) {
        this.unReadNum = unReadNum;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
