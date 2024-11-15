package module.support.context;

import framework.context.InitializingLine;
import module.redis.RedisService;
import module.support.Constants;
import module.support.SupportChatService;
import module.support.model.Chat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveSupportInitializingLine implements InitializingLine {

    private final Logger logger = LoggerFactory.getLogger(LiveSupportInitializingLine.class);

    @Autowired
    RedisService redisService;

    @Autowired
    SupportChatService supportChatService;

    @Override
    public void init() {
        //清除缓存
        redisService.del(Constants.redis_support_blacklist);
        redisService.del(Constants.redis_support_chat);
        loadingToRedis();
        if (logger.isInfoEnabled()) {
            logger.info("[ live-support ] 载入redis缓存");
        }
    }

    private void loadingToRedis() {
            List<Chat> list = supportChatService.blacklist();
            if (list.size() == 0) {
                return;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < list.size(); i++) {
                Chat chat=list.get(i);
                if (chat.getBlacklist()){
                    if (chat.getPartyId()!=null){
                        map.put(chat.getPartyId().toString(), chat.getPartyId().toString());
                    }
                    if (!StringUtils.isEmpty(chat.getNoLoginId())){
                        map.put(chat.getNoLoginId(),chat.getNoLoginId());
                    }
                }
            }
            if (map.size()>0){
                redisService.hmset(Constants.redis_support_blacklist,map);
            }



        List<Chat> unread =  supportChatService.unread();
        for (int i = 0; i < unread.size(); i++) {
            Chat chat=unread.get(i);
            redisService.hset(Constants.redis_support_chat,chat.getId().toString(),chat);
        }

    }
}
