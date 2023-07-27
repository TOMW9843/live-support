package com.hs.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import common.bo.EntityObject;


/**
 * 客服留言表
 */
@TableName("support_chat_message")
public class SupportChatMessage extends EntityObject<SupportChatMessage> implements Comparable<SupportChatMessage> {

    private static final long serialVersionUID = -4999012202564084751L;

    public static final String SEND_DIR="send";

    public static final String RECEIVE_DIR="receive";

    @TableField("party_id")
    private Long partyId;

    @TableField("no_login_id")
    private String noLoginId;

    /**
     * 消息渠道
     *
     * message 普通消息
     * googleauth 谷歌验证
     * system 系统消息
     */
    private String cmd;

    /**
     * text 文本
     * img  图片
     * richtext 富文本消息
     */
    private String type;

    /**
     * 消息
     */
    private String content;

    /**
     * 消息方向
     * send 用户发送
     * receive 用户接收
     */
    private String direction;


    /**
     * 谁回复的-管理员或客服-partyId
     */

    private Long responder;

    /**
     * 标记删除，true删除
     */
    @TableField("deleted")
    private boolean deleted = false;


    @TableField("created_time")
    private Long createdTime;
    @Override
    public int compareTo(SupportChatMessage supportChatMessage) {

        if (this.createdTime > supportChatMessage.getCreatedTime()) {
            return 1;
        } else if (this.createdTime < supportChatMessage.getCreatedTime()) {
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Long getResponder() {
        return responder;
    }

    public void setResponder(Long responder) {
        this.responder = responder;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
        this.noLoginId = noLoginId;
    }

}
