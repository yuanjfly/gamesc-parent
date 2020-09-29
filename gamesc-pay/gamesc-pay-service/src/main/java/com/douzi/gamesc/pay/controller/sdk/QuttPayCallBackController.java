package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.cache.MqProducerCacheManager;
import com.douzi.gamesc.pay.sdk.qutt.Sign;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatOrderPreService;
import com.douzi.gamesc.pay.utils.OrderState;
import com.douzi.gamesc.user.utils.ObjectUtils;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pay/qutt")
public class QuttPayCallBackController {

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Autowired
    private PlatOrderPreService platOrderPreServiceImpl;

    @Autowired
    private MqProducerCacheManager mqProducerCacheManager;

    @ResponseBody
    @RequestMapping("/callback")
    public HttpResult payCallback(@RequestBody Map<String, String> params) throws Exception {

        //查询订单渠道相关信息
        String ext = (String) params.get("ext");
        JSONObject extJson = JSONObject.parseObject(ext);
        if(!extJson.containsKey("orderNo"))
        {
            return HttpResult.error(Result.BIND_ERROR.getCode(),Result.BIND_ERROR.getMsg());
        }
        String orderNo = extJson.getString("orderNo");
        Integer realMoney = ObjectUtils.toInt(params.get("total_fee"));
        PlatOrderPre orderPre = platOrderPreServiceImpl.getOneByOrderNo(orderNo);

        if(orderPre==null||orderPre.getMoney().intValue()!=realMoney.intValue()){
            return HttpResult.error(Result.ORDER_EXIST_ERROR.getCode(),Result.ORDER_EXIST_ERROR.getMsg());
        }else if(orderPre.getState()== OrderState.FINISN_PAY){//订单已完成，直接返回成功
            return HttpResult.ok();
        }
        PlatChannelMaster channelMaster = platChannelMasterServiceImpl.getPlatChannelMaster(
                orderPre.getChannelId(),orderPre.getAppId(),orderPre.getMasterId());

        if(!Sign.checkSign(params,channelMaster.getCpAppKey())){
            log.error("the qutt callback param check sign failed.");
            return HttpResult.error(Result.PARAM_SIGN_ERROR.getCode(),Result.PARAM_SIGN_ERROR.getMsg());
        }

        orderPre.setMasterOrderNo(params.get("trade_no"));
        orderPre.setRealMoney(realMoney);
        orderPre.setState(OrderState.FINISN_PAY);
        orderPre.setFinishTime(new Date());
        orderPre.setPayRate(channelMaster.getPayRate());
        orderPre.setPayShareRate(channelMaster.getPayShareRate());
        platOrderPreServiceImpl.update(orderPre);
        //发送mq消息
        if(channelMaster.getMqInfo()!=null&&!"".equals(channelMaster.getMqInfo())&&orderPre.getExtension()!=null){
            JSONObject message= new JSONObject();
            message.put("event","recharge_order");
            message.put("gameid",0);
            message.put("roomid","");
            message.put("distinct_id",orderPre.getUserId());
            JSONObject business = new JSONObject();
            business.put("orderid",orderNo);
            business.put("money",realMoney);
            business.put("business",orderPre.getExtension());
            business.put("tradetype",channelMaster.getMasterId());
            message.put("properties",business);
            mqProducerCacheManager.sendMqMessage(channelMaster.getMqInfo(),message.toJSONString());
        }
        return HttpResult.ok() ;
    }
}
