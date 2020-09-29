package com.douzi.gamesc.user.feign;


import com.douzi.gamesc.http.HttpResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("gamesc-user")
public interface ApiUserFeign {

    @RequestMapping("/user/getToken/{userId}/{token}")
    public HttpResult getToken(@PathVariable("userId")Long userId,@PathVariable("token")String token,@RequestParam(value = "productId",required = false)String productId);

    @RequestMapping("/user/getUserInfo/{userId}")
    public HttpResult getUserInfo(@PathVariable("userId")Long userId);

    @RequestMapping("/user/getGameUserCurrency/{userId}")
    public HttpResult getGameUserCurrency(@PathVariable("userId")Long userId) ;

    @RequestMapping("/user/getGameUserBackpack/{userId}/{propId}")
    public HttpResult getGameUserBackpack(@PathVariable("userId")Long userId,@PathVariable("propId")Integer propId);

    @RequestMapping("/userMaster/getRecord/{userId}/{listDate}")
    public HttpResult getMasterRecord(@PathVariable("userId")Long userId,
            @PathVariable("listDate")String listDate);

    @RequestMapping("/userMaster/updateRecord/{userId}/{listDate}")
    public HttpResult updateMasterRecord(@PathVariable("userId")Long userId,
            @PathVariable("listDate")String listDate);
}
