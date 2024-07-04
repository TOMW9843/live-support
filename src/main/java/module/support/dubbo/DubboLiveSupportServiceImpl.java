package module.support.dubbo;

import module.party.PartyService;
import module.support.LiveSupportApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class DubboLiveSupportServiceImpl implements DubboLiveSupportService{

    @Autowired
    private LiveSupportApiService liveSupportApiService;

    @Override
    public Long read(Long partyId, String noLoginId) {
        return liveSupportApiService.read(partyId,noLoginId);
    }
}
