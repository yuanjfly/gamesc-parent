package com.douzi.gamesc.exchange.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.account.feign.ApiAccountFeign;
import com.douzi.gamesc.advexchange.fegin.ApiAdvExchangeFeign;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.exchange.utils.WeixinUtil;
import com.douzi.gamesc.exchange.utils.WeixinUtil.UserInfo;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.http.IpUtils;
import com.douzi.gamesc.user.feign.ApiUserFeign;
import com.douzi.gamesc.user.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
//@Api(tags = "猜歌达人微信公众号零钱提现", description = "操作接口")
@RequestMapping("/caige/wechat")
@Slf4j
public class AdvWechatCashBalanceController {

    @Autowired
    private ApiAccountFeign apiAccountFeign;

    @Autowired
    private ApiUserFeign apiUserFeign;

    @Autowired
    private ApiAdvExchangeFeign apiAdvExchangeFeign;

    /***
     * 通过微信公众号自动获取账号
     */
    /*@ApiOperation(value = "通过微信公众号自动获取账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",dataType="String",required=true,value="公众号应用id"),
            @ApiImplicitParam(name="productId",dataType="String",required=true,value="产品Id"),
            @ApiImplicitParam(name="code",dataType="String",required=true,value="前端授权过来的code")
    })*/
    @RequestMapping(value = "/getBindUser/{appId}/{productId}/{code}",method = RequestMethod.POST)
    public HttpResult getBindUser(@PathVariable("appId")String appId,@PathVariable("productId")String productId,@PathVariable("code")String code){
        try {
            HttpResult result = apiAdvExchangeFeign.getAppInfo(appId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出配置信息
            Map<Object, Object> info  = (Map<Object, Object>)result.getData();
            //通过code获取openid和unionid
            String str = WeixinUtil.getToken(code,appId,(String) info.get("appsecret"));
            String openId = (String) JSON.parseObject(str, Map.class).get("openid");
            String accessToken = (String) JSON.parseObject(str, Map.class).get("access_token");
            String refreshToken = (String) JSON.parseObject(str, Map.class).get("refresh_token");
            UserInfo userInfo = WeixinUtil.getSnsUserInfo(openId, accessToken);
            //查询账号
            //第一步查找之前是否绑定过账号
            result = apiAdvExchangeFeign.getUser(1,appId,productId,openId);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            ExchangeBindUser bindUser = null;
            if(result.getData()!=null){//已找到公众号曾今绑定过的账号
                Map<Object, Object> object = (Map<Object, Object>)result.getData();
                bindUser =  BeanUtils.convertMapToObject(object,ExchangeBindUser.class);
            }else{//未找到则查找是否有微信登录的账号
                /*result = apiAccountFeign.getAccount(apkAppId,userInfo.getUnionid(),2);
                if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                    return result ;
                }
                if(result.getData()!=null){//找到登录过的微信账号
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    AccountThirdPartInfo thirdPartInfo =  BeanUtils.convertMapToObject(object,AccountThirdPartInfo.class);
                    //找到后自动添加到公众号绑定关系
                    result =  apiExchangeFeign.bindUser(1,thirdPartInfo.getUserId(),appId,openId);
                    if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                        return result ;
                    }
                    object = (Map<Object, Object>)result.getData();
                    bindUser =  BeanUtils.convertMapToObject(object,ExchangeBindUser.class);
                }*/
            }
            //未有账号返回
            if(bindUser==null){
                bindUser = new ExchangeBindUser();
                bindUser.setType(1);
                bindUser.setAppId(appId);
                bindUser.setOpenId(openId);
                bindUser.setProductId(productId);
            }
            JSONObject data = new JSONObject();
            data.put("bindUser",bindUser);
            if(userInfo!=null&&!StringUtils.isBlank(userInfo.getHeadimgurl())){
                data.put("headimgurl",userInfo.getHeadimgurl());
            }
            data.put("once_limit_down",info.containsKey("once_limit_down")?info.get("once_limit_down"):10000);
            data.put("once_limit_up",info.containsKey("once_limit_up")?info.get("once_limit_up"):10000);
            return HttpResult.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(appId+"  get wehcat Bind User error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /*@ApiOperation(value = "获取账号微信零钱券")
    @ApiImplicitParams({
            @ApiImplicitParam(name="productId",dataType="String",required=true,value="产品Id"),
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="平台用户id")
    })*/
    @RequestMapping(value = "/getRedBagBalance/{productId}/{userId}",method = RequestMethod.GET)
    public HttpResult getRedBagBalance(@PathVariable("productId")String productId,@PathVariable("userId")Long userId){
        try {
            JSONObject data = new JSONObject();
            HttpResult result = apiAccountFeign.getMainInfo(userId);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            Map<Object, Object> object;
            if(result.getData()!=null){
                object = (Map<Object, Object>)result.getData();
                data.put("nickName",object.containsKey("nickName")?object.get("nickName"):"***");
            }else{
                data.put("nickName","***");
            }
            result = apiUserFeign.getGameUserBackpack(userId,6);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            if(result.getData()!=null){
                object = (Map<Object, Object>)result.getData();
                data.put("propCount",object.get("propCount"));
            }else{
                data.put("propCount",0);
            }
            return HttpResult.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+"  get RedBag Balance error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /***
     * 红包提现
     */
    /*@ApiOperation(value = "红包提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",dataType="String",required=true,value="应用Id"),
            @ApiImplicitParam(name="productId",dataType="String",required=true,value="产品Id"),
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="平台用户Id"),
            @ApiImplicitParam(name="money",dataType="Long",required=true,value="提现金额(分)")
    })*/
    @RequestMapping(value = "/cashBalance/{appId}/{productId}/{userId}/{money}",method = RequestMethod.POST)
    public HttpResult cashBalance(@PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("userId")Long userId,
            @PathVariable("money")Integer money,
            HttpServletRequest request){
        try {
            HttpResult result = apiAdvExchangeFeign.getAppInfo(appId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出配置信息
            Map<Object, Object> info  = (Map<Object, Object>)result.getData();
            int once_limit_down = info.containsKey("once_limit_down")?(Integer) info.get("once_limit_down"):10000;
            int once_limit_up = info.containsKey("once_limit_up")?(Integer) info.get("once_limit_up"):10000;
            if(money<once_limit_down||money>once_limit_up){
               return HttpResult.error(Result.REDBAG_APP_ERROR.getCode(),"单笔金额不正确");
            }
            //验证红包点数
            result = apiUserFeign.getGameUserBackpack(userId,6);
            if(result.getCode()!= HttpStatus.SC_OK){//查询账号出现异常
                return result ;
            }
            Map<Object, Object> object = (Map<Object, Object>)result.getData();
            int propCount = 0;
            if(object.containsKey("propCount")){
                propCount = (Integer) object.get("propCount");
            }
            if(money>propCount){//微信红包券不够
                return HttpResult.error(Result.REDBAG_PROP_NOT_ENOUGH.getCode(),Result.REDBAG_PROP_NOT_ENOUGH.getMsg());
            }
            //发红包
            return  apiAdvExchangeFeign.advance(userId,appId,productId,money,IpUtils.getIpAddr(request));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" wechat cash Balance :"+money+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }
}