package com.douzi.gamesc.advexchange.redbagbalance.ads;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;

public interface RebagBalanceStrategy {

    //红包兑换方式
    RedbagSendLog balanceOutWay(ExchangeRedbagRecord redbag,JSONObject redBagCfg);

    boolean isOK(int type);
}
