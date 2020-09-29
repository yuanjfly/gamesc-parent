package com.douzi.gamesc.pay.sdk.lianwifi;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.sdk.ISDKOrderListener;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.pay.sdk.ISDKVerifyListener;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import com.douzi.gamesc.user.utils.HttpClient;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * 万能钥匙连尚SDK
 * @author yuanjf
 *
 */
public class LianWifiSdk implements ISDKScript {

	@Override
	public void verify(PlatChannelMasterService channelService, PlatChannelMaster channel,
			String extension, ISDKVerifyListener callback) {

	}

	@Override
	public void onGetOrderID(PlatChannelMasterService channelService,
			PlatMasterConfigService masterService, PlatOrderPre order, ISDKOrderListener callback) {
		if(callback != null){
			try {
				JSONObject ext = JSONObject.parseObject(order.getExtension());
				if(!ext.containsKey("tradetype")){
					JSONObject rs = new JSONObject();
					rs.put("return_code", -1);
					rs.put("return_message", "请选择支付方式");
					callback.onSuccess(rs.toJSONString());
					return;
				}
				String tradeType = ext.getString("tradetype");
				PlatChannelMaster platChannelMaster = channelService.
						getPlatChannelMaster(order.getChannelId(),order.getAppId(),order.getMasterId());
				PlatMasterConfig config = masterService.getPlatMasterConfig(order.getMasterId());
				JSONObject postJson = new JSONObject();
				Map<String, String> param = new TreeMap();
				param.put("mchId", platChannelMaster.getCpPayId());
				param.put("sdpAppId", platChannelMaster.getCpAppId());
				param.put("outTradeNo",order.getOrderNo());
				param.put("notifyUrl",config.getNotifyUrl());
				param.put("nonceStr", RandomChars.getRandomChars(32));
				param.put("totalFee", order.getMoney()+"");
				param.put("tradeType", tradeType);
				param.put("body", order.getGiftName());
				param.put("currency", "CNY");
				param.put("clientIp", order.getIp());
				param.put("signType", "RSA");
				StringBuffer sb = new StringBuffer();
				Iterator it = param.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					String k = (String)entry.getKey();
					String v = (String)entry.getValue();
					if(null != v && !"".equals(v)) {
						sb.append(k + "=" + v + "&");
						postJson.put(k,v);
					}
				}
				String paramStr = sb.toString();
				paramStr = paramStr.substring(0,paramStr.length()-1);
				String sign = RSA.sign(paramStr,platChannelMaster.getCpPaySecret(),"UTF-8");
				postJson.put("sign",sign);
				HttpClient httpClient = new HttpClient(config.getOrderUrl());
				httpClient.setJsonParam(postJson.toJSONString());
				httpClient.post();
				JSONObject rs = JSONObject.parseObject(httpClient.getContent());
				if(rs.containsKey("returnCode")&&rs.getString("returnCode").equals("SUCCESS")){//预下单成功
					rs.put("return_code",1);
					rs.put("return_message", "SUCCESS");
				}else{
					rs.put("return_code", 1);
					rs.put("return_message", rs.getString("returnMsg"));
				}
				callback.onSuccess(rs.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
