package com.douzi.gamesc.account.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface EtcdConfigService {

    public void getEtcdConfig()throws Exception;

    public JSONArray getSmsCfg() throws Exception;

    public JSONObject getSmsCfgByOpen() throws Exception;

    /**
     * 添加监听器
     * @throws Exception
     */
    public void addListenerCfg()throws Exception;
}
