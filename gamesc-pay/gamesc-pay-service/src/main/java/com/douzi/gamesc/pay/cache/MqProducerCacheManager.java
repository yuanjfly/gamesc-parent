package com.douzi.gamesc.pay.cache;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.douzi.gamesc.pay.mq.MqConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqProducerCacheManager {

    private static MqProducerCacheManager instance;

    private Map<String, ProducerBean> producerBeanCaches;

    private MqProducerCacheManager(){
        producerBeanCaches = new HashMap<String, ProducerBean>();
    }

    public static MqProducerCacheManager getInstance(){
        if(instance == null){
            instance = new MqProducerCacheManager();
        }
        return instance;
    }
    /**
     * 获取出创建的客户端
     * @param mqConfig
     * @return
     */

    public ProducerBean buildProducer(MqConfig mqConfig) {
        if(producerBeanCaches.containsKey(mqConfig.getGroupId())){
            return producerBeanCaches.get(mqConfig.getGroupId());
        }
        ProducerBean producer = buildProducer(mqConfig.getMqPropertie());
        producerBeanCaches.put(mqConfig.getGroupId(), producer);
        return producer;
    }

    public ProducerBean buildProducer(Properties properties) {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(properties);
        producer.start();
        return producer;
    }

    /**
     * 通过mqjson配置信息发送消息
     * @param mqInfo
     * @param body
     */
    public void sendMqMessage(String mqInfo,String body){
        MqConfig mqConfig = JSONObject.parseObject(mqInfo,MqConfig.class);
        sendMqMessage(mqConfig,body);
    }

    /**
     * 通过 MqConfig 发送信息
     * @param mqConfig
     * @param body
     */
    public void sendMqMessage(MqConfig mqConfig,String body){
        ProducerBean producerBean = buildProducer(mqConfig);
        Message msg = new Message(
                mqConfig.getTopic(),
                mqConfig.getTag(),
                body.getBytes());
        try {
            SendResult sendResult = producerBean.send(msg);
            log.info("send mq success msg:"+body+".....msgId:"+sendResult.getMessageId());
        } catch (ONSClientException e) {
            log.error("send mq error msg:"+body);
        }
    }


}
