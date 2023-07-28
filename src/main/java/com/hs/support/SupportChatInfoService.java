package com.hs.support;

import java.util.List;

public interface SupportChatInfoService {


    /**
     * 是否存在该对话
     *
     * @param noLoginId 没有登录id
     * @param partyId   方id
     * @return boolean
     */
    boolean hasChatInfo(String noLoginId, Long partyId);

    SupportChatInfo findByNoLoginId(String noLoginId);

    SupportChatInfo findByPartyId(Long partyId);

    List<SupportChatInfo> findByTime(Long timestamp,int pageSize);

    void insert(SupportChatInfo supportChatInfo);

    void update(SupportChatInfo supportChatInfo);

    void setRemark(Long chatId,String remarks);
}
