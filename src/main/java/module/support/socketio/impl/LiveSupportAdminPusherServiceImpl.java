package module.support.socketio.impl;

import module.redis.RedisService;
import module.socketio.support.ResDefault;
import module.support.Constants;
import module.support.model.Chat;
import module.support.model.Message;
import module.support.socketio.LiveSupportAdminPusherService;
import module.support.socketio.LiveSupportMessageEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import module.socketio.MessagePusher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveSupportAdminPusherServiceImpl implements LiveSupportAdminPusherService {
    @Autowired
    private MessagePusher messagePusher;

    @Autowired
    private RedisService redisService;

    @Override
    public void user(List<Chat> entityList) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            Chat chat = entityList.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("chatid", chat.getId().toString());
            if ( chat.getPartyId()!=null){
                item.put("partyid", chat.getPartyId().toString());
            }else{
                item.put("partyid", "");
            }
            item.put("nologinid", chat.getNoLoginId());
            item.put("username", chat.getUsername());
            item.put("unread", chat.getSupporterUnread());
            item.put("lastmsg", chat.getLastmsg());
            item.put("lasttime", chat.getLastTime());
            item.put("readtime", chat.getSupporterReadTime());
            item.put("blacklist", chat.getBlacklist());
            item.put("remarks", chat.getRemarks());
            data.add(item);
        }

        if (data.size() == 0) {
            return;
        }
        ResDefault packet = new ResDefault();
        packet.setData(data);
        messagePusher.pushMessage(LiveSupportMessageEventHandler.NAMESPACE, "user", packet);

    }

    @Override
    public void receive(List<Message> entityList) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            Message message = entityList.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("chatid", message.getChatid().toString());
            item.put("msgid", message.getId().toString());
            item.put("direction", message.getDirection());
            item.put("type", message.getType());
            item.put("content", message.getContent());
            item.put("time", message.getCreatedTime());
            data.add(item);
        }
        if (data.size() == 0) {
            return;
        }
        ResDefault packet = new ResDefault();
        packet.setData(data);
        messagePusher.pushMessage(LiveSupportMessageEventHandler.NAMESPACE, "receive", packet);
    }



    @Override
    public void delmsg(Long chatId,List<Long> msgIds) {
        Map<String, Object> data = new HashMap<>();
        data.put("chatid",chatId);
        data.put("msgid",msgIds);

        ResDefault packet = new ResDefault();
        packet.setData(data);
        messagePusher.pushMessage(LiveSupportMessageEventHandler.NAMESPACE, "delmsg", packet);
    }
}
