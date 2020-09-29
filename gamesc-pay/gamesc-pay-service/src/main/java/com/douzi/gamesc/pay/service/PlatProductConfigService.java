package com.douzi.gamesc.pay.service;

import com.douzi.gamesc.common.pojo.order.PlatAppConfig;
import com.douzi.gamesc.common.pojo.order.PlatProductConfig;

public interface PlatProductConfigService {

    PlatProductConfig getPlatProductConfig(String appId,int productId);
}
