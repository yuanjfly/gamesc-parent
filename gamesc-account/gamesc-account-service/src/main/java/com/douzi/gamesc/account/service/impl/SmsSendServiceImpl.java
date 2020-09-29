package com.douzi.gamesc.account.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.account.service.SmsSendService;
import com.douzi.gamesc.account.utils.ChuanglanSmsUtils;
import com.douzi.gamesc.account.utils.SendReceiveMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsSendServiceImpl implements SmsSendService {

    @Autowired
    private ChuanglanSmsUtils chuanglanSmsUtils;

    @Override
    public int sendVerifyCodeByMajhong(JSONObject smsCfg,String phone, String verifyCode) throws Exception {
        String msgContent;
        int result = -1;
        if("xuanwu".equals(smsCfg.getString("tag"))){
            msgContent = "【猜歌达人】验证码："+verifyCode+"，有效期10分钟，为了您的账号安全，请勿泄露给他人。（若非本人操作，请忽略不本短信）";
            result = SendReceiveMsgUtil.md5SendSMSPost(new String[]{phone},msgContent,smsCfg.getString("account"),smsCfg.getString("password"));
        }else if("chuanglan".equals(smsCfg.getString("tag"))){
            msgContent = "【猜歌达人】验证码："+verifyCode+"，有效期10分钟，为了您的账号安全，请勿泄露给他人。（若非本人操作，请忽略不本短信）";
            result = chuanglanSmsUtils.sendSms(smsCfg.getString("url"),smsCfg.getString("account"),smsCfg.getString("password"),phone,msgContent);
        }
        return  result ;
    }
}
