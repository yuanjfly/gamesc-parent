package com.douzi.gamesc.advexchange.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.redbagbalance.ads.RebagBalanceStrategy;
import com.douzi.gamesc.advexchange.redbagbalance.concrete.AliPayBalanceStrategy;
import com.douzi.gamesc.advexchange.redbagbalance.concrete.AppBalanceStrategy;
import com.douzi.gamesc.advexchange.redbagbalance.concrete.PhoneBillBalanceStrategy;
import com.douzi.gamesc.advexchange.redbagbalance.concrete.QuttBalanceStrategy;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.advexchange.mapper.ExchangeBindUserMapper;
import com.douzi.gamesc.advexchange.mapper.ExchangeRedbagRecordMapper;
import com.douzi.gamesc.advexchange.service.MongoDbService;
import com.douzi.gamesc.advexchange.service.RedBagBalanceService;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.additional.aggregation.AggregateCondition;
import tk.mybatis.mapper.additional.aggregation.AggregateType;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Slf4j
public class RedBagBalanceServiceImpl implements RedBagBalanceService {

    @Autowired
    private ExchangeBindUserMapper exchangeBindUserMapper;
    @Autowired
    private ExchangeRedbagRecordMapper exchangeRedbagRecordMapper;
    @Autowired
    private MongoDbService<RedbagSendLog> redbagSendLogMongoDbServiceImpl;

    // 拥有一个出行策略引用
    private List<RebagBalanceStrategy> strategylist;

    public RedBagBalanceServiceImpl() {
        this.strategylist = new ArrayList<>();
        strategylist.add(new AppBalanceStrategy());
        strategylist.add(new AliPayBalanceStrategy());
        strategylist.add(new PhoneBillBalanceStrategy());
        strategylist.add(new QuttBalanceStrategy());
    }


    @Override
    public int queryDayTotalByUserId(long userId){
        Example example = new Example(ExchangeRedbagRecord.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andCondition("TO_DAYS(create_time) = TO_DAYS(NOW())");
        AggregateCondition condition = new  AggregateCondition("money",AggregateType.SUM).aliasName("money");
        List<ExchangeRedbagRecord> redBagDayTotal =  exchangeRedbagRecordMapper.selectAggregationByExample(example,condition);
        if(redBagDayTotal!=null&&redBagDayTotal.size()>0&&redBagDayTotal.get(0)!=null){
            return redBagDayTotal.get(0).getMoney()==null?0:redBagDayTotal.get(0).getMoney();
        }
        return 0;
    }

    @Override
    public int queryAllTotalByUserId(long userId){
        Example example = new Example(ExchangeRedbagRecord.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        AggregateCondition condition = new  AggregateCondition("money",AggregateType.SUM).aliasName("money");
        List<ExchangeRedbagRecord> redBagDayTotal =  exchangeRedbagRecordMapper.selectAggregationByExample(example,condition);
        if(redBagDayTotal!=null&&redBagDayTotal.size()>0&&redBagDayTotal.get(0)!=null){
            return redBagDayTotal.get(0).getMoney()==null?0:redBagDayTotal.get(0).getMoney();
        }
        return 0;
    }

    @Override
    public  ExchangeBindUser getExchangeBindUser(String appId,long userId){
        Example example = new Example(ExchangeBindUser.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("appId",appId);
        return exchangeBindUserMapper.selectOneByExample(example);
    }

    @Override
    public ExchangeBindUser saveExchangeBindUser(ExchangeBindUser bindUser){
        if(bindUser.getId()==null||bindUser.getId()<=0){
            exchangeBindUserMapper.insertSelective(bindUser);
        }else{
            exchangeBindUserMapper.updateByPrimaryKeySelective(bindUser);
        }
        return  bindUser;
    }

    @Override
    public  ExchangeBindUser getExchangeBindOpenId(String productId,String appId,String openId){
        Example example = new Example(ExchangeBindUser.class);
        example.createCriteria().andEqualTo("openId",openId).andEqualTo("appId",appId).andEqualTo("productId",productId);
        return exchangeBindUserMapper.selectOneByExample(example);
    }

    @Override
    public ExchangeRedbagRecord getExchangeRedbagRecord(ExchangeRedbagRecord redbag){
        return exchangeRedbagRecordMapper.selectOne(redbag);
    }

    @Override
    public  ExchangeRedbagRecord saveExchangeRedbagRecord(ExchangeRedbagRecord redbag){
        if(redbag.getId()==null||redbag.getId()<=0){
            exchangeRedbagRecordMapper.insertSelective(redbag);
        }else{
            exchangeRedbagRecordMapper.updateByPrimaryKeySelective(redbag);
        }
        return  redbag;
    }

    @Override
    public JSONObject sendRedBagByBindUser(ExchangeRedbagRecord redbag,JSONObject redBagCfg){
        log.info("发送红包开始，用户==="+redbag.getUserId()+"===发送方式=="+redbag.getType());
        JSONObject result = null;
        // 根据具体策略类，执行对应的出行策略
        for (RebagBalanceStrategy balancelStrategy : strategylist) {
            if (balancelStrategy.isOK(redbag.getType())) {
                result = new JSONObject() ;
                RedbagSendLog redbagSendlog =  balancelStrategy.balanceOutWay(redbag,redBagCfg);
                if(redbagSendlog.getIsSend()!=1){
                    result.put("msg",redbagSendlog.getSendReson());
                }
                //插入mogodb日志
                redbagSendLogMongoDbServiceImpl.saveObj(redbagSendlog);
                log.info("企业支付零钱日志添加成功！！");
            }
        }
        return  result ;
    }

    @Override
    public List<ExchangeRedbagRecord> getExchangeRedbagRecord(long userId){
        Example example = new Example(ExchangeRedbagRecord.class);
        example.setOrderByClause("id DESC");
        example.createCriteria().andEqualTo("userId", userId);
        return  exchangeRedbagRecordMapper.selectByExample(example);
    }

}
