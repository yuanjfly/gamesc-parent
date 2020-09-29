package com.douzi.gamesc.pay.controller.sdk;

import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.feign.ApiPayFeign;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pay/alipay")
public class AliPaySdkCallBackController {

    @Autowired
    private ApiPayFeign apiPayFeign;

    @ResponseBody
    @RequestMapping("/payCallback")
    public String payCallback(HttpServletRequest request,HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");// 此句必须在response.getWriter()之前
        try{
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            HttpResult result = apiPayFeign.aliPaySdkCallback(params);
            return  result.getMsg();
        }catch (Exception e){
            log.error("alipay call back error:"+e.getMessage());
            return "error";
        }
    }
}
