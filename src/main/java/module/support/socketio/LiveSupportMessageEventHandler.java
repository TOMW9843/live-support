package module.support.socketio;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import module.security.ResourceMatcher;
import module.security.interceptor.Authorization;
import module.security.token.SecurityTokenService;
import module.security.token.Token;
import module.socketio.IdSession;
import module.socketio.support.MessageEventHandler;
import module.support.LiveSupportAdminService;

import module.socketio.IdSessionManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 在线客服admin端 EventHandler
 */
@Component(value = "liveSupportMessageEventHandler")
@ConditionalOnClass(SocketIOServer.class)
public class LiveSupportMessageEventHandler extends MessageEventHandler {

    /**
     * 聊天模块命名空间
     */
    public static final String NAMESPACE = "liveSupport";
    @Autowired
    private IdSessionManager idSessionManager;

    @Autowired
    private SecurityTokenService securityTokenService;

    @Autowired
    private ResourceMatcher resourceMatcher;

    @Autowired
    private LiveSupportAdminService liveSupportAdminService;


    @Override
    @OnConnect
    public void onConnect(SocketIOClient client) {
        IdSession session = new IdSession(client);
        String auth = client.getHandshakeData().getHttpHeaders().get("auth");
        if (!StringUtils.isEmpty(auth)) {
            Token token = securityTokenService.find(auth);
            if (token != null) {
                /**
                 * 判断是否有权限
                 */
                Authorization authorization = new Authorization();
                authorization.setToken(auth);
                authorization.setPartyId(token.getPartyId());
                List<String> codes = new ArrayList<>();
                codes.add("601");//配资持仓单 菜单code

                Map<String, Integer> match = resourceMatcher.elementMatch(authorization, codes);
                if (match.get("601") == 1) {
                    idSessionManager.addSession(NAMESPACE, client.getSessionId(), session);
                    liveSupportAdminService.connect(session);
                }
            }
        }
    }

    @Override
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        idSessionManager.removeSession(NAMESPACE, client.getSessionId());
    }

    @OnEvent(value = "send")
    public void send(SocketIOClient client, AckRequest ackRequest, String data) {
        JSONObject obj = JSONObject.parseObject(data);
        Long chatid=obj.getLong("chatid");
        String type=obj.getString("type");
        String content=obj.getString("content");
        liveSupportAdminService.send(chatid,type,content);
    }

    @OnEvent(value = "read")
    public void read(SocketIOClient client, AckRequest ackRequest, String data) {
        JSONObject obj = JSONObject.parseObject(data);
        Long chatid=obj.getLong("chatid");
        liveSupportAdminService.read(chatid);
    }



}
