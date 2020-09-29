package com.douzi.gamesc.advexchange.service;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import java.util.List;

public interface RedBagBalanceService {

    /**
     * 获取当天提现金额
     * @param userId
     * @return
     */
    public int queryDayTotalByUserId(long userId);
    /**
     * 获取用户累计提现金额
     * @param userId
     * @return
     */
    public int queryAllTotalByUserId(long userId);
    /**
     * 通过用户和应用查找绑定信息
     * @param appId
     * @param userId
     * @return
     */
    public ExchangeBindUser getExchangeBindUser(String appId,long userId);

    /**
     * 绑定用户关系
     * @param bindUser
     * @return
     */
    public ExchangeBindUser saveExchangeBindUser(ExchangeBindUser bindUser);
    /**
     * 通过openId 和应用找对应关系
     * @param appId
     * @param openId
     * @return
     */
    public  ExchangeBindUser getExchangeBindOpenId(String productId,String appId,String openId);

    /**
     *
     * @param redbag
     * @return
     */
    public ExchangeRedbagRecord getExchangeRedbagRecord(ExchangeRedbagRecord redbag);
    /**
     *
     * @param redbag
     * @return
     */
    public ExchangeRedbagRecord saveExchangeRedbagRecord(ExchangeRedbagRecord redbag);

    /**
     *
     * @param redbag
     * @param redBagCfg
     * @return
     */
    public JSONObject sendRedBagByBindUser(ExchangeRedbagRecord redbag,JSONObject redBagCfg);

    /**
     *获取兑换列表
     * @return
     */
    public List<ExchangeRedbagRecord> getExchangeRedbagRecord(long userId);

}
