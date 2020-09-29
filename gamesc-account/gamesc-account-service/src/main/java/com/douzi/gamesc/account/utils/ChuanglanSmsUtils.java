package com.douzi.gamesc.account.utils;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.user.utils.HttpClient;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChuanglanSmsUtils {

    public  int sendSms(String url,String account,String password, String mobile,String msgContent) throws IOException {
        JSONObject map = new JSONObject();
        map.put("account",account);//API账号
        map.put("password",password);//API密码
        map.put("msg",msgContent);//短信内容
        map.put("phone",mobile);//手机号
        map.put("report","true");//是否需要状态报告
        map.put("extend","123");//自定义扩展码
        HttpClient httpClient = new HttpClient(url);
        httpClient.setJsonParam(map.toJSONString());
        httpClient.post();
        String jsonStr = httpClient.getContent();
        log.info(mobile+" send msg result:"+jsonStr);
        JSONObject result = JSONObject.parseObject(jsonStr);
        if(result.containsKey("code")){
            return result.getIntValue("code") ;
        }else{
            return -1 ;
        }
    }

}
