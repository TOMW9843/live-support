package com.hs.support;

import common.bo.EntityObject;

/**
 * 消息列表
 */
public class ChatInfo extends EntityObject<ChatInfo> implements Comparable<ChatInfo> {

    private static final long serialVersionUID = -7768174302895619763L;

    private Long partyId;

    private Long noLoginId;

    /**
     * 用户未读消息数
     */
    private int userUnreadNum;
    /**
     * 客服未读消息数
     */
    private int accountManagerUnreadNum;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 最后回复的客服-partyId
     */
    private Long lastResponder;

    /**
     * 最后消息，不需要保存数据库
     */
    private String lastmsg;

    /**
     * 最后更新时间，不需要保存数据库
     */
    private Long lastTime;

    @Override
    public int compareTo(ChatInfo chatInfo) {
        if (this.lastTime > chatInfo.getLastTime()) {
            return -1;
        } else if (this.lastTime < chatInfo.getLastTime()) {
            return 1;
        }
        return 0;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public Long getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(Long noLoginId) {
        this.noLoginId = noLoginId;
    }

    public int getUserUnreadNum() {
        return userUnreadNum;
    }

    public void setUserUnreadNum(int userUnreadNum) {
        this.userUnreadNum = userUnreadNum;
    }

    public int getAccountManagerUnreadNum() {
        return accountManagerUnreadNum;
    }

    public void setAccountManagerUnreadNum(int accountManagerUnreadNum) {
        this.accountManagerUnreadNum = accountManagerUnreadNum;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getLastResponder() {
        return lastResponder;
    }

    public void setLastResponder(Long lastResponder) {
        this.lastResponder = lastResponder;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }
}
