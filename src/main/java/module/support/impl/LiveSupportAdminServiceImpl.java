package module.support.impl;

import module.message.model.AdminMessage;
import module.message.model.ApiMessage;
import module.message.MessagePusherService;
import module.redis.RedisService;
import module.socketio.IdSession;
import module.socketio.IdSessionManager;
import module.support.Constants;
import module.support.LiveSupportAdminService;

import module.support.SupportChatService;
import module.support.SupportMessageService;
import module.support.model.Chat;
import module.support.socketio.LiveSupportAdminPusherService;
import module.support.socketio.LiveSupportApiPusherService;
import module.support.socketio.LiveSupportMessageEventHandler;
import module.support.socketio.SupportMessageEventHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class LiveSupportAdminServiceImpl implements LiveSupportAdminService {
    @Autowired
    SupportChatService supportChatService;
    @Autowired
    LiveSupportAdminPusherService liveSupportAdminPusherService;

    @Autowired
    SupportMessageService supportMessageService;

    @Autowired
    LiveSupportApiPusherService liveSupportApiPusherService;

    @Autowired
    IdSessionManager idSessionManager;

    @Autowired
    RedisService redisService;

    @Autowired
    MessagePusherService messagePusherService;

    @Autowired
    private MessagePusherService messageAdminPusherService;



    @Override
    public void connect(IdSession session) {
        List<Chat> entityList = supportChatService.pagedQuery(System.currentTimeMillis(), 50);
        liveSupportAdminPusherService.user(entityList);
    }

    @Override
    public void send(Long chatid, String type, String content) {
        /**
         * 确认Chat是否存在
         */
        Chat chat = supportChatService.findBy(chatid);
        /**
         * 保存消息记录
         */
        module.support.model.Message message = new module.support.model.Message();
        message.setChatid(chat.getId());
        message.setPartyId(chat.getPartyId());
        message.setNoLoginId(chat.getNoLoginId());
        message.setDirection(module.support.model.Message.RECEIVE);
        message.setType(type);
        message.setContent(content);
        message.setCreatedTime(System.currentTimeMillis());
        supportMessageService.insert(message);

        /**
         * 更新Chat
         */
        chat.setUserUnread(chat.getUserUnread()+1);
        if (module.support.model.Message.MSG_TYPE_TEXT.equals(type)) {
            chat.setLastmsg(content);
        }
        chat.setLastTime(System.currentTimeMillis());
        supportChatService.modify(chat);

        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);

        List<module.support.model.Message> messageList = new ArrayList<>();
        messageList.add(message);
        liveSupportAdminPusherService.receive(messageList);


        //api
        IdSession idSession = null;
        if (chat.getPartyId() != null) {
            idSession = idSessionManager.getSession(SupportMessageEventHandler.NAMESPACE, chat.getPartyId());
        }

        if (idSession == null) {
            idSession = idSessionManager.getSession(SupportMessageEventHandler.NAMESPACE, chat.getNoLoginId());

        }
        if (idSession!=null){
            liveSupportApiPusherService.receive(idSession, messageList);
        }
        /**
         * API端脚标通知
         */
        ApiMessage apiMessage=new ApiMessage();
        apiMessage.setChannel(ApiMessage.channel_support);
        apiMessage.setNum(chat.getUserUnread());
        apiMessage.setPartyId(chat.getPartyId());
        apiMessage.setNoLoginId(chat.getNoLoginId());
        messagePusherService.api(null,apiMessage);


    }

    @Override
    public Long read(Long chatid) {
        Chat chat = supportChatService.findBy(chatid);
        if (chat == null) {
            return System.currentTimeMillis();
        }
        if (chat.getSupporterUnread() <= 0) {
            return chat.getSupporterReadTime();
        }
        chat.setSupporterUnread(0);
        chat.setSupporterReadTime(System.currentTimeMillis());
        supportChatService.modify(chat);


        Map<Object, Object> map = redisService.hmget(Constants.redis_support_delay);
        List<String> cancel=new ArrayList<>();
        Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            module.support.model.Message message=   (module.support.model.Message)entry.getValue();
            if (message.getChatid().equals(chatid)){
                cancel.add(String.valueOf(message.getId()));
            }
        }
        if (cancel.size()>0){
            String[] ids=cancel.toArray(new String[0]);
            for (int i = 0; i < cancel.size(); i++) {

            }
            redisService.hdel(Constants.redis_support_delay,(String[])cancel.toArray());
        }

        /**
         * 消息脚标更新
         */
        AdminMessage adminMessage=new AdminMessage();
        adminMessage.setChannel(AdminMessage.channel_support);
        adminMessage.setType(AdminMessage.type_update);
        /**
         * 未回复客服消息
         */
        Map<Object, Object> chatmap = redisService.hmget(Constants.redis_support_chat);
        int num = 0;
        for (Object value : chatmap.values()) {
            num = num + ((Chat) value).getSupporterUnread();
        }
        adminMessage.setNum(num);
        messagePusherService.admin(adminMessage);


        return chat.getSupporterReadTime();
    }

    @Override
    @Transactional
    public Chat create(Long partyId) {
        Chat chat = supportChatService.findBy(partyId, null);
        if (chat == null) {
            chat = new Chat();
            chat.setPartyId(partyId);
            chat.setLastTime(System.currentTimeMillis());
            supportChatService.insert(chat);
        }
        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);
        return chat;
    }


    @Override
    public void delmsg(Long chatId, List<Long> msgIds) {
        /**
         * 数据库操作
         */
        supportMessageService.deleteBatchById(msgIds);
        /**
         * 推送
         */
        //admin
        liveSupportAdminPusherService.delmsg(chatId, msgIds);
        //api
        Chat chat = supportChatService.findBy(chatId);
        IdSession idSession = null;
        if (chat.getPartyId() != null) {
            idSession = idSessionManager.getSession(LiveSupportMessageEventHandler.NAMESPACE, chat.getPartyId());
        }

        if (idSession == null) {
            idSession = idSessionManager.getSession(LiveSupportMessageEventHandler.NAMESPACE, chat.getNoLoginId());

        }
        if (idSession != null) {
            liveSupportApiPusherService.delmsg(idSession, msgIds);
        }


    }

    @Override
    public void setRemark(Long chatId, String remarks) {
        Chat chat = supportChatService.setRemark(chatId, remarks);
        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);


    }

    @Override
    public void addBlacklist(Long chatId) {
        Chat chat = supportChatService.findBy(chatId);
        chat.setBlacklist(true);
        supportChatService.modify(chat);
        if (chat.getPartyId()!=null){
            redisService.hset(Constants.redis_support_blacklist,chat.getPartyId().toString(),chat.getPartyId().toString());
        }
        if (!StringUtils.isEmpty(chat.getNoLoginId())){
            redisService.hset(Constants.redis_support_blacklist,chat.getNoLoginId(),chat.getNoLoginId());
        }
        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);

    }

    @Override
    public void deleteBlacklist(Long chatId) {
        Chat chat = supportChatService.findBy(chatId);
        chat.setBlacklist(false);
        supportChatService.modify(chat);
        if (chat.getPartyId()!=null){
            redisService.hdel(Constants.redis_support_blacklist,chat.getPartyId().toString());
        }
        if (!StringUtils.isEmpty(chat.getNoLoginId())){
            redisService.hdel(Constants.redis_support_blacklist,chat.getNoLoginId());
        }

        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList = new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);
    }
}
