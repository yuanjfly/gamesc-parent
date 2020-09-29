package com.douzi.gamesc.account.service;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;

public interface SmsSendService {

    /**
     * 发送真人雀神验证码
     * @param phone
     * @param code
     * @return
     */
    int sendVerifyCodeByMajhong(JSONObject smsCfg,String phone,String code) throws Exception;
}
