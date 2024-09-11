package module.support;

import module.socketio.IdSession;
import module.support.model.Chat;

import java.util.List;

public interface LiveSupportAdminService {
    /**
     * WS
     */
    /**
     * 连接
     */
    void connect(IdSession session);

    /**
     * 发送消息
     * @param type 消息类型
     *
     * 文字或表情消息 [ text ]
     * 图片消息 [ img ]
     * 富文本消息 [ html ]
     *
     * @param content 消息内容
     *
     * 图片类型内容是URL地址
     * 富文本类型内容是JOSN内容
     */
    void send(Long chatid, String type, String content);

    /**
     * HTTP
     */
    /**
     * 创建用户对话
     */
    Chat create(Long partyId);

    /**
     * 已读回执
     */
    Long read(Long chatid);

    //管理

    /**
     * 撤回消息
     */
    void delmsg(Long chatid,List<Long> msgids);

    /**
     *设置备注
     */
    void setRemark(Long chatId,String remarks);


    /**
     *加入黑名单
     */
    void addBlacklist(Long chatId);


    /**
     *移除黑名单
     */
    void deleteBlacklist(Long chatId);
}
