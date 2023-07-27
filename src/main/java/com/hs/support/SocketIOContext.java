package com.hs.support;

import com.hs.socketio.support.MessageQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component(value = "supportChatSocketIOContext")
public class SocketIOContext {

    /**
     * 聊天模块命名空间
     */
    public static final String NAMESPACE="supportChat";

    @Bean(value = "supportChatMessageQueue")
    public MessageQueue messageQueue() {
        return new MessageQueue();
    }
}
