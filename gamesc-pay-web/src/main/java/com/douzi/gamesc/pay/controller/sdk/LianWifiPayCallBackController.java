package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.feign.ApiPayFeign;
import com.douzi.gamesc.user.utils.BeanUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pay/lianwifi")
public class LianWifiPayCallBackController {

    @Autowired
    private ApiPayFeign apiPayFeign;

    @ResponseBody
    @RequestMapping("/payCallback")
    public String payCallback(HttpServletRequest request,HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");// 此句必须在response.getWriter()之前
        try{
            String str = IOUtils.toString(request.getInputStream(),"UTF-8");
            Map<String, String> param = JSONObject.parseObject(str,Map.class);
            HttpResult result = apiPayFeign.lianWifiSdkCallback(param);
            return  result.getMsg();
        }catch (Exception e){
            log.error("lianwifi call back error:"+e.getMessage());
            return "error";
        }
    }
}
