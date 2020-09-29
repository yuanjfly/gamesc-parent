package com.douzi.gamesc.pay.service;

import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;

public interface PlatChannelMasterService {

    public void initPlatChannelMaster();

    public PlatChannelMaster getPlatChannelMaster(String channel,String appId,String masterId);
}
