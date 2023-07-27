package com.hs.support.socketio.Impl;

import com.hs.socketio.IdSession;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatInfoService;
import com.hs.support.SupportChatMessage;
import com.hs.support.SupportChatMessageService;
import com.hs.support.socketio.OnlineSupportSocketMessageService;
import com.hs.support.socketio.message.ReqSendMsg;
import common.util.SnowFlakeUtil;
import framework.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import party.Party;

@Service
public class OnlineSupportSocketMessageServiceImpl implements OnlineSupportSocketMessageService {

    @Autowired
    SupportChatMessageService supportChatMessageService;

    @Autowired
    SupportChatInfoService  supportChatInfoService;

    @Autowired
    RedisService redisService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SupportChatMessage createCustomerServiceMessage(ReqSendMsg message,Party party) {

        //创建消息实体
        SupportChatMessage supportChatMessage=new SupportChatMessage();
        supportChatMessage.setId(new SnowFlakeUtil().nextId());
        supportChatMessage.setCmd(message.getCmd());
        supportChatMessage.setType(message.getType());
        supportChatMessage.setContent(message.getContent());
        supportChatMessage.setCreatedTime(System.currentTimeMillis());
        supportChatMessage.setDirection(SupportChatMessage.RECEIVE_DIR);
        supportChatMessage.setResponder(party.getId());

        if(message.getPartyId()!=null)
            supportChatMessage.setPartyId(message.getPartyId());
        else
            supportChatMessage.setNoLoginId(message.getNoLoginId());


        supportChatMessageService.insert(supportChatMessage);


        return supportChatMessage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SupportChatMessage createUserChatMessage(IdSession idSession, Party party, ReqSendMsg message) {

        String ip=idSession.getClient().getHandshakeData().getAddress().getAddress().getHostAddress();
        long time=System.currentTimeMillis();

        //判断是否存在该聊天
        if(!supportChatInfoService.hasChatInfo(message.getNoLoginId(),idSession.getPartyId())){

            SupportChatInfo supportChatInfo=new SupportChatInfo();
            if(party!=null){
                supportChatInfo.setPartyId(party.getId());
                supportChatInfo.setNickName(party.getNickname());
                supportChatInfo.setAvatar(party.getAvatar());
            }
            else{
                supportChatInfo.setNoLoginId(message.getNoLoginId());
            }

            supportChatInfo.setId(new SnowFlakeUtil().nextId());
            //supportChatInfo.setAccountManagerUnreadNum(1);
            supportChatInfo.setIp(ip);
            //supportChatInfo.setLastMsg(message.getContent());
            //supportChatInfo.setLastTime(time);

            supportChatInfoService.insert(supportChatInfo);
        }

        //创建消息实体
        SupportChatMessage supportChatMessage=new SupportChatMessage();
        supportChatMessage.setId(new SnowFlakeUtil().nextId());
        supportChatMessage.setCmd(message.getCmd());
        supportChatMessage.setType(message.getType());
        supportChatMessage.setContent(message.getContent());
        supportChatMessage.setCreatedTime(time);
        supportChatMessage.setDirection(SupportChatMessage.SEND_DIR);

        if(party==null)
            supportChatMessage.setNoLoginId(message.getNoLoginId());
        else
            supportChatMessage.setPartyId(party.getId());


        supportChatMessageService.insert(supportChatMessage);


        return supportChatMessage;
    }
}
