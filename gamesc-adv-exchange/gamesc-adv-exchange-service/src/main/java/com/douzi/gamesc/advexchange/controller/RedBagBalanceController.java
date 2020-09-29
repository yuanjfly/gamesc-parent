package com.douzi.gamesc.advexchange.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import com.douzi.gamesc.advexchange.service.RedBagBalanceService;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.user.utils.DateExtendUtil;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/advexchange/redbag")
public class RedBagBalanceController {

    @Autowired
    private RedBagBalanceService redBagBalanceServiceImpl;
    @Autowired
    private UserPropService userPropServiceImpl;
    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;
    @Autowired
    private MqProductSendSevice mqProductSendSeviceImpl;



    /**
     *通过appId相关信息
     * @return
     */
    @RequestMapping("/getAppInfo/{appId}")
    public HttpResult getAppInfo(@PathVariable("appId")String appId) {
        try {
            //获取app相关信息
            JSONObject redBagCfg = etcdConfigServiceImpl.getRedBagWechatCfgByAppId(appId);
            if(redBagCfg==null){
                return HttpResult.error(Result.REDBAG_APP_CONFIG.getCode(),Result.REDBAG_APP_CONFIG.getMsg());
            }
            return HttpResult.ok(redBagCfg);
        }catch (Exception e) {
            log.error(appId+"..redbag getAppInfo  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *通过第三方查找绑定的用户
     * @return
     */
    @RequestMapping("/getUser/{type}/{appId}/{productId}/{openId}")
    public HttpResult getUser(@PathVariable("type")Integer type,
            @PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("openId")String openId) {
        try {
            //查询是否已经绑定关系
            ExchangeBindUser bindUser = redBagBalanceServiceImpl.getExchangeBindOpenId(productId,appId,openId);
            if(bindUser==null||bindUser.getType().intValue()!=type){
                return HttpResult.ok();
            }
            return HttpResult.ok(bindUser);
        }catch (Exception e) {
            log.error(openId+"..redbag get bindUser  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *用户建立绑定关系
     * @return
     */
    @RequestMapping("/bindUser/{type}/{userId}/{appId}/{productId}/{openId}")
    public HttpResult bindUser(@PathVariable("type")Integer type,
            @PathVariable("userId")Long userId,
            @PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("openId")String openId) {
        try {
            //查询是否已经绑定关系
            ExchangeBindUser bindUser = redBagBalanceServiceImpl.getExchangeBindUser(appId,userId);
            if(bindUser!=null){
                return HttpResult.error(Result.REDBAG_USER_BIND.getCode(),Result.REDBAG_USER_BIND.getMsg());
            }
            bindUser = new ExchangeBindUser();
            bindUser.setAppId(appId);
            bindUser.setProductId(productId);
            bindUser.setUserId(userId);
            bindUser.setOpenId(openId);
            bindUser.setType(type);
            redBagBalanceServiceImpl.saveExchangeBindUser(bindUser);
            return HttpResult.ok(bindUser);
        }catch (Exception e) {
            log.error(userId+"..redbag bindUser  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *红包提现
     * @return
     */
    @RequestMapping("/advance")
    public HttpResult advance(@RequestParam("userId")Long userId,
            @RequestParam("appId")String appId,
            @RequestParam("productId")String productId,
            @RequestParam("money")Integer money,
            @RequestParam(value="ip",required=false)String ip) {
        try {
            JSONObject redBagCfg = etcdConfigServiceImpl.getRedBagWechatCfgByAppId(appId);
            if(redBagCfg==null){
                return HttpResult.error(Result.REDBAG_APP_CONFIG.getCode(),Result.REDBAG_APP_CONFIG.getMsg());
            }
            //查询当天兑换金额
            int allTotal = redBagBalanceServiceImpl.queryAllTotalByUserId(userId);
            if(allTotal+money>(redBagCfg.containsKey("all_limit")?redBagCfg.getIntValue("all_limit"):10000)){
                return HttpResult.error(Result.REDBAG_ALL_LIMIT.getCode(),Result.REDBAG_ALL_LIMIT.getMsg());
            }
            //查询是否已经绑定关系
            ExchangeBindUser bindUser = redBagBalanceServiceImpl.getExchangeBindUser(appId,userId);
            if(bindUser==null){
                return HttpResult.error(Result.REDBAG_USER_UNBIND.getCode(),Result.REDBAG_USER_UNBIND.getMsg());
            }
            JSONArray properties = new JSONArray();//发送道具同步
            //微信红包点数
            JSONObject data = new JSONObject();
            data.put("Route","GameDBServer.UpdateProp");
            JSONObject reqData = new JSONObject();
            reqData.put("UserID",userId);
            reqData.put("GIftID",0);
            reqData.put("AppID","");
            reqData.put("TradeNo","");
            reqData.put("SubChannelID",0);
            reqData.put("SourceID",redBagCfg.containsKey("sourceId")?redBagCfg.getIntValue("sourceId"):0);
            //扣红包券
            JSONArray sendProp = new JSONArray();
            JSONObject operateItem = new JSONObject();
            operateItem.put("prop_count",0-money);
            operateItem.put("prop_id",6);
            sendProp.add(operateItem);
            properties.add(operateItem);
            reqData.put("PropList",sendProp);
            data.put("ReqData",reqData);
            JSONObject rs = userPropServiceImpl.operatPropByRpc(data);
            //扣除道具失败
            if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                return HttpResult.error(Result.CURRENCY_COST_ERROR.getCode(),Result.CURRENCY_COST_ERROR.getMsg());
            }
            //添加红包记录
            ExchangeRedbagRecord redbag = new ExchangeRedbagRecord();
            redbag.setAppId(bindUser.getAppId());
            redbag.setUserId(bindUser.getUserId());
            redbag.setOpenId(bindUser.getOpenId());
            redbag.setType(bindUser.getType());
            redbag.setMoney(money);
            redbag.setOrderNo(DateExtendUtil.formatDate2String(new Date(),DateExtendUtil.FULL_DATE_FORMAT_TWO)+RandomChars
                    .getRandomNumber(1));
            redbag.setStatus(0);
            redbag.setPrizeTicket(money);
            if(ip!=null&&!"".equals(ip)){
                redbag.setIp(ip);
            }
            redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
            JSONObject result = redBagBalanceServiceImpl.sendRedBagByBindUser(redbag,redBagCfg);
            if(result!=null&&result.containsKey("msg")){
                redbag.setUpdateTime(new Date());
                redbag.setRemark(result.getString("msg"));
                redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
                return HttpResult.error(Result.REDBAG_APP_ERROR.getCode(),"提现失败请联系客服");
            }
            redbag.setStatus(1);
            redbag.setUpdateTime(new Date());
            redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
            //发送道具同步消息
            JSONObject saveField = new JSONObject();
            saveField.put("goodsid",0);
            saveField.put("canceldelay",0);
            saveField.put("isexchange",0);
            mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(userId+"..redbag advance  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }
}
