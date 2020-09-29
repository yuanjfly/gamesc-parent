package com.douzi.gamesc.exchange.controller;


import com.douzi.gamesc.account.feign.ApiAccountFeign;
import com.douzi.gamesc.advexchange.fegin.ApiAdvExchangeFeign;
import com.douzi.gamesc.common.pojo.account.AccountSecurityInfo;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.user.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/***
 *
 * @Author:yuanjf
 * @Description:yuanjf
 * @date: 2020/3/4 16:19
 * 微信公众号绑定平台游戏账号
 ****/
@RestController
//@Api(tags = "猜歌达人微信公众号绑定平台游戏账号", description = "操作接口")
@RequestMapping("/caige/wechat")
@Slf4j
public class AdvWechatBindController {

    //麻将单包短信业务标示
    private  static final String business = "100003";

    @Autowired
    private ApiAccountFeign apiAccountFeign;

    @Autowired
    private ApiAdvExchangeFeign apiAdvExchangeFeign;



    /***
     * 发送手机验证码
     */
    /*@ApiOperation(value = "发送手机验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="productId",dataType="String",required=true,value="产品Id"),
            @ApiImplicitParam(name="phone",dataType="String",required=true,value="手机号码")
    })*/
    @RequestMapping(value = "/sendCode/{productId}/{phone}",method = RequestMethod.POST)
    public HttpResult sendCode(@PathVariable("productId")String productId,@PathVariable("phone")String phone){
        try {
            //检查手机是否有绑定账号
            HttpResult result = apiAccountFeign.getUserByPhone(productId,phone);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            return  apiAccountFeign.sendCode(business,phone);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(phone+"  wechat send phone code error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /***
     * 绑定手机号码"
     */
    /*@ApiOperation(value = "绑定手机号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",dataType="String",required=true,value="公众号应用id"),
            @ApiImplicitParam(name="productId",dataType="String",required=true,value="产品Id"),
            @ApiImplicitParam(name="openId",dataType="String",required=true,value="提现标示"),
            @ApiImplicitParam(name="phone",dataType="String",required=true,value="手机号码"),
            @ApiImplicitParam(name="code",dataType="String",required=true,value="验证码")
    })*/
    @RequestMapping(value = "/bindPhone/{appId}/{productId}/{openId}/{phone}/{code}",method = RequestMethod.POST)
    public HttpResult bindPhone(@PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("openId")String openId,
            @PathVariable("phone")String phone,
            @PathVariable("code")String code){
        try {
            //验证短信验证码
            HttpResult result = apiAccountFeign.verifyCode(business,phone,code);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            //通过手机查询账号
            result = apiAccountFeign.getUserByPhone(productId,phone);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            Map<Object, Object> object = (Map<Object, Object>)result.getData();
            AccountSecurityInfo accountSecurityInfo =  BeanUtils.convertMapToObject(object,AccountSecurityInfo.class);
            //添加绑定关系
            result = apiAdvExchangeFeign.bindUser(1,accountSecurityInfo.getUserId(),appId,productId,openId);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            object = (Map<Object, Object>)result.getData();
            ExchangeBindUser bindUser =  BeanUtils.convertMapToObject(object,ExchangeBindUser.class);
            return  HttpResult.ok(bindUser);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(openId+" wechat bind phone :"+phone+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

}