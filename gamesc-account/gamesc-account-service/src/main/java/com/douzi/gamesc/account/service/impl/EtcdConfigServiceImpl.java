package com.douzi.gamesc.account.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.account.service.EtcdConfigService;
import com.douzi.gamesc.account.utils.JdEtcdClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EtcdConfigServiceImpl implements EtcdConfigService {

    private  JSONObject smsCfgCache = new JSONObject();

    @Value("${qtth5_cluster_sms}")
    private  String sms_cfg_key = "";

    private String smsKey = "smscfg";

    @Autowired
    private JdEtcdClientUtils jdEtcdClientUtils;

    @Override
    public void addListenerCfg()throws Exception{
        jdEtcdClientUtils.startListenerThread(sms_cfg_key,smsCfgCache,smsKey,0);
    }
    @Override
    public void getEtcdConfig()throws Exception {
        String cfgStr = jdEtcdClientUtils.getValueByKey(sms_cfg_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            smsCfgCache.put(smsKey,cfgStr);
        }
    }

    @Override
    public JSONArray getSmsCfg() throws Exception {
        String cfgStr;
        if(smsCfgCache.containsKey(smsKey)){
            cfgStr =  smsCfgCache.getString(smsKey);
            if(cfgStr!=null&&!"".equals(cfgStr)){
                return JSONArray.parseArray(cfgStr);
            }
        }
        //缓存取不到从etcd上拉取
        cfgStr = jdEtcdClientUtils.getValueByKey(sms_cfg_key);
        if(cfgStr!=null&&!"".equals(cfgStr)){
            smsCfgCache.put(smsKey,cfgStr);//放入缓存
            return JSONArray.parseArray(cfgStr);
        }
        return null;
    }

    @Override
    public JSONObject getSmsCfgByOpen() throws Exception {
        JSONArray cfg  = getSmsCfg();
        if(cfg!=null){
            for(int i=0;i<cfg.size();i++){
                JSONObject item = cfg.getJSONObject(i);
                if(item.containsKey("open")&&item.getIntValue("open")==1){
                    return item ;
                }
            }
        }
        return null;
    }

}
