package module.support.socketio;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import module.socketio.IdSession;
import module.socketio.IdSessionManager;
import module.socketio.support.MessageEventHandler;
import module.support.LiveSupportApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;



/**
 * 在线客服api端 EventHandler
 */
@Component(value = "supportMessageEventHandler")
@ConditionalOnClass(SocketIOServer.class)
public class SupportMessageEventHandler extends MessageEventHandler {

    /**
     * 聊天模块命名空间
     */
    public static final String NAMESPACE = "support";
    @Autowired
    private IdSessionManager idSessionManager;


    @Autowired
    private LiveSupportApiService liveSupportApiService;


    @Override
    @OnConnect
    public void onConnect(SocketIOClient client) {
        IdSession session = new IdSession(client);
        idSessionManager.addSession(NAMESPACE, client.getSessionId(), session);
        liveSupportApiService.connect(session);
    }

    @Override
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        idSessionManager.removeSession(NAMESPACE, client.getSessionId());
    }

    @OnEvent(value = "send")
    public void send(SocketIOClient client, AckRequest ackRequest, String data) {
        JSONObject obj = JSONObject.parseObject(data);
        String type = obj.getString("type");
        String content = obj.getString("content");
        liveSupportApiService.send(idSessionManager.getSession(NAMESPACE,client.getSessionId()),type,content);
    }


}
