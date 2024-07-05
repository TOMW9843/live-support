package module.support;

import module.support.model.Chat;
import module.support.model.Message;

import java.util.List;
import java.util.Map;

public interface SupportChatService {

    /**
     * 新建
     */
    void insert(Chat entity);
    /**
     * 更新
     */
    void modify(Chat entity);

    Chat findBy(Long partyId,String noLoginId);
    Chat findBy(Long id);

    /**
     * 对话列表
     *
     */
    List<Chat> pagedQuery(Long lastTime,Integer pageSize);

    List<Chat> pagedQuery(String params);

    /**
     * 所有黑名单记录
     */
    List<Chat> blacklist();


    /**
     *设置备注
     */
    Chat setRemark(Long chatId,String remarks);


}
