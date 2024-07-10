package module.support;

import common.util.DateTimeUtil;
import common.util.ThreadUtils;
import framework.context.WorkerThread;
import module.message.model.AdminMessage;
import module.message.MessageAdminPusherService;
import module.redis.RedisService;
import module.support.model.Chat;
import module.support.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class LiveSupportMessageDelayWorkerThread implements WorkerThread, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LiveSupportMessageDelayWorkerThread.class);

    @Autowired
    private MessageAdminPusherService messageAdminPusherService;

    @Autowired
    private RedisService redisService;

    private Thread thread;

    @Override
    public void start() {
        thread = new Thread(this, "LiveSupportMessageDelayWorkerThread");
        thread.start();
        if (logger.isInfoEnabled()) {
            logger.info("客服延时通知线程 [启动]");
        }

    }

    @Override
    public void run() {
        while (true) {
            try {

                /**
                 * 未回复客服消息
                 */
                Map<Object, Object> chatmap = redisService.hmget(Constants.redis_support_chat);
                int num = 0;
                for (Object value : chatmap.values()) {
                    num = num + ((Chat) value).getSupporterUnread();
                }


                Map<Object, Object> map = redisService.hmget(Constants.redis_support_delay);
                List<String> cancel=new ArrayList<>();
                Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Object, Object> entry = it.next();
                    cancel.add((String)entry.getKey());
                    Message message=   (Message)entry.getValue();
                    AdminMessage adminMessage=new AdminMessage();
                    adminMessage.setChannel(AdminMessage.channel_support);
                    adminMessage.setType(AdminMessage.type_new);
                    adminMessage.setNum(num);
                    adminMessage.setText(message.getContent());
                    String html=html(message);
                    adminMessage.setHtml(html);
                    /**
                     * 事件通知
                     */
                    messageAdminPusherService.receive(adminMessage);

                }
                redisService.hdel(Constants.redis_support_delay,(String[])cancel.toArray());


            } catch (Throwable t) {
                logger.error("[程序错误] ", t);
            } finally {
                /**
                 * 1分钟
                 */
                ThreadUtils.sleep(1000 * 60 * 1);
            }

        }

    }

    String html(Message message){
        StringBuilder  html=new StringBuilder("新的客服消息");
        html.append("\\n");
        if (Message.MSG_TYPE_TEXT.equals(message.getType())){
            if (message.getContent().length()>12) {
                html.append(message.getContent().substring(0, 12)+"...");
            }else {
                html.append(message.getContent());
            }
        }
        html.append("消息时间："+ DateTimeUtil.formatDateTimetoString(new Date(message.getCreatedTime()),DateTimeUtil.FMT_yyyyMMddHHmmss));
        return html.toString();
    }

}