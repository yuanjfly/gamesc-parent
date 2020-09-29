package com.douzi.gamesc.advexchange.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.mapper.ExchangeCurrentRecordMapper;
import com.douzi.gamesc.advexchange.mapper.ExchangeProductDayMapper;
import com.douzi.gamesc.advexchange.mapper.ExchangeProductTotalMapper;
import com.douzi.gamesc.advexchange.service.ExchangeProductService;
import com.douzi.gamesc.advexchange.start.PropOperateClient;
import com.douzi.gamesc.advexchange.utils.ExchangeRedisUtils;
import com.douzi.gamesc.common.pojo.exhange.ExchangeCurrentRecord;
import com.douzi.gamesc.common.pojo.exhange.ExchangeProductDay;
import com.douzi.gamesc.common.pojo.exhange.ExchangeProductTotal;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.additional.aggregation.AggregateCondition;
import tk.mybatis.mapper.additional.aggregation.AggregateType;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ExchangeProductServiceImpl implements ExchangeProductService {

    @Autowired
    private ExchangeCurrentRecordMapper exchangeCurrentRecordMapper ;
    @Autowired
    private ExchangeProductTotalMapper exchangeGiftTotalMapper ;
    @Autowired
    private ExchangeProductDayMapper exchangeGiftDayMapper;
    @Autowired
    private PropOperateClient propOperateClient;
    @Autowired
    private ExchangeRedisUtils exchangeRedisUtils;

    private final String EXCHANGE_CFG_KEY = "advexchange:shop:daystore";

    private static  String USER_PRODUCT_EXCHANGE_ALL = "advexchange:shop:product:%d";

    /**
     * 获取靠近平均的两小时的剩余秒数
     * @return
     */
    public long getSecondsTwoHours(){
        long cuurry = System.currentTimeMillis()/1000;
        Calendar cal = Calendar.getInstance();
        for(int i=1;i<=12;i++){
            cal.set(Calendar.HOUR_OF_DAY, i*2);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            System.out.println(cal.getTimeInMillis()/1000-1);
            if(cal.getTimeInMillis()/1000-1>cuurry){
                return cal.getTimeInMillis()/1000-1-cuurry;
            }
        }
        return  0;
    }

    public Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    @Override
    public int getTwoHourStoreByProductId( int productId,JSONArray cfg){
        int store = 0;
        Object redisStore = exchangeRedisUtils.hmget(EXCHANGE_CFG_KEY);
        if(redisStore==null){
            Map<String, Object>  daystore = new HashMap<String, Object>();
            for(int i=0;i<cfg.size();i++) {
                JSONObject item = cfg.getJSONObject(i);
                if (item.containsKey("store")) {
                    //均分到12个区间，每两小时还原
                    daystore.put(item.getString("id"), item.getIntValue("store")/12);
                }
                if(item.getString("id").equals(productId+"")){
                    store = item.getIntValue("store")/12;
                }
            }
            exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsTwoHours());
        }else{//已经存在
            Map<String, Object> daystore = (Map<String, Object>) redisStore;
            Iterator it= daystore.keySet().iterator();
            while(it.hasNext()){
                //取出key
                String key=it.next().toString();
                if(key.equals(productId+"")){
                    //取出缓存中的库存
                    store = (Integer) daystore.get(key);
                    break;
                }
            }
        }
        return store;
    }

    @Override
    public void updateTwoHourStoreAll(JSONArray cfg){
        Object redisStore = exchangeRedisUtils.hmget(EXCHANGE_CFG_KEY);
        if(redisStore==null){
            Map<String, Object>  daystore = new HashMap<String, Object>();
            for(int i=0;i<cfg.size();i++) {
                JSONObject item = cfg.getJSONObject(i);
                if (item.containsKey("store")) {
                    //均分到12个区间，每两小时还原
                    daystore.put(item.getString("id"),item.getIntValue("store")/12);
                }
            }
            exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsTwoHours());
        }else{//已经存在
            Map<String, Object> daystore = (Map<String, Object>) redisStore;
            for(int i=0;i<cfg.size();i++) {
                JSONObject item = cfg.getJSONObject(i);
                if (item.containsKey("store")) {
                    if(daystore.containsKey(item.getString("id"))){//已经存在取出redis数据
                        item.put("store",daystore.get(item.getString("id")));
                    }else{//不存在放入redis
                        daystore.put(item.getString("id"),item.getIntValue("store")/12);
                    }
                }
            }
            exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsTwoHours());
        }
    }

    @Override
    public void updateTwoHourStoreByProductId(long userId,int allTotal,int productId,JSONObject cfg){
        Object  redisStore = exchangeRedisUtils.hmget(EXCHANGE_CFG_KEY);
        if(redisStore!=null){//只处理已经存在的
            Map<String, Object> daystore = (Map<String, Object>) redisStore;
            Iterator it= daystore.keySet().iterator();
            while(it.hasNext()){
                //取出key
                String key=it.next().toString();
                if(key.equals(cfg.getString("id"))&&cfg.containsKey("store")){
                    //取出缓存中的库存
                    int had = (Integer) daystore.get(key)-1;
                    daystore.put(key,had>0?had:0);
                    exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsTwoHours());
                    break;
                }
            }
        }
        //更新redis 商品的历史兑换次数
        if(exchangeRedisUtils.hHasKey(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"")){
            exchangeRedisUtils.hincr(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"",1);
        }else{
            int random = (int)(Math.random()*24)+12;
            exchangeRedisUtils.hset(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"",allTotal+1,random*60*60);
        }
    }

    @Override
    public void updateDayStoreAll(JSONArray cfg){
        Object redisStore = exchangeRedisUtils.hmget(EXCHANGE_CFG_KEY);
        if(redisStore==null){
            Map<String, Object>  daystore = new HashMap<String, Object>();
            for(int i=0;i<cfg.size();i++) {
                JSONObject item = cfg.getJSONObject(i);
                if (item.containsKey("store")) {
                    daystore.put(item.getString("id"),item.getIntValue("store"));
                }
            }
            exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsNextEarlyMorning());
        }else{//已经存在
            Map<String, Object> daystore = (Map<String, Object>) redisStore;
            for(int i=0;i<cfg.size();i++) {
                JSONObject item = cfg.getJSONObject(i);
                if (item.containsKey("store")) {
                    if(daystore.containsKey(item.getString("id"))){//已经存在取出redis数据
                        item.put("store",daystore.get(item.getString("id")));
                    }else{//不存在放入redis
                        daystore.put(item.getString("id"),item.getIntValue("store"));
                    }
                }
            }
            exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsNextEarlyMorning());
        }
    }

    @Override
    public void updateDayStoreByProductId(long userId,int allTotal,int productId,int dayTotal,JSONObject cfg){
        Object  redisStore = exchangeRedisUtils.hmget(EXCHANGE_CFG_KEY);
        if(redisStore!=null){//只处理已经存在的
            Map<String, Object> daystore = (Map<String, Object>) redisStore;
            Iterator it= daystore.keySet().iterator();
            while(it.hasNext()){
                //取出key
                String key=it.next().toString();
                if(key.equals(cfg.getString("id"))&&cfg.containsKey("store")){
                    int had = cfg.getIntValue("store")-dayTotal-1;
                    daystore.put(key,had>0?had:0);
                    exchangeRedisUtils.hmset(EXCHANGE_CFG_KEY,daystore,getSecondsNextEarlyMorning());
                    break;
                }
            }
        }
        //更新redis 商品的历史兑换次数
        if(exchangeRedisUtils.hHasKey(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"")){
            exchangeRedisUtils.hincr(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"",1);
        }else{
            int random = (int)(Math.random()*24)+12;
            exchangeRedisUtils.hset(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"",allTotal+1,random*60*60);
        }
    }

    @Override
    public ExchangeCurrentRecord getExchangeCurrentRecord(long userId) {
        Example example = new Example(ExchangeCurrentRecord.class);
        example.createCriteria().andEqualTo("userId",userId).andNotEqualTo("status",2).andCondition("TO_DAYS(create_time)+30 >=TO_DAYS(NOW())");
        List<ExchangeCurrentRecord> list = exchangeCurrentRecordMapper.selectByExample(example);
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }
        return null;
    }

    @Override
    public int getExchangeGiftDayByProductId(int productId) {
        Example example = new Example(ExchangeProductDay.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId);
        criteria.andCondition("TO_DAYS(create_time) = TO_DAYS(NOW())");
        AggregateCondition condition = new  AggregateCondition("hasNum",AggregateType.SUM).aliasName("hasNum");
        List<ExchangeProductDay> giftDayTotal =  exchangeGiftDayMapper.selectAggregationByExample(example,condition);
        if(giftDayTotal!=null&&giftDayTotal.size()>0&&giftDayTotal.get(0)!=null){
            return giftDayTotal.get(0).getHasNum()==null?0:giftDayTotal.get(0).getHasNum();
        }
        return 0;
    }

    @Override
    public ExchangeProductDay getExchangeGiftDayByProductIdAndUserId(long userId,int productId){
        Example example = new Example(ExchangeProductDay.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId).andEqualTo("productId",productId);
        criteria.andCondition("TO_DAYS(create_time) = TO_DAYS(NOW())");
        return exchangeGiftDayMapper.selectOneByExample(example);
    }

    @Override
    public int getExchangeGiftTotalByProductIdAndUserId(int type,long userId,int productId) {

        if(type==1){//可以优先去缓存
            if(exchangeRedisUtils.hHasKey(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"")){
                Object value = exchangeRedisUtils.hget(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"");
                if(value!=null){
                    return  Integer.parseInt(String.valueOf(value));
                }
            }
        }
        Example example = new Example(ExchangeProductTotal.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("productId",productId);
        AggregateCondition condition = new  AggregateCondition("hasNum",AggregateType.SUM).aliasName("hasNum");
        List<ExchangeProductTotal> userTotal =  exchangeGiftTotalMapper.selectAggregationByExample(example,condition);
        int num = 0;
        if(userTotal!=null&&userTotal.size()>0&&userTotal.get(0)!=null){
            num = userTotal.get(0).getHasNum()==null?0:userTotal.get(0).getHasNum();
        }
        int random = (int)(Math.random()*24)+12;
        exchangeRedisUtils.hset(String.format(USER_PRODUCT_EXCHANGE_ALL,userId),productId+"",num,random*60*60);
        return num;

    }


    @Override
    public JSONObject operatPropByRpc(JSONObject data){
        return  propOperateClient.sendData("BalanceService.TransferData",data);
    }

    @Override
    public void saveExchanegProduct(long userId,int productId,ExchangeProductDay exchangeProductDay){
        Date now = new Date();
        //保存当天兑换信息
        if(exchangeProductDay==null){
            exchangeProductDay = new ExchangeProductDay();
            exchangeProductDay.setUserId(userId);
            exchangeProductDay.setProductId(productId);
            exchangeProductDay.setHasNum(1);
            exchangeProductDay.setUpdateTime(now);
            exchangeGiftDayMapper.insertSelective(exchangeProductDay);
        }else{
            exchangeProductDay.setUpdateTime(now);
            exchangeProductDay.setHasNum(exchangeProductDay.getHasNum()+1);
            exchangeGiftDayMapper.updateByPrimaryKeySelective(exchangeProductDay);
        }
        //保存通兑换记录
        ExchangeProductTotal exchangeProductTotal = new ExchangeProductTotal();
        exchangeProductTotal.setUserId(userId);
        exchangeProductTotal.setProductId(productId);
        exchangeProductTotal.setUpdateTime(now);
        ExchangeProductTotal selectOne = exchangeGiftTotalMapper.selectOne(exchangeProductTotal);
        if(selectOne!=null){//存在记录
            exchangeProductTotal = selectOne ;
            exchangeProductTotal.setHasNum(selectOne.getHasNum()+1);
            exchangeProductTotal.setUpdateTime(now);
            Example example = new Example(ExchangeProductTotal.class);
            example.createCriteria().andEqualTo("userId",userId).andEqualTo("productId",productId);
            exchangeGiftTotalMapper.updateByExampleSelective(exchangeProductTotal,example);
        }else{
            exchangeProductTotal.setHasNum(1);
            exchangeGiftTotalMapper.insertSelective(exchangeProductTotal);
        }

    }

    @Override
    public ExchangeCurrentRecord saveExhangeCurrentRecord(long userId,int productId,String productDetail,String rpcInfo,int times){
        ExchangeCurrentRecord exhangeCurrentRecord = new ExchangeCurrentRecord();
        exhangeCurrentRecord.setUserId(userId);
        exhangeCurrentRecord.setProductId(productId);
        exhangeCurrentRecord.setProductDetail(productDetail);
        exhangeCurrentRecord.setRpcInfo(rpcInfo);
        exhangeCurrentRecord.setLaveTime(times);
        exhangeCurrentRecord.setStatus(0);
        exhangeCurrentRecord.setCreateTime(new Date());
        exchangeCurrentRecordMapper.insertSelective(exhangeCurrentRecord);
        return exhangeCurrentRecord ;
    }

    @Override
    public  boolean updateExhangeCurrentRecord(long userId,int productId,int status){
        ExchangeCurrentRecord exhangeCurrentRecord = new ExchangeCurrentRecord();
        exhangeCurrentRecord.setStatus(status);
        exhangeCurrentRecord.setUpdateTime(new Date());
        Example example = new Example(ExchangeCurrentRecord.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("productId",productId).andNotEqualTo("status",2);
        if(exchangeCurrentRecordMapper.updateByExampleSelective(exhangeCurrentRecord,example)>0){
            return  true ;
        }else{
            return  false ;
        }
    }

    @Override
    public int getAllExhangeTotal(long userId){
        Example example = new Example(ExchangeProductTotal.class);
        example.createCriteria().andEqualTo("userId",userId);
        AggregateCondition condition = new  AggregateCondition("hasNum",AggregateType.SUM).aliasName("hasNum");
        List<ExchangeProductTotal> userTotal =  exchangeGiftTotalMapper.selectAggregationByExample(example,condition);
        if(userTotal!=null&&userTotal.size()>0&&userTotal.get(0)!=null){
            return userTotal.get(0).getHasNum()==null?0:userTotal.get(0).getHasNum();
        }
        return 0;
    }
}
