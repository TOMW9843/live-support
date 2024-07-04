package module.support.dubbo;

public interface DubboLiveSupportService {

    /**
     * 已读回执
     * @return 已读时间戳
     */
    public Long read(Long partyId,String noLoginId);

}