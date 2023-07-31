package com.hs.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import common.bo.EntityObject;

@TableName("support_chat_black_list")
public class SupportChatBlackList extends EntityObject<SupportChatBlackList> {
    private static final long serialVersionUID = 2080505297877915782L;

    private String ip;

    @TableField("no_login_id")
    private String noLoginId;

    private String remarks;

    private Long created_time;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Long created_time) {
        this.created_time = created_time;
    }

    public String getNoLoginId() {
        return noLoginId;
    }

    public void setNoLoginId(String noLoginId) {
        this.noLoginId = noLoginId;
    }
}
