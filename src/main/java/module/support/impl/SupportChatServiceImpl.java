package module.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import module.party.Party;
import module.party.PartyService;
import module.redis.RedisService;
import module.support.Constants;
import module.support.model.Chat;
import module.support.SupportChatService;
import module.support.mapper.SupportChatMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class SupportChatServiceImpl extends ServiceImpl<SupportChatMapper, Chat> implements SupportChatService {


    @Autowired
    SupportChatMapper supportChatMapper;

    @Autowired
    PartyService partyService;

    @Autowired
    RedisService redisService;

    @Override
    public void insert(Chat entity) {
        if (entity.getPartyId()!=null){
            Party party= partyService.get(entity.getPartyId());
            entity.setUsername(party.getUsername());
            entity.setUid(party.getUsercode());
        }

        this.save(entity);
    }

    @Override
    public void modify(Chat entity) {
        this.updateById(entity);
        if (entity.getSupporterUnread()>0){
            redisService.hset(Constants.redis_support_chat,entity.getPartyId().toString(),entity);
        }else{
            redisService.hdel(Constants.redis_support_chat,entity.getPartyId().toString());
        }
    }

    @Override
    public   Chat findBy(Long partyId,String noLoginId){
        Chat chat = null;
        if (partyId != null){
            LambdaQueryWrapper<Chat> ew = new LambdaQueryWrapper<>();
            ew.eq(Chat::getPartyId, partyId);
            chat= this.getOne(ew);

        }
        if (chat==null && !StringUtils.isEmpty(noLoginId)){
            LambdaQueryWrapper<Chat> ew = new LambdaQueryWrapper<>();
            ew.eq(Chat::getNoLoginId, noLoginId);
            chat= this.getOne(ew);
        }
        if (chat!=null){
            return chat;
        }
        return null;

    }

    @Override
    public Chat findBy(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Chat> pagedQuery(Long lastTime,Integer pageSize){
        return supportChatMapper.pagedQuery(lastTime,pageSize);

    }

    @Override
    public List<Chat> pagedQuery(String params) {
        return supportChatMapper.pagedQuery(params);
    }

    @Override
    public List<Chat> unread() {
        LambdaQueryWrapper<Chat> ew = new LambdaQueryWrapper<>();
        ew.ge(Chat::getSupporterUnread, 0);
        return this.list();
    }

    @Override
    public List<Chat> blacklist() {
        LambdaQueryWrapper<Chat> ew = new LambdaQueryWrapper<>();
        ew.eq(Chat::getBlacklist, true);
        return this.list(ew);
    }

    @Override
    public Chat setRemark(Long chatId, String remarks) {
        Chat entity=this.getById(chatId);
        entity.setRemarks(remarks);
        this.updateById(entity);
        return entity;
    }

}
