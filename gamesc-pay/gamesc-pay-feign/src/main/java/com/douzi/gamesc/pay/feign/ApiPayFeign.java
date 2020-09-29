package com.douzi.gamesc.pay.feign;

import com.douzi.gamesc.common.pojo.OrderParam;
import com.douzi.gamesc.http.HttpResult;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("gamesc-pay")
public interface ApiPayFeign {

    @RequestMapping(value = "/pay/createOrder",method = RequestMethod.POST)
    HttpResult createOrder(@RequestBody OrderParam orderParam);

    @RequestMapping(value = "/pay/qutt/callback",method = RequestMethod.POST)
    HttpResult quttCallback(@RequestBody Map<String, String> params);

    @RequestMapping(value ="/pay/wechat/sdk/callback",method = RequestMethod.POST)
    public HttpResult wechatSdkCallback(@RequestBody Map<String, String> mapRest) throws Exception;

    @RequestMapping(value ="/pay/alipay/sdk/callback",method = RequestMethod.POST)
    public HttpResult aliPaySdkCallback(@RequestBody Map<String, String> mapRest) throws Exception;

    @RequestMapping(value ="/pay/lianwifi/sdk/callback",method = RequestMethod.POST)
    public HttpResult lianWifiSdkCallback(@RequestBody Map<String, String> mapRest) throws Exception;
}
