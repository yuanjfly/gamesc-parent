package com.douzi.gamesc.pay.sdk.wechatoffice.vo;

import lombok.Data;

@Data
public class WeiXinPayRespVo {
	private String appid;//appid
	private String timestamp;//时间戳
	private String noncestr;//随机字符串
	private String packageValue;//package值
	private String prepayid;//预订单成功后返回的订单id
	private String signType;//签名类型：APP充值时为空，JSAPI充值时为“MD5”
	private String sign;//签名
	private String partnerid;//商户号（APP充值时有值，JSAPI充值时为空）

}
