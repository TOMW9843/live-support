package com.hs.support.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.support.Constants;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatInfoService;
import com.hs.support.mapper.SupportChatInfoMapper;
import framework.redis.RedisService;
import org.apache.tomcat.util.bcel.Const;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupportChatInfoServiceImpl extends ServiceImpl<SupportChatInfoMapper, SupportChatInfo> implements SupportChatInfoService {



    @Autowired
    RedisService redisService;

    @Autowired
    SupportChatInfoMapper supportChatInfoMapper;


    @Override
    public boolean hasChatInfo(String noLoginId, Long partyId) {

        if(partyId==null)
            return redisService.hHasKey(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,noLoginId);
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
    public List<SupportChatInfo> findByTime(Long timestamp,int pageSize) {
        Map<Object,Object> queryMap=redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID);
        queryMap.putAll(redisService.hmget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID));
        List<Object> list= Arrays.asList(queryMap.values().toArray());
        if(list.size()>0){

            List<SupportChatInfo> result= list.stream()
                    .map(item-> (SupportChatInfo)item)
                    .filter(item->item.getLastTime()!=null&&item.getLastTime()<timestamp)
                    .sorted(SupportChatInfo::compareTo)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            return result;
        }

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
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,supportChatInfo.getNoLoginId(),supportChatInfo);
        else
            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,supportChatInfo.getPartyId().toString(),supportChatInfo);
    }

    @Override
    public void setRemark(Long chatId, String remarks) {

         SupportChatInfo chatInfo=this.getById(chatId);
         if(chatInfo!=null){
             chatInfo.setRemarks(remarks);
             String noLoginId=chatInfo.getNoLoginId();
             Long partyId=chatInfo.getPartyId();
             this.updateById(chatInfo);

             Object obj=null;
             if(partyId!=null)
             {
                 obj=redisService.hget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,partyId.toString());
             }else{
                 obj=redisService.hget(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,noLoginId);
             }
             if(obj!=null){
                 chatInfo=(SupportChatInfo)obj;
                 chatInfo.setRemarks(remarks);
                 if(partyId!=null)
                     redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_PARTY_ID,partyId.toString(),chatInfo);
                 else
                     redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_INFO_NO_LOGIN_ID,noLoginId,chatInfo);
             }

         }
    }

    @Override
    public List<Map<String, Object>> findUser(String parameters) {
        return supportChatInfoMapper.findUser(parameters);
    }


}
