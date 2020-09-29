package com.douzi.gamesc.pay.sdk.wechatoffice.vo;

import lombok.Data;

@Data
public class WeiXinCallBackRequset {
	
	private String return_code;//交易结果
	private String return_msg;//返回错误信息（非必有）

	//return_code为SUCCESS时有以下参数
	private String appid;
	private String mch_id;//商户号
	private String device_info;//设备号（非必有）
	private String nonce_str;//随机字符串，不长于32位
	private String sign;//签名
	private String result_code;//业务结果：SUCCESS/FAIL
	private String err_code;//错误代码（非必有）
	private String err_code_des;//错误代码描述（非必有）
	
	//当return_code 和result_code都为SUCCESS时
	private String openid;//用户微信ID
	private String is_subscribe;//用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
	private String trade_type;//JSAPI、NATIVE、APP、MICROPAY
	private String bank_type;//付款银行
	private String total_fee;//订单总金额，单位为分
	private String cash_fee;//现金支付金额，单位为分
	private String cash_fee_type;//现金支付货币类型
	private String coupon_fee;//现金券金额（非必有）,现金券支付金额<=订单总金额，订单总金额-现金券金额为现金支付金额
	private String fee_type;//货币种类,符合ISO 4217标准的三位字母代码，默认人民币：CNY
	private String transaction_id;//微信支付订单号
	private String out_trade_no;//商户订单号
	private String attach;//商家数据包（非必有）
	private String time_end;//支付完成时间
	private String coupon_count;
	private String coupon_id_0;
	private String coupon_fee_0;

}
