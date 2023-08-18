package com.hs.support.socketio;

import com.hs.socketio.IdSession;
import com.hs.support.SupportChatInfo;
import com.hs.support.SupportChatMessage;
import com.hs.support.model.SupportChatInfoUserList;
import com.hs.support.socketio.message.ReqSendMsg;
import party.Party;

import java.util.List;
import java.util.Map;

public interface OnlineSupportSocketMessageService {


    /**
     * 创建客服回复消息
     *
     * @param reqSendMsg 请求发送味精
     * @return {@link SupportChatMessage}
     */
    SupportChatMessage  createCustomerServiceMessage(ReqSendMsg reqSendMsg,Party party);


    /**
     * 创建用户聊天信息
     *
     * @param idSession  会话id
     * @param party      方
     * @param reqSendMsg 请求发送味精
     * @return {@link SupportChatMessage}
     */
    SupportChatMessage  createUserChatMessage(IdSession idSession,Party party,ReqSendMsg reqSendMsg);


    /**
     * 创建系统消息
     *
     * @param idSession 没有登录id
     * @param party     方
     * @return {@link SupportChatMessage}
     */
    SupportChatMessage createSystemChatMessage(IdSession idSession, Party party,String msgType);


    /**
     * 客服创建聊天消息
     *
     * @param partyId 方id
     */
    void customerServiceCreateChatInfo(Long partyId) throws Exception;


    SupportChatInfo systemCreateChatInfo(Long partyId, String noLoginId, String ip);

    /**
     * 用户列表
     *
     * @param timestamp 时间戳
     * @param pageSize 每页多少个
     * @return {@link List}<{@link SupportChatInfoUserList}>
     * @throws Exception 异常
     */
    List<SupportChatInfoUserList> chatInfoUserList(Long timestamp,int pageSize) throws Exception;

}
