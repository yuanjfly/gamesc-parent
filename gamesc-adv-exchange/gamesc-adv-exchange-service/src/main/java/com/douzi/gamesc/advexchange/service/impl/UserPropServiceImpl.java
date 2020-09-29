package com.douzi.gamesc.advexchange.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.advexchange.start.PropOperateClient;
import com.douzi.gamesc.advexchange.utils.ExchangeRedisUtils;
import com.douzi.gamesc.advexchange.utils.JdEtcdClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserPropServiceImpl implements UserPropService {


    @Value("${yule_caige_cluster_vip_level}")
    private  String vip_level = "";

    @Value("${yule_caige_cluster_prop_list}")
    private  String prop_list = "";

    private final String VIPLEVEL_CFG_KEY = "advexchange:vip:config";

    @Autowired
    private ExchangeRedisUtils exchangeRedisUtils;

    @Autowired
    private JdEtcdClientUtils jdEtcdClientUtils;

    @Autowired
    private PropOperateClient propOperateClient;


    @Override
    public void addListenerCfg()throws Exception{

        jdEtcdClientUtils.startListenerThread(vip_level,exchangeRedisUtils,VIPLEVEL_CFG_KEY,10*3600*2);
    }

    @Override
    public JSONArray getUserVipLevelConfig() throws Exception {
        String vipStr = jdEtcdClientUtils.getValueByKey(vip_level);
        if(vipStr!=null&&!"".equals(vipStr)){
            JSONArray vipLevelConfig = JSONArray.parseArray(vipStr);
            exchangeRedisUtils.set(VIPLEVEL_CFG_KEY,vipStr,10*3600*2);
            return  vipLevelConfig ;
        }
        return  null ;
    }


    /**
     * 通过经验值获得vip等级
     * @param exp
     */
    @Override
    public JSONObject getUserVipLevelByExp(long exp,int vipEXPid) throws Exception {

        JSONArray vipLevelConfig = null ;
        Object str = exchangeRedisUtils.get(VIPLEVEL_CFG_KEY);
        if(str!=null&&!"".equals(str))
        {
            vipLevelConfig = JSONArray.parseArray((String) str);
        }else{
            vipLevelConfig = getUserVipLevelConfig();
        }
        if(vipLevelConfig == null){
            return null;
        }
        JSONObject level = null;
        for(int i=0;i<vipLevelConfig.size();i++){
            JSONObject item = vipLevelConfig.getJSONObject(i);
            if(item.containsKey("vipEXPid")&&item.getIntValue("vipEXPid")!=vipEXPid){
                continue;
            }
            if(item.getLong("vipEXP")>exp){
                break;
            }
            level = item ;
        }
        return  level ;
    }



    @Override
    public JSONObject operatPropByRpc(JSONObject data){
        return  propOperateClient.sendData("BalanceService.TransferData",data);
    }


}
