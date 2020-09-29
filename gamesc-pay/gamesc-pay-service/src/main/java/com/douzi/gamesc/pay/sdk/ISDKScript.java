package com.douzi.gamesc.pay.sdk;

import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;

/**
 * SDK操作脚本
 * 第三方登录认证的校验
 *
 * 关于项目中编码问题
 * tomcat中sever.xml中Connector中需要设置编码为utf-8
 * web.xml中
 * struts.xml中
 * 都设置为utf-8
 *
 */
public interface ISDKScript {

    public void verify(PlatChannelMasterService channelService, PlatChannelMaster channel, String extension, ISDKVerifyListener callback);

    public void onGetOrderID(PlatChannelMasterService channelService, PlatMasterConfigService masterService, PlatOrderPre order, ISDKOrderListener callback);

}
