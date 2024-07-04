package module.support.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.support.model.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupportChatMapper extends BaseMapper<Chat> {

    List<Chat> pagedQuery(@Param("lastTime")Long lastTime, @Param("pageSize")Integer pageSize);
}
