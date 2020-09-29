package com.douzi.gamesc.pay.sdk.wechatoffice.vo;

import lombok.Data;

@Data
public class WxRequest {
    private String appid;//微信公众账号ID
    private String body;//商品描述
    private String mch_id;//微信商户号
    private String nonce_str;//随机字符串，不长于32位
    private	String notify_url;//接收微信支付成功通知
    private String openid;//用户标识，只在trade_type为JSAPI时需要填写（非必填）
    private String out_trade_no;//订单号
    private String spbill_create_ip;//订单生成的机器IP
    private String total_fee;//订单金额，单位分
    private String trade_type;//JSAPI、NATIVE、APP
    private String sign;//签名

    private String attach;//附加数据，原样返回（非必填）
    private String product_id;//只在trade_type为NATIVE时需要填写（非必填）
    private String device_info;//设备号（非必填）
    private String time_start;//订单生成的时间，格式为yyyyMMddHHmmss（非必填）
    private String time_expire;//订单失效时间，格式为yyyyMMddHHmmss（非必填）
    private String goods_tag;//商品标记（非必填）
}
