package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.cache.MqProducerCacheManager;
import com.douzi.gamesc.pay.sdk.wechatoffice.WechatOrderHelp;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WeiXinCallBackRequset;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WeiXinCallBackResp;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatOrderPreService;
import com.douzi.gamesc.pay.utils.OrderState;
import com.douzi.gamesc.user.utils.BeanUtils;
import com.douzi.gamesc.user.utils.ObjectUtils;
import java.util.Date;
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
@RequestMapping("/pay/wechat")
public class WechatOfficePayCallBackController {

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Autowired
    private PlatOrderPreService platOrderPreServiceImpl;

    @Autowired
    private MqProducerCacheManager mqProducerCacheManager;

    @ResponseBody
    @RequestMapping("/sdk/callback")
    public HttpResult wechatSdkCallback(@RequestBody Map<String, String> mapRest) throws Exception {
        log.info("wehcat sdk payback--微信回调字符串str:"+mapRest);
        WeiXinCallBackResp weiXinCallBackResp = new WeiXinCallBackResp();
        try{

            WeiXinCallBackRequset result = BeanUtils.convertMapToObject(mapRest,WeiXinCallBackRequset.class);
            if(!"SUCCESS".equalsIgnoreCase(result.getReturn_code())){
                log.error("微信订单回调结果失败！"+result.getReturn_msg());
                weiXinCallBackResp.setReturn_code("FAIL");
                weiXinCallBackResp.setReturn_msg("参数验证失败");
                return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }
            if(!"SUCCESS".equalsIgnoreCase(result.getResult_code())){
                log.error("微信订单回调结果失败！"+result.getErr_code_des());
                weiXinCallBackResp.setReturn_code("FAIL");
                weiXinCallBackResp.setReturn_msg("微信订单未完成");
                return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }

            String orderNo = result.getOut_trade_no();
            Integer realMoney = ObjectUtils.toInt(result.getTotal_fee());
            PlatOrderPre orderPre = platOrderPreServiceImpl.getOneByOrderNo(orderNo);

            if(orderPre==null||orderPre.getMoney().intValue()!=realMoney.intValue()){
                log.error("微信官方回调充值订单不存在！sdcustomno="+result.getOut_trade_no());
                weiXinCallBackResp.setReturn_code("FAIL");
                weiXinCallBackResp.setReturn_msg("订单号验证失败");
                return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }else if(orderPre.getState()== OrderState.FINISN_PAY){//订单已完成，直接返回成功
                log.error("微信官方回调充值订单已完成！sdcustomno="+result.getOut_trade_no());
                weiXinCallBackResp.setReturn_code("SUCCESS");
                weiXinCallBackResp.setReturn_msg("OK");
                return HttpResult.ok(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }

            PlatChannelMaster channelMaster = platChannelMasterServiceImpl.getPlatChannelMaster(
                    orderPre.getChannelId(),orderPre.getAppId(),orderPre.getMasterId());

            if(channelMaster==null){
                log.error("微信官方回调商户错误");
                weiXinCallBackResp.setReturn_code("FAIL");
                weiXinCallBackResp.setReturn_msg("商户号验证失败");
                return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }
            SortedMap<String, String> parameters = new TreeMap<String, String>(mapRest);
            //获取签名
            String sign = WechatOrderHelp.createSign(parameters, channelMaster.getCpPaySecret());
            //判断签名
            if(!result.getSign().equalsIgnoreCase(sign))
            {
                log.error("微信官方回调 签名失败");
                weiXinCallBackResp.setReturn_code("FAIL");
                weiXinCallBackResp.setReturn_msg("签名验证失败");
                return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
            }

            orderPre.setMasterOrderNo(result.getTransaction_id());
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
            weiXinCallBackResp.setReturn_code("SUCCESS");
            weiXinCallBackResp.setReturn_msg("OK");
            return HttpResult.ok(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
        }catch (Exception e){
            weiXinCallBackResp.setReturn_code("FAIL");
            weiXinCallBackResp.setReturn_msg("服务器处理异常");
            return HttpResult.error(WechatOrderHelp.getWechatRepXml(weiXinCallBackResp));
        }

    }
}
