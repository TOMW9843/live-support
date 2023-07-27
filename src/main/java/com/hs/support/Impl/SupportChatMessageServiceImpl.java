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

@Service
public class SupportChatMessageServiceImpl extends ServiceImpl<SupportChatMessageMapper, SupportChatMessage> implements SupportChatMessageService {


    @Autowired
    RedisService redisService;

    @Override
    public void send(SupportChatMessage supportChatMessage) {

    }

    @Override
    public void insert(SupportChatMessage supportChatMessage) {

        this.save(supportChatMessage);
        if (supportChatMessage.getDirection().equals(SupportChatMessage.SEND_DIR)) {
            if (supportChatMessage.getPartyId() == null) {
                redisService.rpush(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + supportChatMessage.getNoLoginId(), supportChatMessage);
            } else {
                redisService.rpush(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID + supportChatMessage.getPartyId(), supportChatMessage);
            }

        } else {
            if (supportChatMessage.getPartyId() == null) {
                redisService.rpush(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + supportChatMessage.getNoLoginId(), supportChatMessage);
            } else {
                redisService.rpush(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + supportChatMessage.getPartyId(), supportChatMessage);
            }

        }

    }


    @Override
    public List<SupportChatMessage> unReadMsg(String noLoginId, Long partyId, String direction) {

        List<Object> list = null;
        if (direction.equals(SupportChatMessage.SEND_DIR)) {
            if (partyId == null)
                list = redisService.lGet(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_NO_LOGIN_ID + noLoginId, 0, -1);
            else
                list = redisService.lGet(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_USER_PARTY_ID + partyId, 0, -1);
        } else {
            if (partyId == null)
                list = redisService.lGet(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_NO_LOGIN_ID + noLoginId, 0, -1);
            else
                list = redisService.lGet(Constants.REDIS_KEY_SUPPORT_CHAT_MESSAGE_CS_PARTY_ID + partyId, 0, -1);

        }

        if (list == null || list.size() < 1)
            return null;

        List<SupportChatMessage> result = new ArrayList<>();
        for (Object o : list) {
            result.add((SupportChatMessage) o);
        }
        return result;

    }

    @Override
    public void readMsg(String noLoginId, Long partyId, String direction) {

    }


}
