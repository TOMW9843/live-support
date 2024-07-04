package module.support.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import module.redis.RedisService;
import module.support.Constants;
import module.support.model.Message;
import module.support.SupportMessageService;
import module.support.mapper.SupportMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SupportMessageServiceImpl extends ServiceImpl<SupportMessageMapper, Message> implements SupportMessageService {

    @Autowired
    SupportMessageMapper supportMessageMapper;


    @Override
    public void insert(Message entity) {
        this.save(entity);
    }


    @Override
    public List<Message> pagedQuery(Long partyId,String noLoginId,Long lastTime,Integer pageSize) {
        return supportMessageMapper.pagedQuery(partyId,noLoginId,lastTime,pageSize);
    }

    @Override
    @Transactional
    public void deleteBatchById(List<Long> msgIds) {
        if(msgIds.size()>0){
            List<Message> list= listByIds(msgIds);
            for(Message m:list){
                m.setDeleted(true);
            }
            this.updateBatchById(list);
        }
    }


}
