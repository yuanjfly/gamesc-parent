package com.douzi.gamesc.account.feign;


import com.douzi.gamesc.http.HttpResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("gamesc-account")
public interface ApiAccountFeign {

    @RequestMapping("/account/getMainInfo/{userId}")
    public HttpResult getMainInfo(@PathVariable("userId")Long userId);

    @RequestMapping("/account/getUserByPhone/{productId}/{phone}")
    public HttpResult getUserByPhone(@PathVariable("productId")String productId,@PathVariable("phone")String phone);

    @RequestMapping("/account/verify/phone/{productId}/{userId}/{phone}")
    public HttpResult verifyPhone(@PathVariable("productId")String productId,@PathVariable("userId")Long userId,@PathVariable("phone")String phone);

    @RequestMapping("/account/bind/phone")
    public HttpResult bindPhone(@RequestParam("userId")Long userId,
            @RequestParam("phone")String phone,
            @RequestParam(value="address",required=false)String address);

    @RequestMapping("/account/getThirdPart/{appId}/{unionId}/{type}")
    public HttpResult getAccount(@PathVariable("appId")String appId,@PathVariable("unionId")String unionId,@PathVariable("type")Integer type);

    @RequestMapping("/account/getThirdPart/{userId}/{appId}")
    public HttpResult getAccountByUser(@PathVariable("userId")Long userId,@PathVariable("appId")String appId);

    @RequestMapping("/account/bind/aliaccount")
    public HttpResult bindAliaccount(@RequestParam("productId")String productId,@RequestParam("userId")Long userId,@RequestParam("account")String account);

    @RequestMapping("/sms/send/code/{business}/{phone}")
    public HttpResult sendCode(@PathVariable("business")String business,@PathVariable("phone")String phone);

    @RequestMapping("/sms/verifyCode/{business}/{phone}/{code}")
    public HttpResult verifyCode(@PathVariable("business")String business,@PathVariable("phone")String phone,@PathVariable("code")String code);


}
