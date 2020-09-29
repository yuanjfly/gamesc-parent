package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.cache.MqProducerCacheManager;
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
@RequestMapping("/pay/alipay")
public class AliPaySdkCallBackController {

    private final  String ali_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgeG2tOALhT4J5Yl+OnrlqsmXB0BP1WRRFOn4G2PPq6LaeR9VoCuXmUdX7Jv3MiQLiBdB9YWlwBPIzQbQXJ2bpo/1bBUcXK0/2UKQXsPFmacXj4shMLD7cKknH/d2p+IrPV1riyeA1MphcsugdSsU9jYDSH6SyWEa6jl+YJenci05xuJcNPgnA29bIs8saTpYlXvIRie0wTtEAgxZNWoxXpkpI/8+NSUNOOJey/hOTZNd0q99NHodsbXEecsQ4n6dPbRUFiLGKtp0229DAEcxPVdhJjbe33qTv9vRXphq/xuQ5fvdomh6PpwlYnvqgBsRnEGYE/7ve0ie7XY7smsGFwIDAQAB";

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Autowired
    private PlatOrderPreService platOrderPreServiceImpl;

    @Autowired
    private MqProducerCacheManager mqProducerCacheManager;

    @ResponseBody
    @RequestMapping("/sdk/callback")
    public HttpResult payCallback(@RequestBody Map<String, String> params) throws Exception {
        log.info("ali sdk payback--支付宝回调字符串str:"+params);
        try{
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)
            //商户订单号
            String out_trade_no = params.get("out_trade_no");
            //支付宝交易号
            String trade_no = params.get("trade_no");
            //交易状态
            String trade_status = params.get("trade_status");

            String total_amount = params.get("total_amount");

            String app_id = params.get("auth_app_id");

            if("TRADE_FINISHED".equals(trade_status)){//订单结束
                return HttpResult.ok("success");
            }
            //支付结果
            if(!"TRADE_SUCCESS".equals(trade_status)){
                log.error("TRADE_FINISHED===交易结束，不可退款.交易状态："+trade_status);
                return HttpResult.error("fail");
            }

            Integer realMoney = ObjectUtils.toInt(Double.valueOf(total_amount)*100);
            PlatOrderPre orderPre = platOrderPreServiceImpl.getOneByOrderNo(out_trade_no);

            if(orderPre==null||orderPre.getMoney().intValue()!=realMoney.intValue()){
                log.error("支付宝SDK回调充值订单不存在！sdcustomno="+out_trade_no);
                return HttpResult.error("订单号验证失败");
            }else if(orderPre.getState()== OrderState.FINISN_PAY){//订单已完成，直接返回成功
                log.error("支付宝SDK回调充值订单已完成！sdcustomno="+out_trade_no);
                return HttpResult.ok("success");
            }

            PlatChannelMaster channelMaster = platChannelMasterServiceImpl.getPlatChannelMaster(
                    orderPre.getChannelId(),orderPre.getAppId(),orderPre.getMasterId());

            if(channelMaster==null){
                log.error("支付宝SDK回调商户错误");
                return HttpResult.error("商户号验证失败");
            }
            boolean verify_result = AlipaySignature
                    .rsaCheckV1(params, ali_public_key, "UTF-8", "RSA2");
            if(!verify_result){
                log.error(out_trade_no + " 签名验证失败");
                return HttpResult.error("签名验证失败");
            }
            orderPre.setMasterOrderNo(trade_no);
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
                business.put("orderid",out_trade_no);
                business.put("money",realMoney);
                business.put("business",orderPre.getExtension());
                business.put("tradetype",channelMaster.getMasterId());
                message.put("properties",business);
                mqProducerCacheManager.sendMqMessage(channelMaster.getMqInfo(),message.toJSONString());
            }
            return HttpResult.ok("success");
        }catch (Exception e){
            log.error("支付宝回调处理异常：参数"+params+" 错误信息："+e.getMessage());
            e.printStackTrace();
            return HttpResult.error("fail");
        }
    }
}
