package com.hs.support;

public interface SupportChatInfoService {

    /**
     * 已读
     * @param role user-用户  AM-客服
     */
    void read(Long partyId,String noLoginId,String role);


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


    void insert(SupportChatInfo supportChatInfo);

    void update(SupportChatInfo supportChatInfo);
}
