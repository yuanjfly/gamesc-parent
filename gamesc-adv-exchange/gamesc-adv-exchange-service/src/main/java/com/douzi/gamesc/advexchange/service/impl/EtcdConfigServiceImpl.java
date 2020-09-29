package com.douzi.gamesc.advexchange.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.utils.ExchangeRedisUtils;
import com.douzi.gamesc.advexchange.utils.JdEtcdClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class EtcdConfigServiceImpl implements EtcdConfigService {


    @Value("${yule_caige_cluster_globalexchange}")
    private  String cfg_key = "";

    @Value("${yule_caige_cluster_gift_list}")
    private  String gift_list_key = "";

    @Value("${yule_caige_cluster_redbag_wechatcfg}")
    private String redbag_wechat_key = "";

    private final String EXCHANGE_CFG_KEY = "advexchange:shop:config";

    private final String GIFT_LIST_KEY = "advexchange:gift:config";

    private final String REDBAG_WECHAT_KEY = "advexchange:redbag:wechatcfg";


    @Autowired
    private ExchangeRedisUtils exchangeRedisUtils;

    @Autowired
    private JdEtcdClientUtils jdEtcdClientUtils;

    @Override
    public void addListenerCfg()throws Exception{

        jdEtcdClientUtils.startListenerThread(cfg_key,exchangeRedisUtils,EXCHANGE_CFG_KEY,10*3600*2);
        jdEtcdClientUtils.startListenerThread(gift_list_key,exchangeRedisUtils,GIFT_LIST_KEY,15*3600*2);
        jdEtcdClientUtils.startListenerThread(redbag_wechat_key,exchangeRedisUtils,REDBAG_WECHAT_KEY,20*3600*2);
    }
    @Override
    public void getEtcdConfig()throws Exception {

        String cfgStr = jdEtcdClientUtils.getValueByKey(cfg_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(EXCHANGE_CFG_KEY,cfgStr,10*3600*2);
        }
        cfgStr = jdEtcdClientUtils.getValueByKey(gift_list_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(GIFT_LIST_KEY,cfgStr,15*3600*2);
        }
        cfgStr = jdEtcdClientUtils.getValueByKey(redbag_wechat_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(REDBAG_WECHAT_KEY,cfgStr,20*3600*2);
        }

    }

    /**
     * 获取麻将兑换商城配置信息
     * @return
     */
    @Override
    public JSONArray getExchangeShopCfg() throws Exception {

        Object str = exchangeRedisUtils.get(EXCHANGE_CFG_KEY);
        if(str!=null&&!"".equals(str))
        {
            JSONArray cfg = JSONArray.parseArray((String) str);
            return cfg;
        }
        String cfgStr = jdEtcdClientUtils.getValueByKey(cfg_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(EXCHANGE_CFG_KEY,cfgStr,24*3600*2);
            return  JSONArray.parseArray(cfgStr);
        }
        return null;
    }

    @Override
    public JSONObject getExchangeShopCfgByProductId(int productId) throws Exception{
        JSONArray cfg = getExchangeShopCfg();
        for(int i=0;i<cfg.size();i++){
            JSONObject item = cfg.getJSONObject(i);
            if(item.containsKey("id")&&item.getIntValue("id")==productId){
                return item ;
            }
        }
        return  null ;
    }

    @Override
    public JSONArray getGiftListCfg() throws Exception{
        Object str = exchangeRedisUtils.get(GIFT_LIST_KEY);
        if(str!=null&&!"".equals(str))
        {
            JSONArray cfg = JSONArray.parseArray((String) str);
            return cfg;
        }
        String cfgStr = jdEtcdClientUtils.getValueByKey(gift_list_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(GIFT_LIST_KEY,cfgStr,21*3600*2);
            return  JSONArray.parseArray(cfgStr);
        }
        return null;
    }

    @Override
    public JSONObject getGiftListCfgByGiftId(int giftId) throws Exception{
        JSONArray cfg = getGiftListCfg();
        for(int i=0;i<cfg.size();i++){
            JSONObject item = cfg.getJSONObject(i);
            if(item.containsKey("id")&&item.getIntValue("id")==giftId){
                return item ;
            }
        }
        return  null ;
    }


    @Override
    public JSONArray getRedBagWechatCfg() throws Exception{
        Object str = exchangeRedisUtils.get(REDBAG_WECHAT_KEY);
        if(str!=null&&!"".equals(str))
        {
            JSONArray cfg = JSONArray.parseArray((String) str);
            return cfg;
        }
        String cfgStr = jdEtcdClientUtils.getValueByKey(redbag_wechat_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            exchangeRedisUtils.set(REDBAG_WECHAT_KEY,cfgStr,10*3600*2);
            return  JSONArray.parseArray(cfgStr);
        }
        return null;
    }

    @Override
    public JSONObject getRedBagWechatCfgByAppId(String appId) throws Exception{
        JSONArray cfg = getRedBagWechatCfg();
        for(int i=0;i<cfg.size();i++){
            JSONObject item = cfg.getJSONObject(i);
            if(item.containsKey("appid")&&appId.equals(item.getString("appid"))){
                return item ;
            }
        }
        return  null ;
    }


}
