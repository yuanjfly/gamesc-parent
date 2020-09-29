package com.douzi.gamesc.pay.sdk.wechatoffice.vo;

import lombok.Data;

@Data
public class WxResponse {
	private String return_code;//SUCCESS/FAIL
	private String return_msg;//返回信息，如非空，为错误原因
	
	//当return_code为SUCCESS时
	private String appid;//用户微信ID
	private String mch_id;//商户号
	private String device_info;//设备号（非必有）
	private String nonce_str;//随机字符串
	private String sign;//签名
	private String result_code;//业务结果：SUCCESS/FAIL
	private String err_code;//错误代码
	private String err_code_des;//错误代码描述
	
	//当return_code 和result_code都为SUCCESS时
	private String trade_type;//JSAPI、NATIVE、APP
	private String prepay_id;//预支付ID
	private String code_url;//二维码链接，trade_type为NATIVE时有返回


}
