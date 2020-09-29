package com.douzi.gamesc.advexchange.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.exhange.ExchangeCurrentRecord;
import com.douzi.gamesc.common.pojo.exhange.ExchangeProductDay;

public interface ExchangeProductService {

    /**
     * 获取商品缓存中的库存
     */
    public int getTwoHourStoreByProductId( int productId,JSONArray cfg);

    /**
     * 更新所有每两小时库存
     */
    public void updateTwoHourStoreAll(JSONArray cfg);

    /**
     * 更新其中一件商品的每两小时库存
     * @param productId
     */
    public void updateTwoHourStoreByProductId(long userId,int allTotal, int productId, JSONObject cfg);

    /**
     * 更新所有每日库存
     */
    public void updateDayStoreAll(JSONArray cfg);

    /**
     * 更新其中一件商品的库存
     * @param productId
     */
    public void updateDayStoreByProductId(long userId, int allTotal, int productId, int dayTotal,
            JSONObject cfg);
    /**
     * 当前一个月以内是否与待审核记录
     * @param userId
     * @return
     */
    public ExchangeCurrentRecord getExchangeCurrentRecord(long userId);

    /**
     * 获取该礼包该用户历史总次数
     * @param type 1可以读缓存，2直接查询数据库
     * @param userId
     * @param productId
     * @return
     */
    public int getExchangeGiftTotalByProductIdAndUserId(int type, long userId, int productId);

    /**
     * 获取该礼包每日总兑换数
     * @param productId
     * @return
     */
    public int getExchangeGiftDayByProductId(int productId);
    /**
     * 获取该礼包该用户每日总兑换数
     * @param productId
     * @return
     */
    public ExchangeProductDay getExchangeGiftDayByProductIdAndUserId(long userId, int productId);

    /**
     * 发送道具操作到RPC中间件
     * @param data
     * @return
     */
    public JSONObject operatPropByRpc(JSONObject data);

    /**
     * 插入兑换信息
     * @param userId
     * @param productId
     * @param exchangeProductDay
     */
    public void saveExchanegProduct(long userId, int productId,
            ExchangeProductDay exchangeProductDay);

    /**
     * 插入延迟兑换信息
     * @param userId
     * @param productId
     * @param productDetail
     * @param times
     */
    public ExchangeCurrentRecord saveExhangeCurrentRecord(long userId, int productId,
            String productDetail, String rpcInfo, int times);

    /**
     * 更新当前延迟进度
     * @param userId
     * @param productId
     */
    public  boolean updateExhangeCurrentRecord(long userId, int productId, int status);

    /**
     * 查询历史兑换次数
     * @param userId
     * @return
     */
    public int getAllExhangeTotal(long userId);
}
