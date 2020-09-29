package com.douzi.gamesc.advexchange.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.douzi.gamesc.advexchange.service.ExchangeProductService;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.common.pojo.exhange.ExchangeCurrentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExchangeMessageListener implements MessageListener {

    @Autowired
    private UserPropService userPropServiceImpl;
    @Autowired
    private MqProductSendSevice mqProductSendSeviceImpl;
    @Autowired
    private ExchangeProductService exchangeProductServiceImpl;

    @Override
    public Action consume(Message message, ConsumeContext context) {

        try {
            String res = new String(message.getBody(),"UTF-8");
            log.info("消费者获得数据。。。。。"+res);
            JSONObject mqInfo = JSONObject.parseObject(res);
            //延迟时间到自动更新成待审核
            if(mqInfo.containsKey("event")&&"exchange_delay".equals(mqInfo.getString("event"))){
                long userId = mqInfo.getLongValue("distinct_id");
                int productId = mqInfo.getIntValue("product_id");
                //延迟道具发送成功，更新当前延迟记录的信息
                exchangeProductServiceImpl.updateExhangeCurrentRecord(userId,productId,1);
                //vip等级提升自动更新待审核
            }else  if(mqInfo.containsKey("event")&&"recharge_result".equals(mqInfo.getString("event"))){
                long userId = mqInfo.getLongValue("distinct_id");
                long vip = mqInfo.containsKey("properties")? mqInfo.getLongValue("properties"):0;
                //获取vip经验值
                JSONObject vipCfg = userPropServiceImpl.getUserVipLevelByExp(vip,9);
                //vip等级是否免审
                int vipFastChange = 0 ;
                if(vipCfg!=null&&vipCfg.containsKey("vipFastChange")){
                    vipFastChange =  vipCfg.getIntValue("vipFastChange");
                }
                if(vipFastChange==1){
                    //延迟道具发送成功，更新当前延迟记录的信息
                    ExchangeCurrentRecord record = exchangeProductServiceImpl.getExchangeCurrentRecord(userId);
                    if(record!=null&&record.getStatus()==0){
                        exchangeProductServiceImpl.updateExhangeCurrentRecord(userId,record.getProductId(),1);
                        //发送道具同步消息
                        JSONObject saveField = new JSONObject();
                        saveField.put("goodsid",record.getProductId());
                        saveField.put("canceldelay",1);
                        saveField.put("isexchange",0);
                        mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField, new JSONArray());
                    }
                }
            }
        } catch (Exception e) {
            //消费失败
           log.error( " msgId:"+message.getMsgID()+ "custom messgae error: "+e.getMessage());
        }
        return Action.CommitMessage;
    }
}
