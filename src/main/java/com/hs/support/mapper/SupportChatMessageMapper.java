package com.hs.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.support.SupportChatMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SupportChatMessageMapper extends BaseMapper<SupportChatMessage> {

    List<Map<String,Object>> pagedQuery(@Param("noLoginId")String noLoginId, @Param("partyId")Long partyId, @Param("timestamp")Long timestamp);
}
