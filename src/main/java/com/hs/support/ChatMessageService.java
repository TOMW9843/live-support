package com.hs.support;

/**
 * 客服消息
 */
public interface ChatMessageService {

    /**
     * 发送一条消息
     */
    public void send(ChatMessage chatMessage);

}
