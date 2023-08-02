package com.hs.support.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hs.support.Constants;
import com.hs.support.SupportChatBlackList;
import com.hs.support.SupportChatBlackListService;
import com.hs.support.SupportChatMessageService;
import com.hs.support.mapper.SupportChatBlackListMapper;
import common.web.Page;
import framework.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupportChatBlackListServiceImpl extends ServiceImpl<SupportChatBlackListMapper,SupportChatBlackList> implements SupportChatBlackListService {


    @Autowired
    RedisService redisService;

    @Autowired
    SupportChatBlackListMapper supportChatBlackListMapper;

    @Override
    public Map<String, Object> blackList(String ip, int pageIndex, int pageSize) throws Exception {
        List<?> list=supportChatBlackListMapper.pagedQuery1(ip,new Page(pageIndex,pageSize).getOffset(),pageSize);
        Map<String,Object> map=new HashMap<>();
        map.put("info",list);
        map.put("pageIndex",pageIndex);
        map.put("pageSize",list.size());
        return map;
    }

    @Override
    public void addBlackList(String ip,String noLoginId, String remarks) throws Exception {

        Object obj=redisService.hget(Constants.REDIS_KEY_SUPPORT_CHAT_BLACK_LIST,ip+noLoginId);
        if(obj==null){

            SupportChatBlackList msg=new SupportChatBlackList();
            msg.setIp(ip);
            msg.setNoLoginId(noLoginId);
            msg.setRemarks(remarks);
            this.save(msg);

            redisService.hset(Constants.REDIS_KEY_SUPPORT_CHAT_BLACK_LIST,ip+noLoginId,msg);

        }
    }

    @Override
    public void removeBlackList(String id) throws Exception {
        SupportChatBlackList item=this.getById(id);

        if(item!=null){
            this.removeById(id);
            redisService.hdel(Constants.REDIS_KEY_SUPPORT_CHAT_BLACK_LIST,item.getIp()+item.getNoLoginId());
        }

    }
}
