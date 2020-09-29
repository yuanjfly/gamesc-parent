package com.douzi.gamesc.pay.sdk.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.sdk.ISDKOrderListener;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.pay.sdk.ISDKVerifyListener;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliPaySdk implements ISDKScript {

    final private String requestUrl="https://openapi.alipay.com/gateway.do";

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
                //实例化客户端
                PlatChannelMaster platChannelMaster = channelService.
                        getPlatChannelMaster(order.getChannelId(),order.getAppId(),order.getMasterId());
                PlatMasterConfig config = masterService.getPlatMasterConfig(order.getMasterId());

                AlipayClient alipayClient = new DefaultAlipayClient(
                        requestUrl,
                        platChannelMaster.getCpAppId(),
                        platChannelMaster.getCpPaySecret(),
                        "json",
                        "UTF-8",
                        platChannelMaster.getCpAppKey(), "RSA2");
                //请求参数
                String out_trade_no = order.getOrderNo();//唯一订单
                DecimalFormat df=new DecimalFormat("0.00");
                String total_amount = String.valueOf(df.format(order.getMoney()*1.0/100));//交易金额（元，double类型）
                String product_code = "QUICK_MSECURITY_PAY";//销售产品码，商家和支付宝签约的产品码。该产品请填写固定值：QUICK_MSECURITY_PAY

                AlipayTradeAppPayRequest alipay_request = new AlipayTradeAppPayRequest();

                // 封装请求支付信息
                AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
                model.setOutTradeNo(out_trade_no);
                model.setSubject(order.getGiftName());
                model.setTotalAmount(total_amount);
                model.setBody("");
                model.setTimeoutExpress("");
                model.setProductCode(product_code);
                alipay_request.setBizModel(model);
                // 设置异步通知地址
                alipay_request.setNotifyUrl(config.getNotifyUrl());
                // form表单生产
                String form = "";
                try {
                    AlipayTradeAppPayResponse alipay_response = alipayClient.sdkExecute(alipay_request);
                    form = alipay_response.getBody();
                } catch (AlipayApiException e) {
                    log.error("请求支付宝客户端出现错误："+e.getMessage());
                    e.printStackTrace();
                }
                json.put("return_code", 1);
                json.put("return_message", "");
                json.put("orderInfo",form);
                callback.onSuccess(json.toJSONString());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
