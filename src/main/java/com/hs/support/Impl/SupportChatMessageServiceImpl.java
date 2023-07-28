package com.hs.support.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.support.Constants;
import com.hs.support.SupportChatMessage;
import com.hs.support.SupportChatMessageService;
import com.hs.support.mapper.SupportChatMessageMapper;
import framework.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SupportChatMessageServiceImpl extends ServiceImpl<SupportChatMessageMapper, SupportChatMessage> implements SupportChatMessageService {


    @Autowired
    RedisService redisService;

    @Autowired
    SupportChatMessageMapper supportChatMessageMapper;

    @Override
    public void send(SupportChatMessage supportChatMessage) {

    }

    @Override
    public void insert(SupportChatMessage supportChatMessage) {

        this.save(supportChatMessage);
        if (supportChatMessage.getDirection().equals(SupportChatMessage.SEND_DIR)) {
            if (supportChatMessage.getPartyId() == null) {
                redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + supportChatMessage.getNoLoginId(),supportChatMessage.getId().toString(),supportChatMessage);
            } else {
                redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID + supportChatMessage.getPartyId(),supportChatMessage.getId().toString(), supportChatMessage);
            }

        } else {
            if (supportChatMessage.getPartyId() == null) {
                redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + supportChatMessage.getNoLoginId(), supportChatMessage.getId().toString(),supportChatMessage);
            } else {
                redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + supportChatMessage.getPartyId(), supportChatMessage.getId().toString(),supportChatMessage);
            }

        }

    }


    @Override
    public List<SupportChatMessage> unReadMsg(String noLoginId, Long partyId, String direction) {

        Map<Object,Object> list = null;
        if (direction.equals(SupportChatMessage.SEND_DIR)) {
            if (partyId == null)
                list = redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + noLoginId);
            else
                list = redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID + partyId);
        } else {
            if (partyId == null)
                list = redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + noLoginId);
            else
                list = redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + partyId);

        }

        if (list == null || list.size() < 1)
            return null;

        List<SupportChatMessage> result = new ArrayList<>();
        for (Map.Entry<Object,Object> o : list.entrySet()) {
            result.add((SupportChatMessage) o.getValue());
        }
        return result;

    }

    @Override
    public void readMsg(String noLoginId, Long partyId, String direction) {

        if (direction.equals(SupportChatMessage.SEND_DIR)) {
            if (partyId == null) {
                redisService.del(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + noLoginId);
            } else {
                redisService.del(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID +partyId);
            }

        } else {
            if (partyId==null) {
                redisService.del(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + noLoginId);
            } else {
                redisService.del(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + partyId);
            }

        }
    }

    @Override
    public List<Map<String, Object>> history(String noLoginId, Long partyId, Long timestamp) {
        return supportChatMessageMapper.pagedQuery(noLoginId,partyId,timestamp);
    }

    @Override
    public void delMsg(List<String> msgIds) {

        if(msgIds.size()>0){
            List<SupportChatMessage> list= listByIds(msgIds);
            for(SupportChatMessage m:list){
                m.setDeleted(true);
            }
            this.updateBatchById(list);

            //删除未读消息
            for(SupportChatMessage m:list){
                if (m.getDirection().equals(SupportChatMessage.SEND_DIR)) {
                    if (m.getPartyId() == null) {
                        redisService.hdel(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + m.getNoLoginId(),m.getId().toString());
                    } else {
                        redisService.hdel(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID +m.getPartyId(),m.getId().toString());
                    }

                } else {
                    if (m.getPartyId()==null) {
                        redisService.hdel(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + m.getNoLoginId(),m.getId().toString());
                    } else {
                        redisService.hdel(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + m.getPartyId(),m.getId().toString());
                    }

                }
            }

        }

    }


}
