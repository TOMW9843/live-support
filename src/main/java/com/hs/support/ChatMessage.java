package com.hs.support;

import com.baomidou.mybatisplus.annotation.TableField;
import common.bo.EntityObject;


/**
 * 客服留言表
 */

public class ChatMessage extends EntityObject<ChatMessage> implements Comparable<ChatMessage> {

    private static final long serialVersionUID = -4999012202564084751L;

    private Long partyId;


    private Long noLoginId;
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
    @TableField("isdel")
    private boolean isdel = false;

    private String ip;

    @TableField("created_time")
    private Long createdTime;
    @Override
    public int compareTo(ChatMessage chatMessage) {

        if (this.createdTime > chatMessage.getCreatedTime()) {
            return 1;
        } else if (this.createdTime < chatMessage.getCreatedTime()) {
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

    public boolean isIsdel() {
        return isdel;
    }

    public void setIsdel(boolean isdel) {
        this.isdel = isdel;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(Long noLoginId) {
        this.noLoginId = noLoginId;
    }
}
