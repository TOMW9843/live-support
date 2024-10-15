package module.support.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import common.bo.EntityObject;

/**
 * 消息列表
 */
@TableName("support_chat")
public class Chat extends EntityObject<Chat> implements Comparable<Chat> {

    private static final long serialVersionUID = -7768174302895619763L;

    @TableField("party_id")
    private Long partyId;

    private String username;

    private String uid;

    @TableField("no_login_id")
    private String noLoginId;

    /**
     * 备注
     */
    private String remarks;

    /*
     * 不需要保存数据库
     */
    /**
     * 用户未读消息数
     */


    @TableField("user_unread")
    private int userUnread;
    /**
     * 用户读消息最后时间
     */
    @TableField("user_read_time")
    private Long userReadTime;
    /**
     * 客服未读消息数
     */
    @TableField("supporter_unread")
    private int supporterUnread;

    /**
     * 客服读消息最后时间
     */
    @TableField("supporter_read_time")
    private Long supporterReadTime;

    private String lastmsg;

    /**
     * 最后更新时间
     */
    @TableField("last_time")
    private Long lastTime;
    /**
     * 黑名单
     */
    private Boolean blacklist=false;

    @Override
    public int compareTo(Chat chat) {
        if (this.lastTime > chat.getLastTime()) {
            return -1;
        } else if (this.lastTime < chat.getLastTime()) {
            return -1;
        }
        return 0;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
        this.noLoginId = noLoginId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public int getUserUnread() {
        return userUnread;
    }

    public void setUserUnread(int userUnread) {
        this.userUnread = userUnread;
    }

    public int getSupporterUnread() {
        return supporterUnread;
    }

    public void setSupporterUnread(int supporterUnread) {
        this.supporterUnread = supporterUnread;
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

    public Long getUserReadTime() {
        return userReadTime;
    }

    public void setUserReadTime(Long userReadTime) {
        this.userReadTime = userReadTime;
    }

    public Long getSupporterReadTime() {
        return supporterReadTime;
    }

    public void setSupporterReadTime(Long supporterReadTime) {
        this.supporterReadTime = supporterReadTime;
    }

    public Boolean getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Boolean blacklist) {
        this.blacklist = blacklist;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
