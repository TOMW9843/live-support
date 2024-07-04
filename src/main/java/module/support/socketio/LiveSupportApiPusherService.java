package module.support.socketio;

import module.socketio.IdSession;
import module.support.model.Message;

import java.util.List;

/**
 * Api端消息推送
 */
public interface LiveSupportApiPusherService {

    /**
     * 消息推送
     */
    public void receive(IdSession session, List<Message> entityList);
    /**
     * 消息撤回
     */
    public void delmsg(IdSession session, List<Long> msgIds);
}
