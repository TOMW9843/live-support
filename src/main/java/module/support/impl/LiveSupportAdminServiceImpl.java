package module.support.impl;

import module.redis.RedisService;
import module.socketio.IdSession;
import module.socketio.IdSessionManager;
import module.support.Constants;
import module.support.LiveSupportAdminService;

import module.support.SupportChatService;
import module.support.SupportMessageService;
import module.support.model.Chat;
import module.support.model.Message;
import module.support.socketio.LiveSupportAdminPusherService;
import module.support.socketio.LiveSupportApiPusherService;
import module.support.socketio.LiveSupportMessageEventHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        Message message = new Message();
        message.setChatid(chat.getId());
        message.setPartyId(chat.getPartyId());
        message.setNoLoginId(chat.getNoLoginId());
        message.setDirection(Message.RECEIVE_DIR);
        message.setType(type);
        message.setContent(content);
        message.setCreatedTime(System.currentTimeMillis());
        supportMessageService.insert(message);

        /**
         * 更新Chat
         */
        chat.setSupporterUnread(chat.getSupporterUnread() + 1);
        if (Message.MSG_TYPE_TEXT.equals(type)) {
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

        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        liveSupportAdminPusherService.receive(messageList);
        //api
        IdSession idSession = null;
        if (chat.getPartyId() != null) {
            idSession = idSessionManager.getSession(LiveSupportMessageEventHandler.NAMESPACE, chat.getPartyId());
        }

        if (idSession == null) {
            idSession = idSessionManager.getSession(LiveSupportMessageEventHandler.NAMESPACE, chat.getNoLoginId());

        }
        liveSupportApiPusherService.receive(idSession, messageList);


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
        return chat.getSupporterReadTime();
    }

    @Override
    @Transactional
    public void create(Long partyId) {
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
