package com.hs.support;

import java.util.List;

public interface SupportChatMessageService {

    /**
     * 发送一条消息
     */
    void send(SupportChatMessage supportChatMessage);


    /**
     * 添加一条新消息
     *
     * @param supportChatMessage 支持聊天消息
     */
    void insert(SupportChatMessage supportChatMessage);


    /**
     * 所有未读消息
     *
     * @param noLoginId 没有登录id
     * @param partyId   方id
     * @param direction 方向
     * @return {@link List}<{@link SupportChatMessage}>
     */
    List<SupportChatMessage> unReadMsg(String noLoginId,Long partyId,String direction);

    /**
     * 读取消息
     *
     * @param noLoginId 没有登录id
     * @param partyId   方id
     * @param direction 方向
     */
    void readMsg(String noLoginId,Long partyId,String direction);
}
