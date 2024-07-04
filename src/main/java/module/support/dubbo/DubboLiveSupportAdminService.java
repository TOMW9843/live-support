package module.support.dubbo;

public interface DubboLiveSupportAdminService {

    /**
     * 创建用户对话
     */
    void create(Long partyId);


    /**
     * 已读回执
     */
    public Long read(Long chatId);

    /**
     * 撤回消息
     * @param msgid 可以多个，逗号隔开
     */
    void delmsg(Long chatId, String msgid);

    /**
     *设置备注
     */
    void setRemark(Long chatId,String remarks);


    /**
     *加入黑名单
     */
    void addBlacklist(Long chatId);


    /**
     *移除黑名单
     */
    void deleteBlacklist(Long chatId);
}
