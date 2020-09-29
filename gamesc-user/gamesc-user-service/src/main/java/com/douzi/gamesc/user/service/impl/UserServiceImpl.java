package com.douzi.gamesc.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.user.service.UserService;
import com.douzi.gamesc.user.utils.AccountRedisUtils;
import com.douzi.gamesc.user.utils.UserRedisUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private AccountRedisUtils accountRedisUtils;

    @Autowired
    private UserRedisUtils userRedisUtils;
    /**
     * 获取麻将兑换商城配置信息
     * @return
     */
    @Override
    public String getAccountPropertyByString(String key) {
        Object value = accountRedisUtils.get(key);
        if(value!=null){
            return  String.valueOf(value);
        }
        return null;
    }

    @Override
    public JSONObject getAccountPropertyByJson(String key){
        Object value = accountRedisUtils.get(key);
        if(value!=null){
            return JSONObject.parseObject((String)value);
        }
        return null;
    }

    @Override
    public Map<Object, Object> getGameUserProperty(String key) {

        Map<Object, Object> values = userRedisUtils.hmget(key);
        return values;
    }

    @Override
    public JSONObject getUserPropertyByJson(String key){
        Object value = userRedisUtils.get(key);
        if(value!=null){
            return JSONObject.parseObject((String)value);
        }
        return null;
    }
}
