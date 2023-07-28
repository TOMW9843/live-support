package com.hs.support.socketio;

import com.hs.socketio.IdSession;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatMessage;
import party.Party;

import java.util.List;
import java.util.UUID;

public interface OnlineSupportSocketPusherService {


    /**
     * 接收聊天消息
     *
     * @param supportChatMessage 支持聊天消息
     * @param idSession          连接的idSession
     */
    void supportReceiveMessage(SupportChatMessage supportChatMessage, IdSession idSession, Party party);


    /**
     * 发送所有未读消息
     *
     * @param supportChatMessage 支持聊天消息
     * @param idSession          会话id
     * @param party              方
     */
    void supportUnReadMessage(List<SupportChatMessage> supportChatMessage, IdSession idSession, Party party);


    /**
     * 客服接收用户列表数据更新
     *
     * @param supportChatInfo 支持聊天信息
     * @param idSession       会话id
     */
    void supportReceiveUser(SupportChatInfo supportChatInfo,IdSession idSession);
}
