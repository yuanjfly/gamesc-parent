package com.douzi.gamesc.advexchange.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import com.douzi.gamesc.advexchange.service.RedBagBalanceService;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
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

/**
 * app微信零钱
 */
@Slf4j
@RestController
@RequestMapping("/advexchange/app")
public class RedBagAppBalanceController {

    @Autowired
    private RedBagBalanceService redBagBalanceServiceImpl;
    @Autowired
    private UserPropService userPropServiceImpl;
    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;
    @Autowired
    private MqProductSendSevice mqProductSendSeviceImpl;

    /**
     *零钱提现
     * @return
     */
    @RequestMapping("/advance")
    public HttpResult appAdvance(@RequestParam("userId")Long userId,
            @RequestParam("appId")String appId,
            @RequestParam("type")Integer type,
            @RequestParam("openId")String openId,
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
            redbag.setAppId(appId);
            redbag.setUserId(userId);
            redbag.setOpenId(openId);
            redbag.setType(type);
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
            if(redbag.getType()!=3){//话费不需要更新，要进行回调知道是否成功
                redbag.setStatus(1);
                redbag.setUpdateTime(new Date());
                redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
            }
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


    /**
     *话费回调请求
     * @return
     */
    @RequestMapping("/phonebill/check")
    public HttpResult phonebillcheck(@RequestParam("appId")String appId,
            @RequestParam("openId")String openId,
            @RequestParam("orderNo")String orderNo,
            @RequestParam("money")Integer money,
            @RequestParam("rs")Integer rs,
            @RequestParam(value = "message",required=false)String message) {

        try {
            ExchangeRedbagRecord redbag = new ExchangeRedbagRecord();
            redbag.setAppId(appId);
            redbag.setOpenId(openId);
            redbag.setOrderNo(orderNo);
            redbag = redBagBalanceServiceImpl.getExchangeRedbagRecord(redbag);
            if(redbag==null){
                return HttpResult.error("订单不存在");
            }else if(redbag.getMoney().intValue()!=money){
                return HttpResult.error("金额无法匹配");
            }else if(redbag.getStatus()==1){
                return HttpResult.ok("0000");
            }
            if(rs.intValue()!=0){//充值失败
                redbag.setRemark(message);
            }else{
                redbag.setStatus(1);
            }
            redbag.setUpdateTime(new Date());
            redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
            return HttpResult.ok("0000");
        }catch (Exception e) {
            log.error(orderNo+"..phonebill  check   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

}
