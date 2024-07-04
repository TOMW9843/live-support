package module.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.support.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface SupportMessageMapper extends BaseMapper<Message> {
    List<Message> pagedQuery(@Param("partyId")Long partyId,@Param("noLoginId")String noLoginId, @Param("lastTime")Long lastTime, @Param("pageSize")Integer pageSize);
}
