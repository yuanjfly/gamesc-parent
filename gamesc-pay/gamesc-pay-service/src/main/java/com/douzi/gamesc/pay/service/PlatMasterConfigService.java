package com.douzi.gamesc.pay.service;

import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;

public interface PlatMasterConfigService {

    PlatMasterConfig getPlatMasterConfig(int masterId);

    PlatMasterConfig getPlatMasterConfig(String masterId);
}
