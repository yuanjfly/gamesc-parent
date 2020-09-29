package com.douzi.gamesc.pay.sdk.qutt;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.sdk.ISDKOrderListener;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.pay.sdk.ISDKVerifyListener;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;

public class QuttSdk implements ISDKScript {

    @Override
    public void verify(PlatChannelMasterService channelService, PlatChannelMaster channel,
            String extension, ISDKVerifyListener callback) {

    }
    @Override
    public void onGetOrderID(PlatChannelMasterService channelService,PlatMasterConfigService masterService, PlatOrderPre order, ISDKOrderListener callback) {
        if(callback != null){
            PlatMasterConfig config = masterService.getPlatMasterConfig(order.getMasterId());
            String payCallbackUrl = config.getNotifyUrl();
            JSONObject result = new JSONObject();
            result.put("notifyUrl",payCallbackUrl.substring(0, payCallbackUrl.lastIndexOf("/")));
            callback.onSuccess(result.toJSONString());
        }
    }
}
