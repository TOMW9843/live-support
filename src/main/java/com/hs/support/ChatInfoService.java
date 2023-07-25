package com.hs.support;

public interface ChatInfoService {

    /**
     * 已读
     * @param role user-用户  AM-客服
     */
    public void read(Long partyId,Long noLoginId,String role);
}
