package module.support;

import module.support.model.Message;

import java.util.List;
import java.util.Map;

public interface SupportMessageService {

    /**
     * 保存
     *
     * @param entity 支持聊天消息
     */
    void insert(Message entity);
    /**
     * 聊天记录
     *
     */
    List<Message> pagedQuery(Long partyId,String noLoginId,Long lastTime,Integer pageSize);

    /**
     * 批量删除
     */
    public void deleteBatchById(List<Long> msgIds);




}
