package module.support;

import module.socketio.IdSession;
import module.support.model.Message;

import java.util.List;


public interface LiveSupportApiService {
    /**
     * WS
     */
    /**
     * 连接
     */
    public void connect(IdSession session);

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
    public void send(IdSession session, String type, String content);


    /**
     * HTTP
     */

    /**
     * 已读回执
     * @return 已读时间戳
     */
    public Long read(Long partyId,String noLoginId);
}
