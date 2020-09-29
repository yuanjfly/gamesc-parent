package com.douzi.gamesc.advexchange.service.impl;

import com.douzi.gamesc.advexchange.service.MongoDbService;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class RedbagSendLogMongoDbServiceImpl implements MongoDbService<RedbagSendLog> {

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void saveObj(RedbagSendLog redbagSendLog) {
        mongoTemplate.save(redbagSendLog);
    }

    @Override
    public List<RedbagSendLog> findAll() {
        return mongoTemplate.findAll(RedbagSendLog.class);
    }

    @Override
    public RedbagSendLog getObjectById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, RedbagSendLog.class);
    }

    @Override
    public void updateObject(RedbagSendLog redbagSendLog) {
        Query query = new Query(Criteria.where("_id").is(redbagSendLog.getId()));
        Update update = new Update().set("isSend", redbagSendLog.getIsSend());
        // updateFirst 更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, RedbagSendLog.class);
    }

    @Override
    public void deleteObject(RedbagSendLog redbagSendLog) {
        mongoTemplate.remove(redbagSendLog);
    }

    @Override
    public void deleteObjectById(String id) {
        deleteObject(getObjectById(id));
    }

    @Override
    public List<RedbagSendLog> findByLikes(String search) {
        return null;
    }

    @Override
    public RedbagSendLog findLastOne(Query query ,String orderFiled,String sortTag){
        if(sortTag!=null){
            if(sortTag.toUpperCase().equals(Direction.ASC.name())){
                query.with(Sort.by(
                        Sort.Order.asc(orderFiled)
                ));
            }else if(sortTag.toUpperCase().equals(Direction.DESC.name())){
                query.with(Sort.by(
                        Sort.Order.desc(orderFiled)
                ));
            }
        }
        query.limit(1);
        List<RedbagSendLog> list = mongoTemplate.find(query,RedbagSendLog.class);
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }
        return  null;
    }
}
