package module.support.impl;


import module.message.model.AdminMessage;
import module.message.model.ApiMessage;
import module.message.socketio.MessageAdminPusherService;
import module.message.socketio.MessageApiPusherService;
import module.redis.RedisService;
import module.socketio.IdSession;
import module.support.Constants;
import module.support.LiveSupportApiService;
import module.support.SupportChatService;
import module.support.SupportMessageService;
import module.support.model.Chat;
import module.support.model.Message;

import module.support.socketio.LiveSupportAdminPusherService;
import module.support.socketio.LiveSupportApiPusherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private MessageAdminPusherService messageAdminPusherService;

    @Autowired
    private MessageApiPusherService messageApiPusherService;


    @Override
    public void connect(IdSession session) {
        /**
         * 推送最新消息
         */
        Chat chat = supportChatService.findBy(session.getPartyId(), session.getNoLoginId());
        if (chat==null || chat.getUserUnread() <= 0){
            return;
        }
      List<Message> entityList= supportMessageService.pagedQuery(session.getPartyId(),session.getNoLoginId(),chat.getUserReadTime(),chat.getUserUnread()+20);
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
        Message message= new Message();
        message.setChatid(chat.getId());
        message.setPartyId(session.getPartyId());
        message.setNoLoginId(session.getNoLoginId());
        message.setDirection(Message.SEND);
        message.setType(type);
        message.setContent(content);
        message.setCreatedTime(System.currentTimeMillis());
        supportMessageService.insert(message);

        /**
         * 更新Chat
         */
        chat.setSupporterUnread(chat.getSupporterUnread()+1);
        if (Message.MSG_TYPE_TEXT.equals(type)){
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
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        liveSupportAdminPusherService.receive(messageList);

        //事件通知
        liveSupportAdminPusherService.message(messageList);
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
        messageAdminPusherService.receive(adminMessage);

        //api
        liveSupportApiPusherService.receive(session, messageList);



    }

    @Override
    public Long read(Long partyId, String noLoginId) {
        Chat chat = supportChatService.findBy(partyId, noLoginId);
        if (chat==null){
            return System.currentTimeMillis();
        }

        if ( chat.getUserUnread()<=0){
            return chat.getUserReadTime();
        }
        chat.setUserUnread(0);
        chat.setUserReadTime(System.currentTimeMillis());
        supportChatService.modify(chat);

        ApiMessage message=new ApiMessage();
        message.setPartyId(partyId);
        message.setNoLoginId(noLoginId);
        message.setChannel(ApiMessage.channel_support);
        message.setNum(chat.getUserUnread());
        messageApiPusherService.receive(null,message);

        return chat.getUserReadTime();

    }


}
