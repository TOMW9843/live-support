package com.hs.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hs.support.SupportChatBlackList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SupportChatBlackListMapper extends BaseMapper<SupportChatBlackList> {

    List<SupportChatBlackList> pagedQuery(@Param("ip")String ip,@Param("offset")int offset, @Param("pageSize")int pageSize);
    
    List<?> pagedQuery1(@Param("ip")String ip,@Param("offset")int offset, @Param("pageSize")int pageSize);
}
