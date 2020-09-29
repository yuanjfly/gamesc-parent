package com.douzi.gamesc.advexchange.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface EtcdConfigService {

    public void getEtcdConfig()throws Exception;

    public JSONArray getExchangeShopCfg() throws Exception;

    public JSONObject getExchangeShopCfgByProductId(int productId) throws Exception;

    public JSONArray getGiftListCfg() throws Exception;

    public JSONObject getGiftListCfgByGiftId(int giftId) throws Exception;

    public JSONArray getRedBagWechatCfg() throws Exception;

    public JSONObject getRedBagWechatCfgByAppId(String appId) throws Exception;

    /**
     * 添加监听器
     * @throws Exception
     */
    public void addListenerCfg()throws Exception;
}
