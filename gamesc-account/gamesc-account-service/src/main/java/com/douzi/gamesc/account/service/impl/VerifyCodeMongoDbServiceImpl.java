package com.douzi.gamesc.account.service.impl;

import com.douzi.gamesc.account.pojo.SmsVerifyCodeRecord;
import com.douzi.gamesc.account.service.MongoDbService;
import java.util.Date;
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
public class VerifyCodeMongoDbServiceImpl implements MongoDbService<SmsVerifyCodeRecord> {

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public void saveObj(SmsVerifyCodeRecord smsVerifyCodeRecord) {
        mongoTemplate.save(smsVerifyCodeRecord);
    }

    @Override
    public List<SmsVerifyCodeRecord> findAll() {
        return mongoTemplate.findAll(SmsVerifyCodeRecord.class);
    }

    @Override
    public SmsVerifyCodeRecord getObjectById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, SmsVerifyCodeRecord.class);
    }

    @Override
    public void updateObject(SmsVerifyCodeRecord smsVerifyCodeRecord) {
        Query query = new Query(Criteria.where("_id").is(smsVerifyCodeRecord.getId()));
        Update update = new Update().set("status", smsVerifyCodeRecord.getStatus()).set("updateTime", smsVerifyCodeRecord.getUpdateTime());
        // updateFirst 更新查询返回结果集的第一条
        mongoTemplate.updateFirst(query, update, SmsVerifyCodeRecord.class);
    }

    @Override
    public void deleteObject(SmsVerifyCodeRecord smsVerifyCodeRecord) {
        mongoTemplate.remove(smsVerifyCodeRecord);
    }

    @Override
    public void deleteObjectById(String id) {
        deleteObject(getObjectById(id));
    }

    @Override
    public List<SmsVerifyCodeRecord> findByLikes(String search) {
        return null;
    }

    @Override
    public SmsVerifyCodeRecord findLastOne(Query query ,String orderFiled,String sortTag){
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
        List<SmsVerifyCodeRecord> list = mongoTemplate.find(query,SmsVerifyCodeRecord.class);
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }
        return  null;
    }
}
