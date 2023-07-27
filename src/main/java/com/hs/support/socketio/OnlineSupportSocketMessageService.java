package com.hs.support.socketio;

import com.hs.socketio.IdSession;
import com.hs.support.SupportChatMessage;
import com.hs.support.socketio.message.ReqSendMsg;
import party.Party;

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
}
