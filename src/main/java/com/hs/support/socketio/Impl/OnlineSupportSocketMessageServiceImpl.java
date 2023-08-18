package com.hs.support.socketio.Impl;

import com.alibaba.fastjson.JSONObject;
import com.hs.socketio.IdSession;
import com.hs.support.*;
import com.hs.support.model.SupportChatInfoUserList;
import com.hs.support.socketio.OnlineSupportSocketMessageService;
import com.hs.support.socketio.OnlineSupportSocketPusherService;
import com.hs.support.socketio.message.ReqSendMsg;
import com.hs.syspara.SysparaService;
import common.exception.BusinessException;
import common.util.SnowFlakeUtil;
import framework.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import party.Party;
import party.PartyService;
import security.SecUserService;
import security.model.SecUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OnlineSupportSocketMessageServiceImpl implements OnlineSupportSocketMessageService {

    private static final Logger logger = LoggerFactory.getLogger(OnlineSupportSocketMessageService.class);

    @Autowired
    SupportChatMessageService supportChatMessageService;

    @Autowired
    SupportChatInfoService supportChatInfoService;

    @Autowired
    RedisService redisService;

    @Autowired
    PartyService partyService;

    @Autowired
    SecUserService secUserService;

    @Autowired
    SysparaService sysparaService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SupportChatMessage createCustomerServiceMessage(ReqSendMsg message, Party party) {

        //创建消息实体
        SupportChatMessage supportChatMessage = new SupportChatMessage();
        supportChatMessage.setId(new SnowFlakeUtil().nextId());
        supportChatMessage.setCmd(message.getCmd());
        supportChatMessage.setType(message.getType());
        if (message.getType().equals(SupportChatMessage.MSG_TYPE_TEXT))
            supportChatMessage.setContent(message.getContent());
        else if (message.getType().equals(SupportChatMessage.MSG_TYPE_IMG))
            supportChatMessage.setImg(message.getContent());
        supportChatMessage.setCreatedTime(System.currentTimeMillis());
        supportChatMessage.setDirection(SupportChatMessage.RECEIVE_DIR);
        supportChatMessage.setResponder(party.getId());

        if (message.getPartyId() != null)
            supportChatMessage.setPartyId(message.getPartyId());
        else
            supportChatMessage.setNoLoginId(message.getNoLoginId());


        supportChatMessageService.insert(supportChatMessage);


        return supportChatMessage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SupportChatMessage createUserChatMessage(IdSession idSession, Party party, ReqSendMsg message) {

        String ip = idSession.getClient().getHandshakeData().getAddress().getAddress().getHostAddress();
        long time = System.currentTimeMillis();


        //判断是否存在该聊天
        createChatInfo(party,message.getNoLoginId(),ip);


        //创建消息实体
        SupportChatMessage supportChatMessage = new SupportChatMessage();
        supportChatMessage.setId(new SnowFlakeUtil().nextId());
        supportChatMessage.setCmd(message.getCmd());
        supportChatMessage.setType(message.getType());
        if (message.getType().equals(SupportChatMessage.MSG_TYPE_TEXT))
            supportChatMessage.setContent(message.getContent());
        else if (message.getType().equals(SupportChatMessage.MSG_TYPE_IMG))
            supportChatMessage.setImg(message.getContent());
        supportChatMessage.setCreatedTime(time);
        supportChatMessage.setDirection(SupportChatMessage.SEND_DIR);

        if (party == null)
            supportChatMessage.setNoLoginId(message.getNoLoginId());
        else
            supportChatMessage.setPartyId(party.getId());


        supportChatMessageService.insert(supportChatMessage);


        return supportChatMessage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SupportChatMessage createSystemChatMessage(IdSession idSession, Party party,String msgType) {

        String content=getContentByMsgType(msgType);

        if(StringUtils.isEmpty(content)){

            logger.warn("[supportchat]系统消息未找到，类型{}",msgType);
            return null;
        }

        long time = System.currentTimeMillis();

        //创建消息实体
        SupportChatMessage supportChatMessage = new SupportChatMessage();
        supportChatMessage.setId(new SnowFlakeUtil().nextId());
        supportChatMessage.setCmd(SupportChatMessage.MSG_CMD_SYSTEM);
        supportChatMessage.setType(SupportChatMessage.MSG_TYPE_TEXT);
        supportChatMessage.setContent(content);
        supportChatMessage.setCreatedTime(time);
        supportChatMessage.setDirection(SupportChatMessage.RECEIVE_DIR);
        if (party == null)
            supportChatMessage.setNoLoginId(idSession.getNoLoginId());
        else
            supportChatMessage.setPartyId(party.getId());

        supportChatMessageService.insert(supportChatMessage);


        return supportChatMessage;
    }


    /**
     * 创建聊天信息
     *
     * @param party     方
     * @param noLoginId 没有登录id
     * @param ip        知识产权
     */
    private SupportChatInfo createChatInfo(Party party, String noLoginId, String ip){

        Long partyId=party==null ? null : party.getId();
        //判断是否存在该聊天
        if(!supportChatInfoService.hasChatInfo(noLoginId, partyId)){

            SupportChatInfo supportChatInfo = new SupportChatInfo();
            supportChatInfo.setId(new SnowFlakeUtil().nextId());
            supportChatInfo.setIp(ip);

            if(party!=null){
                supportChatInfo.setPartyId(party.getId());
                supportChatInfo.setNickName(party.getNickname());
                supportChatInfo.setAvatar(party.getAvatar());
            }else{

                supportChatInfo.setNoLoginId(noLoginId);
            }


            supportChatInfoService.insert(supportChatInfo);

            return supportChatInfo;

        }

        return null;
    }

    @Override
    public void customerServiceCreateChatInfo(Long partyId) throws Exception {
        Party party = partyService.get(partyId);
        if (party == null)
            throw new BusinessException("400", "用户不存在");

        SecUser user = secUserService.findByPartyId(party.getId());

        createChatInfo(party,null,user.getLoginIp());

    }

    @Override
    public SupportChatInfo systemCreateChatInfo(Long partyId, String noLoginId,String ip) {
        Party party=partyService.get(partyId);
        return createChatInfo(party,noLoginId,ip);
    }


    @Override
    public List<SupportChatInfoUserList> chatInfoUserList(Long timestamp, int pageSize) throws Exception {

        List<SupportChatInfo> chatInfos = supportChatInfoService.findByTime(timestamp, pageSize);

        if (chatInfos != null) {
            List<SupportChatInfoUserList> result = new ArrayList<>();

            for (SupportChatInfo info : chatInfos) {
                SupportChatInfoUserList item = new SupportChatInfoUserList();
                item.setChatId(info.getId().toString());
                item.setAvatar(info.getAvatar());
                item.setIp(info.getIp());
                item.setLastMsg(info.getLastMsg());
                item.setLastTime(info.getLastTime());
                item.setNoLoginId(info.getNoLoginId());
                item.setPartyId(info.getPartyId() != null ? info.getPartyId().toString() : null);
                item.setUnReadNum(info.getAccountManagerUnreadNum());
                item.setNickName(info.getNickName());
                item.setRemarks(info.getRemarks());

                result.add(item);
            }

            return result;
        }
        return null;
    }


    /**
     * 获取系统消息文本
     *
     * @param msgType msg类型
     */
    private String getContentByMsgType(String msgType){

        if(msgType.equals(Constants.SYSTEM_MSG_WELCOME)){
            String jsonStr=sysparaService.find("support_chat_welcome_msg").getValue();
            Map<String,String> contentMap= JSONObject.toJavaObject(JSONObject.parseObject(jsonStr),Map.class);
            return contentMap.get("EN");
        }

        return null;
    }

}
