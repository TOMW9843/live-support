package com.hs.support.socketio.thread;


import com.hs.socketio.support.MessageQueue;
import com.hs.socketio.support.RequestMessage;
import com.hs.support.socketio.OnlineSupportSocketClientService;
import com.hs.support.socketio.message.ConnectMessage;
import com.hs.support.socketio.message.DisconnectMessage;
import com.hs.support.socketio.message.ReqUser;
import com.hs.support.socketio.message.ReqSendMsg;
import common.util.ThreadUtils;
import framework.context.WorkerThread;
import framework.thread.CustomThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component(value = "supportChatMessageDispatcherWorkerThread")
public class SupportChatMessageDispatcherWorkerThread implements WorkerThread, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SupportChatMessageDispatcherWorkerThread.class);

    public volatile boolean isRunning = true;

    @Autowired
    @Qualifier("supportChatMessageQueue")
    private MessageQueue supportChatMessageQueue;

    @Autowired
    private OnlineSupportSocketClientService onlineSupportSocketClientService;

    private ExecutorService supportChatMessageReceiveThreadPool = new CustomThreadPool("supportChatMessageReceiveThreadPool",
            16, 32, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadPoolExecutor.CallerRunsPolicy()).executor();

    @Override
    public void start() {

        new Thread(this, "supportChatMessageDispatcherWorkerThread").start();

        if (logger.isInfoEnabled()) {
            logger.info("[---客服消息分发线程已开启---]");
        }


    }


    @Override
    public void run() {
        while (true) {

            try {

                RequestMessage message = supportChatMessageQueue.poll();

                if(!isRunning && message==null){
                    break;
                }

                if (message != null) {
                    supportChatMessageReceiveThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            runner(message);
                        }
                    });
                }

            } catch (Throwable t) {
                logger.error("supportChatMessageDispatcherWorkerThread 程序错误[run()]", t);

            }finally {
                ThreadUtils.sleep(10);
            }
        }

        logger.info("[----supportChatMessageDispatcherWorkerThread-任务执行完成成功退出----]");


    }

    public void runner(RequestMessage item) {
        try {
            switch(item.getEvent()) {
                case ConnectMessage.EVENTNAME:
                    onlineSupportSocketClientService.connect((ConnectMessage) item);
                    break;
                case DisconnectMessage.EVENTNAME:
                    onlineSupportSocketClientService.disconnect((DisconnectMessage) item);
                    break;
                case ReqUser.EVENTNAME:
                    onlineSupportSocketClientService.user((ReqUser)item);
                    break;
                case ReqSendMsg.EVENTNAME:
                    onlineSupportSocketClientService.send((ReqSendMsg)item);
            }

        } catch (Throwable t) {

            logger.error("[程序错误] supportChatMessageDispatcherWorkerThread.runner(RequestMessage item) fail", t);
        }
    }


    @Override
    public void stop() {
        isRunning=false;
    }

    @Override
    public void restart() {

        /**
         * To do nothing
         */
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
