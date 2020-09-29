package com.douzi.gamesc.advexchange.vo;

import com.douzi.gamesc.advexchange.vo.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信零钱dto
 * 
 * @author Administrator
 *
 */
@XStreamAlias("xml")
public class WechatChange {

	@XStreamAlias("mch@appid")
	@XStreamCDATA
	private String mchAppid;

	@XStreamAlias("mchid")
	@XStreamCDATA
	private String mchId;

	@XStreamAlias("nonce@str")
	@XStreamCDATA
	private String nonceStr;

	@XStreamAlias("sign")
	@XStreamCDATA
	private String sign;

	@XStreamAlias("partner@trade@no")
	@XStreamCDATA
	private String partnerTradeno;

	@XStreamAlias("openid")
	@XStreamCDATA
	private String openid;

	@XStreamAlias("check@name")
	@XStreamCDATA
	private String checkName;

	@XStreamAlias("amount")
	@XStreamCDATA
	private String amount;

	@XStreamAlias("desc")
	@XStreamCDATA
	private String desc;

	@XStreamAlias("spbill@create@ip")
	@XStreamCDATA
	private String spbillCreateIp;

	public String getMchAppid() {
		return mchAppid;
	}

	public void setMchAppid(String mchAppid) {
		this.mchAppid = mchAppid;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPartnerTradeno() {
		return partnerTradeno;
	}

	public void setPartnerTradeno(String partnerTradeno) {
		this.partnerTradeno = partnerTradeno;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}

	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}

	
	public String createParam(String key) throws Exception {
		StringBuffer param = new StringBuffer();
		if (!StringUtils.isBlank(amount)) {
			param.append("amount=" + amount + "&");
		}
		if (!StringUtils.isBlank(checkName)) {
			param.append("check_name=" + checkName + "&");
		}
		if (!StringUtils.isBlank(desc)) {
			param.append("desc=" + desc + "&");
		}
		if (!StringUtils.isBlank(mchAppid)) {
			param.append("mch_appid=" + mchAppid + "&");
		}
		if (!StringUtils.isBlank(mchId)) {
			param.append("mchid=" + mchId + "&");
		}
		if (!StringUtils.isBlank(nonceStr)) {
			param.append("nonce_str=" + nonceStr + "&");
		}
		if (!StringUtils.isBlank(openid)) {
			param.append("openid=" + openid + "&");
		}
		if (!StringUtils.isBlank(partnerTradeno)) {
			param.append("partner_trade_no=" + partnerTradeno + "&");
		}
		if (!StringUtils.isBlank(spbillCreateIp)) {
			param.append("spbill_create_ip=" + spbillCreateIp + "&");
		}
		param.append("key=" + key.trim());
		return param.toString();
	}

}
