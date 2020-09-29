package com.douzi.gamesc.advexchange.vo;

import com.douzi.gamesc.advexchange.vo.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class WechatChangeBack {

	@XStreamAlias("return_code")
	@XStreamCDATA
	private String returnCode;
	@XStreamAlias("return_msg")
	@XStreamCDATA
	private String returnMsg;
	@XStreamAlias("result_code")
	@XStreamCDATA
	private String resultCode;
	@XStreamAlias("err_code")
	@XStreamCDATA
	private String errCode;
	@XStreamAlias("err_code_des")
	@XStreamCDATA
	private String errCodeDes;
	@XStreamAlias("partner_trade_no")
	@XStreamCDATA
	private String partnerTradeNo;
	@XStreamAlias("mchid")
	@XStreamCDATA
	private String mchId;
	@XStreamAlias("mch_appid")
	@XStreamCDATA
	private String wxappId;
	@XStreamAlias("device_info")
	@XStreamCDATA
	private String deviceInfo;
	@XStreamAlias("payment_no")
	@XStreamCDATA
	private String paymentNo;
	@XStreamAlias("payment_time")
	@XStreamCDATA
	private String paymentTime;
	@XStreamAlias("nonce_str")
	@XStreamCDATA
	private String nonceStr;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCodeDes() {
		return errCodeDes;
	}

	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}

	public String getPartnerTradeNo() {
		return partnerTradeNo;
	}

	public void setPartnerTradeNo(String partnerTradeNo) {
		this.partnerTradeNo = partnerTradeNo;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getWxappId() {
		return wxappId;
	}

	public void setWxappId(String wxappId) {
		this.wxappId = wxappId;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	@Override
	public String toString() {
		return "WechatChangeBack [returnCode=" + returnCode + ", returnMsg="
				+ returnMsg + ", resultCode=" + resultCode + ", errCode="
				+ errCode + ", errCodeDes=" + errCodeDes + ", partnerTradeNo="
				+ partnerTradeNo + ", mchId=" + mchId + ", wxappId=" + wxappId
				+ ", deviceInfo=" + deviceInfo + ", paymentNo=" + paymentNo
				+ ", paymentTime=" + paymentTime + ", nonceStr=" + nonceStr
				+ "]";
	}
	
	

}
