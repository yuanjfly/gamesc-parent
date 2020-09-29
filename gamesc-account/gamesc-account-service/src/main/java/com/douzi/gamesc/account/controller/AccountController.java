package com.douzi.gamesc.account.controller;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.account.pojo.SmsVerifyCodeRecord;
import com.douzi.gamesc.account.service.AccountService;
import com.douzi.gamesc.account.service.MongoDbService;
import com.douzi.gamesc.account.utils.AccountRedisUtils;
import com.douzi.gamesc.account.utils.RedisKeyUtils;
import com.douzi.gamesc.account.utils.SendReceiveMsgUtil;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.account.AccountMainInfo;
import com.douzi.gamesc.common.pojo.account.AccountSecurityInfo;
import com.douzi.gamesc.common.pojo.account.AccountThirdPartInfo;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.user.utils.MobileCheckUtil;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    @Autowired
    private AccountService accountServiceImpl;

    @Autowired
    private AccountRedisUtils accountRedisUtils;

    /**
     *获取主账号信息
     * @return
     */
    @RequestMapping("/getMainInfo/{userId}")
    public HttpResult getMainInfo(@PathVariable("userId")Long userId) {
        try {
            if(userId==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            String key = String.format(RedisKeyUtils.USERID_MAIN,userId);
            Object value = accountRedisUtils.get(key);
            if(value!=null){
                String account = (String)value;
                key = String.format(RedisKeyUtils.AccountMain,account);
                value = accountRedisUtils.get(key);
                if(value!=null){
                    JSONObject  accountMain = JSONObject.parseObject((String)value);
                    return HttpResult.ok(accountMain);
                }
            }
            //从数据库中查询
            AccountMainInfo accountMainInfo = accountServiceImpl.getAccountMainInfo(userId);
            return HttpResult.ok(accountMainInfo);
        }catch (Exception e) {
            log.error(userId+"  getMainInfo   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     * 通过手机号码找账号
     * @param phone
     * @param productId 产品Id
     * @return
     */
    @RequestMapping("/getUserByPhone/{productId}/{phone}")
    public HttpResult getUserByPhone(@PathVariable("productId")String productId,@PathVariable("phone")String phone) {
        try {
            AccountSecurityInfo info = accountServiceImpl.getAccountSecurityInfoByPhone(productId,phone);
            if(info==null||StringUtils.isBlank(info.getPhoneNum())){
                return HttpResult.error(Result.PHONE_USER_UNBIND.getCode(),Result.PHONE_USER_UNBIND.getMsg());
            }
            return HttpResult.ok(info);
        }catch (Exception e) {
            log.error(phone+" getUserByPhone  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *验证账号是否已经绑定
     * @return 0账号手机号都未绑定，1账号已经绑定，2手机号码已绑定
     */
    @RequestMapping("/verify/phone/{productId}/{userId}/{phone}")
    public HttpResult verifyPhone(@PathVariable("productId")String productId,@PathVariable("userId")Long userId,@PathVariable("phone")String phone) {
        try {
            if(userId==null||phone==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            AccountSecurityInfo info = accountServiceImpl.getAccountSecurityInfoByUserId(userId);
            if(info!=null&&!StringUtils.isBlank(info.getPhoneNum())){
                return HttpResult.ok(1);
            }
            info = accountServiceImpl.getAccountSecurityInfoByPhone(productId,phone);
            if(info!=null&&!StringUtils.isBlank(info.getPhoneNum())){
                return HttpResult.ok(2);
            }
            return HttpResult.ok(0);
        }catch (Exception e) {
            log.error(userId+"verify phone   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *绑定手机号码
     * @return
     */
    @RequestMapping("/bind/phone")
    public HttpResult bindPhone(@RequestParam("userId")Long userId,
            @RequestParam("phone")String phone,
            @RequestParam(value="address",required=false)String address) {
        boolean isOK = false ;
        try {
            if(userId==null||StringUtils.isBlank(phone)){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            //验证手机号格式是否正确
            if ("".equals(phone) || !MobileCheckUtil.isMobile(phone)) {
                return HttpResult.error(Result.PHONE_FORMAT_ERROR.getCode(),Result.PHONE_FORMAT_ERROR.getMsg());
            }
            AccountSecurityInfo info = accountServiceImpl.getAccountSecurityInfoByUserId(userId);
            if(info!=null&&!StringUtils.isBlank(info.getPhoneNum())){//已绑定手机
                return HttpResult.error(Result.PHONE_HAD_BIND.getCode(),Result.PHONE_HAD_BIND.getMsg());
            }
            if(info==null){
                info = new AccountSecurityInfo();
                info.setUserId(userId);
                info.setPhoneNum(phone);
                if(address!=null&&!"".equals(address)){
                    info.setHomeAddr(address);
                }
                info.setCreateTime(new Date());
                info.setUpdateTime(info.getCreateTime());
                accountServiceImpl.saveAccountSecurityInfo(info);
            }else{
                info.setPhoneNum(phone);
                info.setHomeAddr(address);
                info.setUpdateTime(new Date());
                accountServiceImpl.updateAccountSecurityInfo(info);
            }
            isOK = true ;
            String key = String.format(RedisKeyUtils.ACCOUNT_SECURITY,userId);
            Object value = accountRedisUtils.get(key);
            if(value!=null){
                JSONObject  accountSecurity = JSONObject.parseObject((String)value);
                accountSecurity.put("phone_num",phone);
                if(!StringUtils.isBlank(address)){
                    accountSecurity.put("home_addr",address);
                }
                long times = accountRedisUtils.getExpire(key);
                accountRedisUtils.set(key,accountSecurity.toJSONString(),times);
            }
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(userId+" bind phone  error....:"+e.getMessage());
            e.printStackTrace();
        }
        if(isOK){//为了处理存入redis错误忽略
            return HttpResult.ok();
        }else{
            return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
        }
    }

    /**
     * 获取微信登录账号
     * @param appId
     * @param unionId
     * @param type
     * @return
     */
    @RequestMapping("/getThirdPart/{appId}/{unionId}/{type}")
    public HttpResult getAccount(@PathVariable("appId")String appId,@PathVariable("unionId")String unionId,@PathVariable("type")Integer type) {
        try {
            if(StringUtils.isAnyBlank(appId,unionId)||type==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            AccountThirdPartInfo thirdPartInfo = accountServiceImpl.getAccountThirdPartInfo(appId,unionId,type);
            if(thirdPartInfo==null){
                return HttpResult.ok();
            }
            return HttpResult.ok(thirdPartInfo);
        }catch (Exception e) {
            log.error(unionId+"getThirdPart   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     * 通过账号获取第三方信息
     * @param userId
     * @param appId
     * @return
     */
    @RequestMapping("/getThirdPart/{userId}/{appId}")
    public HttpResult getAccountByUser(@PathVariable("userId")Long userId,@PathVariable("appId")String appId) {
        try {
            if(userId==null||appId==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            AccountThirdPartInfo thirdPartInfo = accountServiceImpl.getAccountThirdPartInfoByUserId(userId,appId);
            if(thirdPartInfo==null){
                return HttpResult.ok();
            }
            return HttpResult.ok(thirdPartInfo);
        }catch (Exception e) {
            log.error(userId+"getThirdPart   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }


    /**
     *绑定支付宝账号
     * @return
     */
    @RequestMapping("/bind/aliaccount")
    public HttpResult bindAliaccount(@RequestParam("productId")String productId,@RequestParam("userId")Long userId,@RequestParam("account")String account) {
        boolean isOK = false ;
        try {
            //查询支付是否绑定其他账号
            AccountSecurityInfo info = accountServiceImpl.getAccountSecurityInfoByAliAccount(productId,account);
            if(info!=null){
                return HttpResult.error(Result.ALI_HAD_BIND.getCode(),Result.ALI_HAD_BIND.getMsg());
            }
            info = accountServiceImpl.getAccountSecurityInfoByUserId(userId);
            if(info!=null&&!StringUtils.isBlank(info.getAlipayAddr())){//已绑定
                return HttpResult.error(Result.ACCOUNT_HAD_BIND_ALI.getCode(),Result.ACCOUNT_HAD_BIND_ALI.getMsg());
            }
            if(info==null){
                info = new AccountSecurityInfo();
                info.setUserId(userId);
                info.setAlipayAddr(account);
                info.setCreateTime(new Date());
                info.setUpdateTime(info.getCreateTime());
                accountServiceImpl.saveAccountSecurityInfo(info);
            }else{
                info.setAlipayAddr(account);
                info.setUpdateTime(new Date());
                accountServiceImpl.updateAccountSecurityInfo(info);
            }
            isOK = true ;
            String key = String.format(RedisKeyUtils.ACCOUNT_SECURITY,userId);
            Object value = accountRedisUtils.get(key);
            if(value!=null){
                JSONObject  accountSecurity = JSONObject.parseObject((String)value);
                accountSecurity.put("alipay_addr",account);
                long times = accountRedisUtils.getExpire(key);
                accountRedisUtils.set(key,accountSecurity.toJSONString(),times);
            }
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(userId+" bind ali account  error....:"+e.getMessage());
            e.printStackTrace();
        }
        if(isOK){//为了处理存入redis错误忽略
            return HttpResult.ok();
        }else{
            return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
        }
    }

}
