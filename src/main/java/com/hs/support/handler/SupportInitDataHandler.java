package com.hs.support.handler;


import com.hs.support.socketio.thread.SupportChatMessageDispatcherWorkerThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 初始化数据
 */
@Component
public class SupportInitDataHandler implements ApplicationRunner {


    @Autowired
    SupportChatMessageDispatcherWorkerThread supportChatMessageDispatcherWorkerThread;

    @Override
    public void run(ApplicationArguments args) throws Exception {

         supportChatMessageDispatcherWorkerThread.start();

    }
}
