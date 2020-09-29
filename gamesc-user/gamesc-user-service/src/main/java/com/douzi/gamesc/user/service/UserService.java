package com.douzi.gamesc.user.service;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

public interface UserService {

    public String getAccountPropertyByString(String key);

    public JSONObject getAccountPropertyByJson(String key);

    public Map<Object, Object> getGameUserProperty(String key);

    public JSONObject getUserPropertyByJson(String key);
}
