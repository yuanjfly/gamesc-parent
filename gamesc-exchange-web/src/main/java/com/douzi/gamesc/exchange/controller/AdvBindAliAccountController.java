package com.douzi.gamesc.exchange.controller;


import com.douzi.gamesc.account.feign.ApiAccountFeign;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.user.feign.ApiUserFeign;
import com.douzi.gamesc.user.utils.MobileCheckUtil;
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
@Api(tags = "猜歌达人绑定支付宝账号", description = "操作接口")
@RequestMapping("/caige")
@Slf4j
public class AdvBindAliAccountController {

    //麻将单包短信业务标示
    private  static final int business = 100001;
    @Autowired
    private ApiAccountFeign apiAccountFeign;

    @Autowired
    private ApiUserFeign apiUserFeign;

    private final  String DeFual_ProductId = "yule_caige";


    /***
     * 绑定支付宝账号
     */
    @ApiOperation(value = "绑定支付宝账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="account",dataType="String",required=true,value="支付宝账号（手机号码或者邮箱）"),
    })
    @RequestMapping(value = "/bindAliaccount/{userId}/{token}/{account}",method = RequestMethod.POST)
    public HttpResult bindAliaccount(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("account")String account){
        try {

            if(userId==null||StringUtils.isBlank(account)){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            //验证格式是否正确
            if ("".equals(account) || (!MobileCheckUtil.isEmail(account)&& !MobileCheckUtil.isMobile(account))) {
                return HttpResult.error(Result.ALI_FORMAT_ERROR.getCode(),Result.ALI_FORMAT_ERROR.getMsg());
            }

            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出安全信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            if(info.containsKey("accountSecurity")){
                Map<Object, Object> accountSecurity = (Map<Object, Object>) info.get("accountSecurity");
                if(accountSecurity.containsKey("alipay_addr")
                        && !StringUtils.isBlank((String)accountSecurity.get("alipay_addr"))){
                    return HttpResult.error(Result.ACCOUNT_HAD_BIND_ALI.getCode(),Result.ACCOUNT_HAD_BIND_ALI.getMsg());
                }
            }
            return apiAccountFeign.bindAliaccount(DeFual_ProductId,userId,account);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" bind Aliaccount  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

}