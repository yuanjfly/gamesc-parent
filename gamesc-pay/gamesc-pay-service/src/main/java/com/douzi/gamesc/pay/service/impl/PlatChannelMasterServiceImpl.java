package com.douzi.gamesc.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.pay.cache.MqProducerCacheManager;
import com.douzi.gamesc.pay.mapper.PlatChannelMasterMapper;
import com.douzi.gamesc.pay.mq.MqConfig;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.utils.RedisUtils;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class PlatChannelMasterServiceImpl implements PlatChannelMasterService {

    private static String REDIS_CHANNEL_MASTER_INFO = "pay:channel_master_info";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PlatChannelMasterMapper  platChannelMasterMapper;

    @Autowired
    private MqProducerCacheManager mqProducerCacheManager;

    private List<PlatChannelMaster> getAllPlatChannelMaster(){
        return platChannelMasterMapper.selectAll();
    }

    @Override
    public void initPlatChannelMaster(){
        JSONObject channelMasterData = new JSONObject();
        List<PlatChannelMaster> channelMasters = getAllPlatChannelMaster();
        for(PlatChannelMaster item : channelMasters){
            String key = item.getChannelId()+"_"+item.getAppId()+"_"+item.getMasterId();
            channelMasterData.put(key,item);
            if(item.getMqInfo()!=null&&!"".equals(item.getMqInfo())){
                MqConfig mqConfig = JSONObject.parseObject(item.getMqInfo(),MqConfig.class);
                mqProducerCacheManager.buildProducer(mqConfig);
            }
        }
        redisUtils.set(REDIS_CHANNEL_MASTER_INFO, channelMasterData.toJSONString(),24*3600*2);
    }

    @Override
    public PlatChannelMaster getPlatChannelMaster(String channel, String appId, String masterId){

        Object str = redisUtils.get(REDIS_CHANNEL_MASTER_INFO);
        String key = channel+"_"+appId+"_"+masterId;
        JSONObject channelMasterData = null;
        if(str!=null&&!"".equals(str))
        {
            channelMasterData = JSONObject.parseObject((String) str);
            if(channelMasterData.containsKey(key)){
                PlatChannelMaster config = JSONObject.parseObject(channelMasterData.get(key).toString(), PlatChannelMaster.class);
                return  config;
            }
        }
        if(channelMasterData==null){
            channelMasterData = new JSONObject();
        }
        //从数据库中查询配置信息
        Example example = new Example(PlatChannelMaster.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("channelId",channel).andEqualTo("appId",appId).andEqualTo("masterId",masterId);
        PlatChannelMaster config = platChannelMasterMapper.selectOneByExample(example);
        if(config!=null){
            channelMasterData.put(key,config);
        }
        redisUtils.set(REDIS_CHANNEL_MASTER_INFO, channelMasterData.toJSONString(),24*3600*2);
        return config;
    }
}
