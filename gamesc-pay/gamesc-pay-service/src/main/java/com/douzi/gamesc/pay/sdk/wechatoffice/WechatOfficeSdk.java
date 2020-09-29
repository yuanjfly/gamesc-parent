package com.douzi.gamesc.pay.sdk.wechatoffice;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.sdk.ISDKOrderListener;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.pay.sdk.ISDKVerifyListener;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WeiXinPayRespVo;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WxResponse;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import com.douzi.gamesc.user.utils.HttpClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WechatOfficeSdk implements ISDKScript {

    //微信生成预订单链接
    final private String requestUrl="https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Override
    public void verify(PlatChannelMasterService channelService, PlatChannelMaster channel,
            String extension, ISDKVerifyListener callback) {

    }

    @Override
    public void onGetOrderID(PlatChannelMasterService channelService,
            PlatMasterConfigService masterService, PlatOrderPre order, ISDKOrderListener callback) {
        if(callback != null){
            try {
                JSONObject json = new JSONObject();
                PlatChannelMaster platChannelMaster = channelService.
                        getPlatChannelMaster(order.getChannelId(),order.getAppId(),order.getMasterId());
                PlatMasterConfig config = masterService.getPlatMasterConfig(order.getMasterId());
                String postXml = WechatOrderHelp.getRequestXml("APP",config.getNotifyUrl(),platChannelMaster,order);

                HttpClient httpClient = new HttpClient(requestUrl);
                httpClient.setXmlParam(postXml);
                httpClient.post();
                String retStr = httpClient.getContent();
                log.info("微信预下单返回结果："+retStr);
                WxResponse wxResponse = WechatOrderHelp.getWxResponseByRepXml(retStr);
                if (!"SUCCESS".equalsIgnoreCase(wxResponse.getReturn_code())||!"SUCCESS".equalsIgnoreCase(wxResponse.getResult_code())) {
                    log.error("提交微信预订单请求失败！");
                    json.put("return_code", "-1");
                    json.put("return_message", "提交微信预订单请求失败");
                }
                WeiXinPayRespVo weiXinPayRespVo = WechatOrderHelp.getWeiXinPayRespVo(wxResponse,platChannelMaster);
                json.put("return_code", 1);
                json.put("return_message", "SUCCESS");
                json.put("appid", weiXinPayRespVo.getAppid());
                json.put("noncestr", weiXinPayRespVo.getNoncestr());
                json.put("package", weiXinPayRespVo.getPackageValue());
                json.put("partnerid", weiXinPayRespVo.getPartnerid());
                json.put("prepayid", weiXinPayRespVo.getPrepayid());
                json.put("sign", weiXinPayRespVo.getSign());
                json.put("timestamp", weiXinPayRespVo.getTimestamp());
                json.put("app_signature", weiXinPayRespVo.getSign());
                json.put("traceid","crestxu_" + System.currentTimeMillis() / 1000);
                json.toString();
                callback.onSuccess(json.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
