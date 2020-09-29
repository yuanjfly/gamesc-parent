package com.douzi.gamesc.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.pay.mapper.PlatMasterConfigMapper;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import com.douzi.gamesc.pay.utils.RedisUtils;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatMasterConfigServiceImpl implements PlatMasterConfigService {

    private static String REDIS_MASTER_INFO = "pay:master_info";
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PlatMasterConfigMapper platMasterConfigMapper;

    private List<PlatMasterConfig> getAllPlatMasterConfig(){
        return platMasterConfigMapper.selectAll();
    }

    @Override
    public PlatMasterConfig getPlatMasterConfig(int keyId) {

        Object str = redisUtils.get(REDIS_MASTER_INFO);
        if(str!=null&&!"".equals(str))
        {
            JSONObject masterData = JSONObject.parseObject((String) str);
            Iterator<String> keys = masterData.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                PlatMasterConfig config = JSONObject.parseObject(masterData.get(key).toString(), PlatMasterConfig.class);
                if(config.getId()==keyId){
                    return  config ;
                }
            }
        }
        //缓存中没有重新加载
        List<PlatMasterConfig> list = getAllPlatMasterConfig();
        if(list==null||list.size()<=0){
            return null;
        }
        JSONObject masterData = new JSONObject();
        PlatMasterConfig config = null ;
        for(PlatMasterConfig item:list){
            masterData.put(item.getMasterId(),item);
            if(item.getId()==keyId&&config==null){
                config = item;
            }
        }
        redisUtils.set(REDIS_MASTER_INFO, masterData.toJSONString(),24*3600*2);
        return config;
    }

    @Override
    public PlatMasterConfig getPlatMasterConfig(String masterId) {
        Object str = redisUtils.get(REDIS_MASTER_INFO);
        if(str!=null&&!"".equals(str))
        {
            JSONObject masterData = JSONObject.parseObject((String) str);
            if(masterData.containsKey(masterId)){
                PlatMasterConfig config = JSONObject.parseObject(masterData.get(masterId).toString(), PlatMasterConfig.class);
                return  config ;
            }
        }
        //缓存中没有重新加载
        List<PlatMasterConfig> list = getAllPlatMasterConfig();
        if(list==null||list.size()<=0){
            return null;
        }
        JSONObject masterData = new JSONObject();
        PlatMasterConfig config = null ;
        for(PlatMasterConfig item:list){
            masterData.put(item.getMasterId(),item);
            if(item.getMasterId().equals(masterId)&&config==null){
                config = item;
            }
        }
        redisUtils.set(REDIS_MASTER_INFO, masterData.toJSONString(),24*3600*2);
        return config;
    }
}
