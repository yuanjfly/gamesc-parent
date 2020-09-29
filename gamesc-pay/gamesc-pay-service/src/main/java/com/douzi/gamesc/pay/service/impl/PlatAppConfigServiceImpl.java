package com.douzi.gamesc.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatAppConfig;
import com.douzi.gamesc.pay.mapper.PlatAppConfigMapper;
import com.douzi.gamesc.pay.service.PlatAppConfigService;
import com.douzi.gamesc.pay.utils.RedisUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatAppConfigServiceImpl implements PlatAppConfigService {

    private static String REDIS_APP_INFO = "pay:app_info";
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PlatAppConfigMapper platAppConfigMapper;

    private List<PlatAppConfig> getAllPlatAppConfig(){
        return platAppConfigMapper.selectAll();
    }

    @Override
    public PlatAppConfig getPlatAppConfig(String appId) {

        Object str = redisUtils.get(REDIS_APP_INFO);
        if(str!=null&&!"".equals(str))
        {
            JSONObject appData = JSONObject.parseObject((String) str);
            if(appData.containsKey(appId)){
                JSONObject jsonObject = appData.getJSONObject(appId);
                return JSONObject.parseObject(jsonObject.toString(), PlatAppConfig.class);
            }
        }
        //缓存中没有重新加载
        List<PlatAppConfig> list = getAllPlatAppConfig();
        if(list==null||list.size()<=0){
            return null;
        }
        JSONObject appData = new JSONObject();
        PlatAppConfig config = null ;
        for(PlatAppConfig item:list){
            appData.put(item.getAppId(),item);
            if(item.getAppId().equals(appId)&&config==null){
                config = item;
            }
        }
        redisUtils.set(REDIS_APP_INFO, appData.toJSONString(),24*3600*2);
        return config;
    }
}
