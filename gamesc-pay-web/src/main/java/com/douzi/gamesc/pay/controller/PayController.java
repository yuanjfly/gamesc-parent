package com.douzi.gamesc.pay.controller;


import com.douzi.gamesc.common.pojo.OrderParam;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.http.IpUtils;
import com.douzi.gamesc.pay.feign.ApiPayFeign;
import com.douzi.gamesc.http.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/***
 *
 * @Author:yuanjf
 * @Description:yuanjf
 * @date: 2020/3/4 16:19
 *
 ****/
@RestController
@Api(tags = "支付模块接口", description = "统一下单接口")
@RequestMapping(value = "/pay")
@Slf4j
public class PayController {

    @Autowired
    private ApiPayFeign apiPayFeign;

    /***
     * 用户下单
     */
    @ApiOperation(value = "用户下单")
    @RequestMapping(value = "/createOrder",method = RequestMethod.POST)
    public HttpResult createOrder(@RequestBody OrderParam orderParam,HttpServletRequest request){
        if(StringUtils.isAnyBlank(orderParam.getAppId(),orderParam.getChannel(),orderParam.getMasterId(),orderParam.getSign())
            || orderParam.getProductId()==null
            || orderParam.getProductPrice()==null
            || orderParam.getUserId()==null
            || orderParam.getClientType()==null){
            return HttpResult.error(Result.BIND_ERROR.getCode(),Result.BIND_ERROR.getMsg());
        }
        try {
            if(orderParam.getIp()==null||"".equals(orderParam.getIp())){
                orderParam.setIp(IpUtils.getIpAddr(request));
                if(orderParam.getIp()==null||"".equals(orderParam.getIp())){
                    orderParam.setIp("127.0.0.1");
                }
            }
            return  apiPayFeign.createOrder(orderParam);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(orderParam.getUserId()+" create Order  error:"+e.getMessage());
        }
        return  HttpResult.error();
    }

}