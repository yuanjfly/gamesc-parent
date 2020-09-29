package com.douzi.gamesc.advexchange.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface UserPropService {

    public JSONObject operatPropByRpc(JSONObject data);

    public JSONArray getUserVipLevelConfig()throws Exception;

    public JSONObject getUserVipLevelByExp(long exp,int vipEXPid) throws Exception;

    /**
     * 添加监听器
     * @throws Exception
     */
    public void addListenerCfg()throws Exception;
}
