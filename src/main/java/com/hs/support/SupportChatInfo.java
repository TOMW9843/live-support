package com.hs.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import common.bo.EntityObject;

/**
 * 消息列表
 */
@TableName("support_chat_info")
public class SupportChatInfo extends EntityObject<SupportChatInfo> implements Comparable<SupportChatInfo> {

    private static final long serialVersionUID = -7768174302895619763L;

    @TableField("party_id")
    private Long partyId;

    @TableField("no_login_id")
    private String noLoginId;

    /**
     * 用户未读消息数
     */
    @TableField("user_unread_num")
    private int userUnreadNum;
    /**
     * 客服未读消息数
     */
    @TableField("account_manager_unread_num")
    private int accountManagerUnreadNum;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 最后回复的客服-partyId
     */
    @TableField("last_responder")
    private Long lastResponder;

    @TableField("ip")
    private String ip;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    private String avatar;

    /**
     * 最后消息，不需要保存数据库
     */
    @TableField(exist = false)
    private String lastMsg;

    /**
     * 最后更新时间，不需要保存数据库
     */
    @TableField(exist = false)
    private Long lastTime;

    @Override
    public int compareTo(SupportChatInfo supportChatInfo) {
        if (this.lastTime > supportChatInfo.getLastTime()) {
            return -1;
        } else if (this.lastTime < supportChatInfo.getLastTime()) {
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

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
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
}
