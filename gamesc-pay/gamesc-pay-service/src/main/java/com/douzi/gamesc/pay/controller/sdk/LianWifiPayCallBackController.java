package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.cache.MqProducerCacheManager;
import com.douzi.gamesc.pay.sdk.lianwifi.RSA;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatOrderPreService;
import com.douzi.gamesc.pay.utils.OrderState;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pay/lianwifi")
public class LianWifiPayCallBackController {

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Autowired
    private PlatOrderPreService platOrderPreServiceImpl;

    @Autowired
    private MqProducerCacheManager mqProducerCacheManager;


    @ResponseBody
    @RequestMapping("/sdk/callback")
    public HttpResult payCallback(@RequestBody Map<String, String> params) throws Exception {
        log.info("lianwifi sdk payback--连尚回调字符串str:"+params);
        try{
            //商户订单号
            String out_trade_no = params.get("outTradeNo");
            //盛付通订单号
            String trade_no = params.get("transactionId");
            //交易状态
            String trade_status = params.get("status");

            String resultCode = params.get("resultCode");

            String total_amount = params.get("totalFee");

            if("CLOSED".equals(trade_status)){//订单结束
                return HttpResult.ok("SUCCESS");
            }
            //支付结果
            if(!"PAY_SUCCESS".equals(trade_status)||!"SUCCESS".equals(resultCode)){
                log.error(out_trade_no+"交易状态："+trade_status);
                return HttpResult.error("fail");
            }

            Integer realMoney = Integer.valueOf(total_amount);

            PlatOrderPre orderPre = platOrderPreServiceImpl.getOneByOrderNo(out_trade_no);

            if(orderPre==null||orderPre.getMoney().intValue()!=realMoney.intValue()){
                log.error("连尚SDK回调充值订单不存在！sdcustomno="+out_trade_no);
                return HttpResult.error("订单号验证失败");
            }else if(orderPre.getState()== OrderState.FINISN_PAY){//订单已完成，直接返回成功
                log.error("连尚SDK回调充值订单已完成！sdcustomno="+out_trade_no);
                return HttpResult.ok("success");
            }

            PlatChannelMaster channelMaster = platChannelMasterServiceImpl.getPlatChannelMaster(
                    orderPre.getChannelId(),orderPre.getAppId(),orderPre.getMasterId());

            if(channelMaster==null){
                log.error("连尚SDK回调商户错误");
                return HttpResult.error("商户号验证失败");
            }
            SortedMap<String, String> sort = new TreeMap<String, String>(params);
            StringBuffer sb = new StringBuffer();
            Iterator it = sort.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String k = (String)entry.getKey();
                String v = (String)entry.getValue();
                if(null != v && !"".equals(v)&&!k.equals("sign")) {
                    sb.append(k + "=" + v + "&");
                }
            }
            log.info("连尚回调过来的");
            boolean verify_result = RSA.verify(sb.toString(),params.get("sign"),channelMaster.getCpAppKey(),"UTF-8");
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
            return HttpResult.ok("SUCCESS");
        }catch (Exception e){
            log.error("连尚回调处理异常：参数"+params+" 错误信息："+e.getMessage());
            e.printStackTrace();
            return HttpResult.error("fail");
        }
    }
}
