package com.hs.support.context;

import com.hs.support.Constants;
import com.hs.support.SupportChatBlackList;
import com.hs.support.mapper.SupportChatBlackListMapper;
import common.web.Page;
import framework.context.InitializingLineRunner;
import framework.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupportChatBlackListInitProcessor implements InitializingLineRunner {

    private final Logger logger = LoggerFactory.getLogger(SupportChatBlackListInitProcessor.class);

    @Autowired
    RedisService redisService;

    @Autowired
    SupportChatBlackListMapper chatMessageBlackListMapper;

    @Override
    public void init() {

        //清除缓存
        redisService.del(Constants.REDIS_KEY_SUPPORT_CHAT_BLACK_LIST);

        loadSupportChatBlackListToRedis();
    }

    private void loadSupportChatBlackListToRedis()
    {
        int page_no = 1;
        while (true) {
            List<SupportChatBlackList> list = chatMessageBlackListMapper.pagedQuery(null,new Page(page_no,1000).getOffset(), 1000);
            if (list.size() == 0) {
                break;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < list.size(); i++) {
                map.put(String.valueOf(list.get(i).getIp()), list.get(i));
            }
            redisService.hmset(Constants.REDIS_KEY_SUPPORT_CHAT_BLACK_LIST,map);

            page_no++;
        }

        logger.info("[---[support]完成客服黑名单数据Redis加载---]");
    }
}
