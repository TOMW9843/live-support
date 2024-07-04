package module.support.dubbo;

import module.support.LiveSupportAdminService;
import module.support.LiveSupportApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
@DubboService
public class DubboLiveSupportAdminServiceImpl implements DubboLiveSupportAdminService{

    @Autowired
    private LiveSupportAdminService liveSupportAdminService;
    @Override
    public void create(Long partyId) {
        liveSupportAdminService.create(partyId);
    }

    @Override
    public Long read(Long chatId) {
        return liveSupportAdminService.read(chatId);
    }

    @Override
    public void delmsg(Long chatId, String msgid) {
        String[] temp=msgid.split(",");
        List<Long> msgIds= new ArrayList<>();
        for (int i = 0; i < temp.length; i++) {
            msgIds.add(Long.valueOf(temp[i]));
        }
        liveSupportAdminService.delmsg(chatId,msgIds);
    }

    @Override
    public void setRemark(Long chatId, String remarks) {
        liveSupportAdminService.setRemark(chatId,remarks);
    }

    @Override
    public void addBlacklist(Long chatId) {
        liveSupportAdminService.addBlacklist(chatId);
    }

    @Override
    public void deleteBlacklist(Long chatId) {
        liveSupportAdminService.deleteBlacklist(chatId);
    }
}
