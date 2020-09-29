package com.douzi.gamesc.pay.cache;

import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个渠道SDK都有一个实现了ISDKScript接口的SDK逻辑处理类，登录认证和获取订单号接口中，通过反射的方式来
 * 实例化对应渠道的类，可能会导致一定的性能瓶颈。所以，这里我们增加一个缓存，第一次实例化之后，将对应渠道的处理类，缓存起来。
 * 后面使用的时候，直接从缓存中取
 * ================================================2019/06/01更新缓存方式到Rredis===========================================================================
 */

@Slf4j
@Component
public class SDKCacheManager {

    @Autowired
    private PlatMasterConfigService platMasterConfigServiceImpl;

    private static SDKCacheManager instance;

    private Map<String, ISDKScript> sdkCaches;

    private SDKCacheManager(){
        sdkCaches = new HashMap<String, ISDKScript>();
    }

    public static SDKCacheManager getInstance(){
        if(instance == null){
            instance = new SDKCacheManager();

        }

        return instance;
    }

    /***
     * 获取指定渠道的ISDKScript的实例
     * @param channel
     * @return
     */
    public ISDKScript getSDKScript(PlatChannelMaster channel){

        if(channel == null){
            return  null;
        }

        if(sdkCaches.containsKey(channel.getMasterId())){
            return sdkCaches.get(channel.getMasterId());
        }

        try {

            PlatMasterConfig platMasterConfig = platMasterConfigServiceImpl
                    .getPlatMasterConfig(channel.getMasterId());
            ISDKScript script = (ISDKScript)Class.forName(platMasterConfig.getVerifyClass()).newInstance();
            sdkCaches.put(channel.getMasterId(), script);
            return script;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
