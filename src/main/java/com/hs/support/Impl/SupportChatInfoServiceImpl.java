package com.hs.support.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.support.Constants;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatInfoService;
import com.hs.support.mapper.SupportChatInfoMapper;
import framework.redis.RedisService;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportChatInfoServiceImpl extends ServiceImpl<SupportChatInfoMapper, SupportChatInfo> implements SupportChatInfoService {



    @Autowired
    RedisService redisService;


    @Override
    public void read(Long partyId, String noLoginId, String role) {


    }

    @Override
    public boolean hasChatInfo(String noLoginId, Long partyId) {

        if(partyId==null)
            return redisService.hHasKey(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,noLoginId.toString());
        else
            return redisService.hHasKey(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,partyId.toString());

    }

    @Override
    public SupportChatInfo findByNoLoginId(String noLoginId) {
        Object obj=redisService.hget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,noLoginId);
        if(obj!=null)
            return (SupportChatInfo)obj;

        return null;
    }

    @Override
    public SupportChatInfo findByPartyId(Long partyId) {
        Object obj=redisService.hget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,partyId.toString());
        if(obj!=null)
            return (SupportChatInfo)obj;

        return null;
    }


    @Override
    public void insert(SupportChatInfo supportChatInfo) {

        this.save(supportChatInfo);
        if(supportChatInfo.getPartyId()==null)
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,supportChatInfo.getNoLoginId(),supportChatInfo);
        else
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,supportChatInfo.getPartyId().toString(),supportChatInfo);

    }

    @Override
    public void update(SupportChatInfo supportChatInfo) {
        if(supportChatInfo.getPartyId()==null)
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,supportChatInfo.getNoLoginId().toString(),supportChatInfo);
        else
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,supportChatInfo.getPartyId().toString(),supportChatInfo);
    }


}
