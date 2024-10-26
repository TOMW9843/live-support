package module.support.impl;


import boot.message.MessageHandler;
import boot.message.MessageQueue;
import module.redis.RedisService;
import module.socketio.IdSession;
import module.support.Constants;
import module.support.LiveSupportApiService;
import module.support.SupportChatService;
import module.support.SupportMessageService;
import module.support.model.Chat;

import module.support.socketio.LiveSupportAdminPusherService;
import module.support.socketio.LiveSupportApiPusherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveSupportApiServiceImpl implements LiveSupportApiService {

    @Autowired
    SupportChatService supportChatService;

    @Autowired
    SupportMessageService supportMessageService;

    @Autowired
    LiveSupportAdminPusherService liveSupportAdminPusherService;

    @Autowired
    LiveSupportApiPusherService liveSupportApiPusherService;

    @Autowired
    RedisService redisService;

    @Autowired
    private MessageHandler messageHandler;


    @Override
    public void connect(IdSession session) {
        /**
         * 推送最新消息
         */
        Chat chat = supportChatService.findBy(session.getPartyId(), session.getNoLoginId());
        if (chat==null || chat.getUserUnread() <= 0){
            return;
        }
      List<module.support.model.Message> entityList= supportMessageService.pagedQuery(session.getPartyId(),session.getNoLoginId(),chat.getUserReadTime(),chat.getUserUnread()+20);
        if (entityList.size()>0){
            /**
             * api端推送
             */
            liveSupportApiPusherService.receive(session,entityList);
        }

    }

    @Override
    @Transactional
    public void send(IdSession session, String type, String content) {

        if (StringUtils.isEmpty(content)){
            return;
        }

        /**
         * 确认Chat是否存在
         */
        Chat chat = supportChatService.findBy(session.getPartyId(), session.getNoLoginId());
        if (chat == null) {
            chat = new Chat();
            chat.setPartyId(session.getPartyId());
            chat.setNoLoginId(session.getNoLoginId());
            supportChatService.insert(chat);
        }
        /**
         * 黑名单
         */
        if (chat.getPartyId()!=null){
           Object object= redisService.hget(Constants.redis_support_blacklist,chat.getPartyId().toString());
           if (object!=null){
               return;
           }
        }else  if (!StringUtils.isEmpty(chat.getNoLoginId())){
            Object object= redisService.hget(Constants.redis_support_blacklist,chat.getNoLoginId());
            if (object!=null){
                return;
            }
        }


        /**
         * 保存消息记录
         */
        module.support.model.Message message= new module.support.model.Message();
        message.setChatid(chat.getId());
        message.setPartyId(session.getPartyId());
        message.setNoLoginId(session.getNoLoginId());
        message.setDirection(module.support.model.Message.SEND);
        message.setType(type);
        message.setContent(content);
        message.setCreatedTime(System.currentTimeMillis());
        supportMessageService.insert(message);

        /**
         * 更新Chat
         */
        chat.setSupporterUnread(chat.getSupporterUnread()+1);
        if (module.support.model.Message.MSG_TYPE_TEXT.equals(type)){
            chat.setLastmsg(content);
        }
        chat.setLastTime(System.currentTimeMillis());
        supportChatService.modify(chat);

        /**
         * 推送消息
         */
        //admin
        List<Chat> chatList=new ArrayList<>();
        chatList.add(chat);
        liveSupportAdminPusherService.user(chatList);
        List<module.support.model.Message> messageList = new ArrayList<>();
        messageList.add(message);
        liveSupportAdminPusherService.receive(messageList);


        //提示推送
        Map<String, Object> data = new HashMap<>();
        messageHandler.admin(MessageQueue.cmdnew, MessageQueue.support, data);

        //api
        liveSupportApiPusherService.receive(session, messageList);



    }

    @Override
    public Long read(Long partyId, String noLoginId) {
        Chat chat = supportChatService.findBy(partyId, noLoginId);
        if (chat==null){
            return System.currentTimeMillis();
        }

        chat.setUserUnread(0);
        chat.setUserReadTime(System.currentTimeMillis());
        supportChatService.modify(chat);

        messageHandler.api(MessageQueue.cmdupdate, MessageQueue.support,chat.getPartyId(), chat.getNoLoginId(),  null);

        return chat.getUserReadTime();

    }


}
