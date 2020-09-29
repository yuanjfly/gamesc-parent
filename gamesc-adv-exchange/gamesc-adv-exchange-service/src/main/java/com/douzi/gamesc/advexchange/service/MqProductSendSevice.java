package com.douzi.gamesc.advexchange.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface MqProductSendSevice {

    public void sendMessage(String body,long times);

    public void sendGameMessage(String body);

    public void sycGameProp(long userId,String event,JSONObject saveField,JSONArray properties);
}
