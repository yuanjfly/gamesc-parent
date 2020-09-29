package com.douzi.gamesc.exchange.controller;


import com.douzi.gamesc.account.feign.ApiAccountFeign;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.user.feign.ApiUserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/***
 *
 * @Author:yuanjf
 * @Description:yuanjf
 * @date: 2020/3/4 16:19
 *
 ****/
@RestController
@Api(tags = "猜歌达人绑定手机号码", description = "操作接口")
@RequestMapping("/caige")
@Slf4j
public class AdvBindPhoneController {

    //猜歌达人短信业务标示
    private  static final int business = 100001;
    @Autowired
    private ApiAccountFeign apiAccountFeign;

    @Autowired
    private ApiUserFeign apiUserFeign;

    private final  String DeFual_ProductId = "yule_caige";//广告版


    /***
     * 发送手机验证码
     */
    @ApiOperation(value = "发送手机验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="phone",dataType="String",required=true,value="手机号码")
    })
    @RequestMapping(value = "/sendCode/{userId}/{token}/{phone}",method = RequestMethod.POST)
    public HttpResult sendCode(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("phone")String phone){
        try {
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出安全信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            if(info.containsKey("accountSecurity")){
                Map<Object, Object> accountSecurity = (Map<Object, Object>) info.get("accountSecurity");
                if(accountSecurity.containsKey("phone_num")
                        && !StringUtils.isBlank((String)accountSecurity.get("phone_num"))){
                    return HttpResult.error(Result.ACCOUNT_HAD_BIND.getCode(),Result.ACCOUNT_HAD_BIND.getMsg());
                }
            }
            result = apiAccountFeign.verifyPhone(DeFual_ProductId,userId,phone);
            if(result.getCode()!= HttpStatus.SC_OK){//检验失败
                return result ;
            }
            Integer isBind = (Integer) result.getData();
            if(isBind==1){//账号已经绑定
                return HttpResult.error(Result.ACCOUNT_HAD_BIND.getCode(),Result.ACCOUNT_HAD_BIND.getMsg());
            }else if(isBind==2){//手机号码已绑定其他账号
                return HttpResult.error(Result.PHONE_HAD_BIND.getCode(),String.format(Result.PHONE_HAD_BIND.getMsg(),phone));
            }
            return  apiAccountFeign.sendCode(business+"",phone);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+"  send phone code error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /***
     * 绑定手机号码"
     */
    @ApiOperation(value = "绑定手机号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="phone",dataType="String",required=true,value="手机号码"),
            @ApiImplicitParam(name="code",dataType="String",required=true,value="验证码"),
            @ApiImplicitParam(name="address",dataType="String",required=false,value="地址")
    })
    @RequestMapping(value = "/bindPhone/{userId}/{token}/{phone}/{code}",method = RequestMethod.POST)
    public HttpResult bindPhone(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("phone")String phone,
            @PathVariable("code")String code,
            @RequestParam(value="address",required=false)String address){
        try {

            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出安全信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            if(info.containsKey("accountSecurity")){
                Map<Object, Object> accountSecurity = (Map<Object, Object>) info.get("accountSecurity");
                if(accountSecurity.containsKey("phone_num")
                        && !StringUtils.isBlank((String)accountSecurity.get("phone_num"))){
                    return HttpResult.error(Result.PHONE_HAD_BIND.getCode(),Result.PHONE_HAD_BIND.getMsg());
                }
            }
            result =  apiAccountFeign.verifyCode(business+"",phone,code);
            if(result.getCode()!= HttpStatus.SC_OK){//验证码验证失败
                return result ;
            }
            //进入绑定
            if(address==null){
                address = "";
            }
            return apiAccountFeign.bindPhone(userId,phone,address);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" bind phone code error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

}