package module.support.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import common.bo.EntityObject;


/**
 * 客服留言表
 */
@TableName("support_message")
public class Message extends EntityObject<Message> implements Comparable<Message> {

    private static final long serialVersionUID = -4999012202564084751L;

    /**
     * 消息方向
     */
    public static final String SEND_DIR="send";
    public static final String RECEIVE_DIR="receive";

    /**
     * 消息类型
     */
    public static final String MSG_TYPE_TEXT="text";
    public static final String MSG_TYPE_IMG="img";

    private Long chatid;

    @TableField("party_id")
    private Long partyId;

    @TableField("no_login_id")
    private String noLoginId;

    /**
     * 消息方向
     * send 用户发送
     * receive 用户接收
     */
    private String direction;


    /**
     * text 文本
     * img  图片
     * html 富文本消息
     */
    private String type;

    /**
     * 消息
     */
    private String content;

    /**
     * 标记删除，true删除
     */
    @TableField("deleted")
    private boolean deleted = false;


    @TableField("created_time")
    private Long createdTime;
    @Override
    public int compareTo(Message supportChatMessage) {

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

    public Long getChatid() {
        return chatid;
    }

    public void setChatid(Long chatid) {
        this.chatid = chatid;
    }
}
