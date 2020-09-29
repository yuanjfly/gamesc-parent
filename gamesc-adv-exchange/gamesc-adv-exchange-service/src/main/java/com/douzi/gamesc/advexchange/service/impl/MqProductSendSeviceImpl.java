package com.douzi.gamesc.advexchange.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.douzi.gamesc.advexchange.config.MqConfig;
import com.douzi.gamesc.advexchange.config.MqGameConfig;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MqProductSendSeviceImpl implements MqProductSendSevice {

    private List<JSONObject> msgCash = new ArrayList<JSONObject>();

    @Autowired
    @Qualifier("producer")
    private ProducerBean producer;

    @Autowired
    @Qualifier("gameProducer")
    private ProducerBean gameProducer;

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private MqGameConfig mqGameConfig;

    public  MqProductSendSeviceImpl(){
        //开启一个线程处理发送失败的mq消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendCacheInfo();
            }
        }).start();
    }

    /**
     * 发送mq消息
     * @param body 消息体
     * @param times 延迟时间毫秒
     */
    @Override
    public void sendMessage(String body,long times){
        Message msg = new Message( //
                // Message所属的Topic
                mqConfig.getTopic(),
                // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                mqConfig.getTag(),
                // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                body.getBytes());
        try {
            if(times>0){
                // 延时消息，单位毫秒（ms），在指定延迟时间（当前时间之后）进行投递，例如消息在 3 秒后投递
                long delayTime = System.currentTimeMillis() + times;
                System.out.println("延迟消息。。。。"+delayTime);
                // 设置消息需要被投递的时间
                msg.setStartDeliverTime(delayTime);
            }
            SendResult sendResult = producer.send(msg);
            log.info("message "+body+" send mq msgId: "+sendResult.getMessageId());
        } catch (ONSClientException e) {
            JSONObject msgInfo = new JSONObject();
            msgInfo.put("body",body);
            msgInfo.put("times",times);
            msgInfo.put("tag",1);
            msgCash.add(msgInfo);
            e.printStackTrace();
            log.error("message "+body+" send mq error: "+e.getMessage());
        }
    }

    /**
     * 发送mq消息
     * @param body 消息体
     */
    @Override
    public void sendGameMessage(String body){
        Message msg = new Message( //
                // Message所属的Topic
                mqGameConfig.getTopic(),
                // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                mqGameConfig.getTag(),
                // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                body.getBytes());
        try {
            SendResult sendResult = gameProducer.send(msg);
            log.info("mqGameConfig message "+body+" send mq msgId: "+sendResult.getMessageId());
        } catch (ONSClientException e) {
            JSONObject msgInfo = new JSONObject();
            msgInfo.put("body",body);
            msgInfo.put("tag",2);
            msgCash.add(msgInfo);
            e.printStackTrace();
            log.error("mqGameConfig message "+body+" send mq error: "+e.getMessage());
        }
    }


    @Override
    public void sycGameProp(long userId,String event,JSONObject saveField ,JSONArray properties){
        JSONObject message= new JSONObject();
        message.put("event",event);
        message.put("gameid",0);
        message.put("roomid","");
        message.put("distinct_id",userId);
        message.put("save_field",saveField);
        message.put("properties",properties);
        sendGameMessage(message.toJSONString());
    }

    private void sendCacheInfo(){
        while(true){
            for(int i=0;i<msgCash.size();i++){
                JSONObject msgInfo = msgCash.get(i);
                if(msgInfo.getIntValue("tag")==1){
                    sendMessage(msgInfo.getString("body"),msgInfo.getIntValue("times"));
                }else{
                    sendGameMessage(msgInfo.getString("body"));
                }
            }
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
