package module.support.socketio;

import module.support.model.Chat;
import module.support.model.Message;

import java.util.List;

/**
 * Admin端消息推送
 */
public interface LiveSupportAdminPusherService {

    /**
     * 用户列表更新
     */
    public void user(List<Chat> entityList);

    /**
     * 消息推送
     */
    public void receive(List<Message> entityList);

    /**
     * 消息撤回
     */
    public void delmsg(Long chatId,List<Long> msgIds);
}
