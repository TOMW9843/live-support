package com.hs.support.socketio;

import com.hs.support.socketio.message.*;

public interface OnlineSupportSocketClientService {


    /**
     * Socket连接
     */
    void connect(ConnectMessage message);

    /**
     *  断开连接
     * @param message
     */
    void disconnect(DisconnectMessage message);
    /**
     * Socket关联用户
     */
    void user(ReqUser message);

    /**
     * 发送消息
     * @param message
     */
    void send(ReqSendMsg message);

    /**
     * 已读消息
     * @param message
     */
    void read(ReqReadMsg message);


    /**
     * 删除消息,使用','分隔
     *
     * @param msgIds 味精id
     */
    void del(String msgIds);
}
