package module.support.socketio.impl;

import module.socketio.IdSession;
import module.socketio.MessagePusher;
import module.socketio.support.ResDefault;
import module.support.model.Message;
import module.support.socketio.LiveSupportApiPusherService;
import module.support.socketio.LiveSupportMessageEventHandler;
import module.support.socketio.SupportMessageEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveSupportApiPusherServiceImpl implements LiveSupportApiPusherService {
    @Autowired
    private MessagePusher messagePusher;
    @Override
    public void receive(IdSession session, List<Message> entityList) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            Message message = entityList.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("chatid", message.getChatid());
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
        messagePusher.pushMessage(SupportMessageEventHandler.NAMESPACE, "receive",session.getClient().getSessionId(), packet);
    }

    @Override
    public void delmsg(IdSession session, List<Long> msgIds) {
        Map<String, Object> data = new HashMap<>();
        data.put("msgids",msgIds);

        ResDefault packet = new ResDefault();
        packet.setData(data);
        messagePusher.pushMessage(LiveSupportMessageEventHandler.NAMESPACE, "delmsg",session.getClient().getSessionId(), packet);
    }
}
