package com.douzi.gamesc.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.game.GameUserBackpack;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;
import com.douzi.gamesc.user.service.UserPropService;
import com.douzi.gamesc.user.service.UserService;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.user.utils.RedisKeyUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserPropService userPropServiceImpl;

    /**
     * 获取用户token信息
     * @param userId
     * @param token
     * @param productId 产品线
     * @return
     */
    @RequestMapping("/getToken/{userId}/{token}")
    public HttpResult getToken(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @RequestParam(value = "productId",required = false)String productId) {
        try {
            if(userId==null||token==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            JSONObject redisToken = userServiceImpl.getAccountPropertyByJson(String.format(RedisKeyUtils.ACCOUNT_TOKEN,userId));
            if(redisToken==null){
                log.error("get account token is null");
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            if(!redisToken.containsKey("Token")||!token.equals(redisToken.getString("Token"))){//token验证不通过
                log.error("get account token is no pass");
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            if(!StringUtils.isBlank(productId)&&(!redisToken.containsKey("ProdID")||!productId.equals(redisToken.getString("ProdID")))){
                log.error("get account  productId is no pass");
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            JSONObject info = new JSONObject();
            Map<Object, Object> userProperty = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_PROPERTY,userId));
            if(userProperty!=null){
                info.put("userProperty",userProperty);
            }
            JSONObject accountSecurity = userServiceImpl.getAccountPropertyByJson(String.format(RedisKeyUtils.ACCOUNT_SECURITY,userId));
            if(accountSecurity!=null){
                info.put("accountSecurity",accountSecurity);
            }
            Map<Object, Object>  userCurrency = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_CURRENCY,userId));
            if(userCurrency!=null){
                info.put("userCurrency",userCurrency);
            }

            JSONObject  accountThirdInfo = userServiceImpl.getAccountPropertyByJson(String.format(RedisKeyUtils.ACCOUNT_THIRDINFO,userId));
            if(userCurrency!=null){
                info.put("accountThirdInfo",accountThirdInfo);
            }

            Map<Object, Object>  userBackPack = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_BACKPACK,userId));
            if(userCurrency!=null){
                info.put("userBackPack",userBackPack);
            }

            JSONObject  userOnline = userServiceImpl.getUserPropertyByJson(String.format(RedisKeyUtils.USER_ONLINE,userId));
            if(userOnline!=null){
                info.put("userOnline",userOnline);
            }

            return HttpResult.ok(info);
        }catch (Exception e) {
            log.error("get account token  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *获取用户相关属性
     * @return
     */
    @RequestMapping("/getUserInfo/{userId}")
    public HttpResult getUserInfo(@PathVariable("userId")Long userId) {
        try {
            JSONObject info = new JSONObject();
            Map<Object, Object> userProperty = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_PROPERTY,userId));
            if(userProperty==null){
                log.error("userId:{} get account  is null",userId);
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            info.put("userProperty",userProperty);
            JSONObject accountSecurity = userServiceImpl.getAccountPropertyByJson(String.format(RedisKeyUtils.ACCOUNT_SECURITY,userId));
            if(accountSecurity!=null){
                info.put("accountSecurity",accountSecurity);
            }
            Map<Object, Object>  userCurrency = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_CURRENCY,userId));
            if(userCurrency!=null){
                info.put("userCurrency",userCurrency);
            }
            return HttpResult.ok(info);
        }catch (Exception e) {
            log.error("get account info  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());

    }

    /**
     *获取用户货币信息
     * @return
     */
    @RequestMapping("/getGameUserCurrency/{userId}")
    public HttpResult getGameUserCurrency(@PathVariable("userId")Long userId) {
        try {
            GameUserCurrency gameUserCurrency = userPropServiceImpl.getGameUserCurrency(userId);
            return HttpResult.ok(gameUserCurrency);
        }catch (Exception e) {
            log.error("get gameUserCurrency info  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }


    /**
     *获取用户背包信息
     * @return
     */
    @RequestMapping("/getGameUserBackpack/{userId}/{propId}")
    public HttpResult getGameUserBackpack(@PathVariable("userId")Long userId,@PathVariable("propId")Integer propId) {
        try {
            GameUserBackpack gameUserBackpack = userPropServiceImpl.getGameUserBackpack(userId,propId);
            return HttpResult.ok(gameUserBackpack);
        }catch (Exception e) {
            log.error("get gameUserBackpack info  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }


}
