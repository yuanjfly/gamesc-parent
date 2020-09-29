package com.douzi.gamesc.advexchange.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.service.ExchangeProductService;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import com.douzi.gamesc.advexchange.service.RedBagBalanceService;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeCurrentRecord;
import com.douzi.gamesc.common.pojo.exhange.ExchangeProductDay;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.user.utils.BeanUtils;
import com.douzi.gamesc.user.utils.DateExtendUtil;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advexchange/caige")
@Slf4j
public class MahjongGameInnerController {

    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;

    @Autowired
    private UserPropService userPropServiceImpl;

    @Autowired
    private ExchangeProductService exchangeProductServiceImpl;

    @Autowired
    private MqProductSendSevice mqProductSendSeviceImpl;

    @Autowired
    private RedBagBalanceService redBagBalanceServiceImpl;

    /**
     * 游戏内提现获取最近面额
     * @param userId
     * @param vip
     * @param minBalance
     * @param selfTicket
     * @return
     */
    @RequestMapping(value = "/game/matching/{userId}/{vip}/{minBalance}/{selfTicket}/{type}/{costType}")
    public HttpResult gameMatching(@PathVariable("userId")Long userId,
            @PathVariable("vip")Long vip,
            @PathVariable("minBalance")Integer minBalance,
            @PathVariable("selfTicket")Long selfTicket,
            @PathVariable("type")Integer type,
            @PathVariable("costType")Integer costType){
        try {
            JSONArray cfg = etcdConfigServiceImpl.getExchangeShopCfg();
            if(cfg==null){
                log.error("get mahjongshop cfg is null");
                return HttpResult.error(Result.CFG_EXIST_ERROR.getCode(),Result.CFG_EXIST_ERROR.getMsg());
            }
            //得到用户vip等级
            int vipLevel = 0;
            JSONObject vipCfg = userPropServiceImpl.getUserVipLevelByExp(vip,9);
            if(vipCfg!=null&&vipCfg.containsKey("viplv")){
                vipLevel =  vipCfg.getIntValue("viplv");
            }
            int giftKind = (type==4?3:(type==3?2:1));
            for(int i=0;i<cfg.size();i++){
                JSONObject item = cfg.getJSONObject(i);
                if(item.containsKey("giftKind")&&item.getIntValue("giftKind")!=giftKind){//匹配对应类型的配置
                    cfg.remove(i);
                    i--;
                    continue;
                }
                if(item.containsKey("costype")&&item.getIntValue("costype")!=costType){//匹配道具消耗配置
                    cfg.remove(i);
                    i--;
                    continue;
                }
                /*if(item.containsKey("seeVIP")){
                    JSONArray value =  item.getJSONArray("seeVIP");
                    if(value!=null&&value.size()>1){
                        if(value.getIntValue(0)>vipLevel||value.getIntValue(1)<vipLevel){
                            cfg.remove(i);
                            i--;
                            continue;
                        }
                    }
                }*/
                if(item.containsKey("LimitRemove")&&item.getIntValue("LimitRemove")==1&&item.containsKey("allLimit")){
                    JSONArray value =  item.getJSONArray("allLimit");
                    if(value!=null&&value.size()>0){
                        int vipTotalLimit = 0;
                        if(vipLevel>=value.size()){//vip等级高于最好一个配置读取最后一个等级的次数
                            vipTotalLimit = value.getIntValue(value.size()-1);
                        }else{
                            vipTotalLimit = value.getIntValue(vipLevel);
                        }
                        //查询该商品该用户终身兑换的次数
                        int userTotal = exchangeProductServiceImpl.getExchangeGiftTotalByProductIdAndUserId(1,userId,item.getIntValue("id"));
                        if(userTotal>0&&userTotal>=vipTotalLimit){
                            cfg.remove(i);
                            i--;
                            continue;
                        }
                    }
                }
            }
            JSONObject returnCfg = null ;
            int maxCost = 0;
            for(int i=0;i<cfg.size();i++){
                JSONObject productIdCfg = cfg.getJSONObject(i);
                int currentCost = (Integer)productIdCfg.get("cost");
                //获取礼包配置信息
                JSONObject giftCfg = etcdConfigServiceImpl.getGiftListCfgByGiftId(productIdCfg.getIntValue("gift"));
                if(giftCfg==null||!giftCfg.containsKey("gift")){
                    continue;
                }
                JSONArray propList = giftCfg.getJSONArray("gift");
                for(int j = 0;j<propList.size();j++){
                    JSONArray propItem  = propList.getJSONArray(j);
                    if(propItem.getIntValue(0)==6){//红包点数
                        int num = getRandom(propItem.getIntValue(1),propItem.getIntValue(2));
                        if(selfTicket>=currentCost&&currentCost>=maxCost&&num>=minBalance){
                            returnCfg = productIdCfg;
                            maxCost = currentCost;
                        }

                    }
                }

            }
            if(returnCfg==null){
                String msg = (type==3?"TextID_Change_Tip_NotEnoughFee":"TextID_Change_Tip_NotEnough");
                return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),msg);
            }
            return  HttpResult.ok(returnCfg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner matching product   error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /**
     * 游戏内兑换物品
     * @return
     */
    @RequestMapping("/game/operate")
    public HttpResult gameOperate(@RequestParam("userId")Long userId,
            @RequestParam("productId")Integer productId,
            @RequestParam("vip")Long vip,
            @RequestParam("appId")String appId,
            @RequestParam("type")Integer type,
            @RequestParam("openId")String openId,
            @RequestParam(value="ip",required=false)String ip) {
        try {
            JSONObject result = new JSONObject();
            //得到用户vip等级
            int viplevel = 0;
            JSONObject vipCfg = userPropServiceImpl.getUserVipLevelByExp(vip,9);
            if(vipCfg!=null&&vipCfg.containsKey("viplv")){
                viplevel =  vipCfg.getIntValue("viplv");
            }
            //获取商品配置信息
            JSONObject productIdCfg = etcdConfigServiceImpl.getExchangeShopCfgByProductId(productId);
            if(productIdCfg==null||!productIdCfg.containsKey("gift")||!productIdCfg.containsKey("cost")){
                return HttpResult.error(Result.CFG_EXIST_ERROR.getCode(),Result.CFG_EXIST_ERROR.getMsg());
            }
            //获取礼包配置信息
            JSONObject giftCfg = etcdConfigServiceImpl.getGiftListCfgByGiftId(productIdCfg.getIntValue("gift"));
            if(giftCfg==null||!giftCfg.containsKey("gift")){
                return HttpResult.error(Result.CFG_EXIST_ERROR.getCode(),Result.CFG_EXIST_ERROR.getMsg());
            }
            //获取该礼包每日的次数
            int giftDayTotal =0 ;
            if(productIdCfg.containsKey("store")){
                giftDayTotal = exchangeProductServiceImpl.getExchangeGiftDayByProductId(productId);
                int giftDayCfg = productIdCfg.getIntValue("store");
                if(giftDayTotal>=giftDayCfg){
                    return HttpResult.error(Result.PRODUCT_NOT_STORE.getCode(),Result.PRODUCT_NOT_STORE.getMsg());
                }
            }
            //vip是否可兑换
            if(productIdCfg.containsKey("buyVIP")){
                JSONArray value =  productIdCfg.getJSONArray("buyVIP");
                if(value!=null&&value.size()>1){
                    if(value.getIntValue(0)>viplevel||value.getIntValue(1)<viplevel){
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),(viplevel-100>0?(viplevel-100):0)));
                    }
                }
            }

            //获取用户礼包终身次数
            int userTotal =0 ;
            if(productIdCfg.containsKey("allLimit")){
                JSONArray value =  productIdCfg.getJSONArray("allLimit");
                if(value!=null&&value.size()>0){
                    int vipTotalLimit = 0;
                    if(viplevel>=value.size()){//vip等级高于最好一个配置读取最后一个等级的次数
                        vipTotalLimit = value.getIntValue(value.size()-1);
                    }else{
                        vipTotalLimit = value.getIntValue(viplevel);
                    }
                    userTotal = exchangeProductServiceImpl.getExchangeGiftTotalByProductIdAndUserId(2,userId,productId);
                    if(userTotal>=vipTotalLimit){
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),productIdCfg.getString("name"),vipTotalLimit));
                    }
                }
            }

            //获取用户礼包每日限次
            ExchangeProductDay giftUserDay = null;
            if(productIdCfg.containsKey("dailyLimit")){
                JSONArray value =  productIdCfg.getJSONArray("dailyLimit");
                if(value!=null&&value.size()>0){
                    int uerDailyLimit = 0;
                    if(viplevel>=value.size()){//vip等级高于最好一个配置读取最后一个等级的次数
                        uerDailyLimit = value.getIntValue(value.size()-1);
                    }else{
                        uerDailyLimit = value.getIntValue(viplevel);
                    }
                    giftUserDay = exchangeProductServiceImpl.getExchangeGiftDayByProductIdAndUserId(userId,productId);
                    if(giftUserDay!=null&&giftUserDay.getHasNum()>=uerDailyLimit){
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),productIdCfg.getString("name"),uerDailyLimit));
                    }
                }
            }
            JSONArray outPro = new JSONArray();//输出给前端道具组
            JSONArray properties = new JSONArray();//发送道具同步
            JSONArray sendProp = new JSONArray();//rpc接口道具信息
            JSONObject operateItem = null;//道具属性
            //获取红包点数
            int redbagNum = 0;//红包点数
            int num =0 ;//道具数量
            JSONArray propList = giftCfg.getJSONArray("gift");
            for(int i = 0;i<propList.size();i++){
                JSONArray item  = propList.getJSONArray(i);
                operateItem = new JSONObject();
                num = getRandom(item.getIntValue(1),item.getIntValue(2));
                operateItem.put("prop_count",num);
                operateItem.put("prop_id",item.getIntValue(0));
                if(item.getIntValue(0)==6){//红包点数
                    redbagNum = num;
                }else{//红包道具无需同步，里面折算成红包零钱
                    properties.add(operateItem);
                    outPro.add(operateItem);
                }
                sendProp.add(operateItem);
            }
            JSONObject redBagCfg = etcdConfigServiceImpl.getRedBagWechatCfgByAppId(appId);
            if(redbagNum>0){
                //获取红包体现的配置新
                if(redBagCfg==null){
                    return HttpResult.error(Result.REDBAG_APP_CONFIG.getCode(),Result.REDBAG_APP_CONFIG.getMsg());
                }
                /*//查询当天兑换金额
                int allTotal = redBagBalanceServiceImpl.queryAllTotalByUserId(userId);
                if(allTotal+redbagNum>(redBagCfg.containsKey("all_limit")?redBagCfg.getIntValue("all_limit"):10000)){
                    return HttpResult.error(Result.REDBAG_ALL_LIMIT.getCode(),Result.REDBAG_ALL_LIMIT.getMsg());
                }*/
            }

            //扣奖券点数
            JSONArray costProp = new JSONArray();//rpc接口道具信息
            JSONObject data = new JSONObject();
            data.put("Route","GameDBServer.UpdateProp");
            JSONObject reqData = new JSONObject();
            reqData.put("UserID",userId);
            reqData.put("GIftID",productIdCfg.containsKey("gift"));
            reqData.put("AppID","");
            reqData.put("TradeNo","");
            reqData.put("SubChannelID",0);
            reqData.put("SourceID",productIdCfg.containsKey("sourceid")?productIdCfg.getIntValue("sourceid"):0);
            num = productIdCfg.getIntValue("cost");
            JSONObject costItem = new JSONObject();
            costItem.put("prop_count",0-num);
            costItem.put("prop_id",productIdCfg.containsKey("costype")?productIdCfg.getIntValue("costype"):3);
            costProp.add(costItem);
            properties.add(costItem);
            reqData.put("PropList",costProp);
            data.put("ReqData",reqData);
            JSONObject rs = exchangeProductServiceImpl.operatPropByRpc(data);
            //扣除道具失败
            if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                return HttpResult.error(Result.CURRENCY_COST_ERROR.getCode(),Result.CURRENCY_COST_ERROR.getMsg());
            }
            //发送道具
            if(sendProp.size()>0){
                reqData.put("PropList",sendProp);
                data.put("ReqData",reqData);
                rs = exchangeProductServiceImpl.operatPropByRpc(data);
                if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                    return HttpResult.error(Result.PROP_ADD_ERROR.getCode(),Result.PROP_ADD_ERROR.getMsg());
                }
            }
            //添加兑换信息
            exchangeProductServiceImpl.saveExchanegProduct(userId,productId,giftUserDay);
            //更新库存
            exchangeProductServiceImpl.updateDayStoreByProductId(userId,userTotal,productId,giftDayTotal,productIdCfg);
            //发送道具同步消息
            JSONObject saveField = new JSONObject();
            saveField.put("goodsid",productId);
            saveField.put("canceldelay",0);
            saveField.put("isexchange",1);
            mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
            //查询历史兑换次数
            result.put("total",exchangeProductServiceImpl.getAllExhangeTotal(userId));
            if(outPro.size()>0){
                result.put("hadSend",outPro);
            }
            if(redbagNum<=0){
                return HttpResult.ok(result);
            }
            //红包点数折算零钱
            sendProp.clear();
            reqData.put("SourceID",redBagCfg.containsKey("sourceId")?redBagCfg.getIntValue("sourceId"):0);
            operateItem = new JSONObject();
            operateItem.put("prop_count",0-redbagNum);
            operateItem.put("prop_id",6);
            sendProp.add(operateItem);
            reqData.put("PropList",sendProp);
            data.put("ReqData",reqData);
            rs = userPropServiceImpl.operatPropByRpc(data);
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
            redbag.setMoney(redbagNum);
            redbag.setOrderNo(DateExtendUtil.formatDate2String(new Date(),DateExtendUtil.FULL_DATE_FORMAT_TWO)+RandomChars
                    .getRandomNumber(1));
            redbag.setStatus(0);
            redbag.setPrizeTicket(redbagNum);
            if(ip!=null&&!"".equals(ip)){
                redbag.setIp(ip);
            }
            redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);
            result = redBagBalanceServiceImpl.sendRedBagByBindUser(redbag,redBagCfg);
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
            result.put("balance", productIdCfg.getIntValue("cost"));
            if(productIdCfg!=null){
                result.put("current_cfg",productIdCfg);
            }
            return HttpResult.ok(result);
        }catch (Exception e) {
            log.error("userid:"+userId+"  productid:"+productId+" exchange   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     * 我的兑换记录
     * @param userId
     * @return
     */
    @RequestMapping(value = "/exchange/list/{userId}")
    public HttpResult exchangeList(@PathVariable("userId")Long userId){
        try {
            List<ExchangeRedbagRecord>  list = redBagBalanceServiceImpl.getExchangeRedbagRecord(userId);
            if(list!=null&&list.size()>0){
                return HttpResult.ok(list);
            }
            return HttpResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner exchange list   error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /**
     * 退回兑换奖励
     * @return
     */
    @RequestMapping(value = "/exchange/refund/{userId}/{exchangeId}")
    public HttpResult getBackExchange(@PathVariable("userId")Long userId,@PathVariable("exchangeId")Long exchangeId){
        try {
            ExchangeRedbagRecord redbag = new ExchangeRedbagRecord();
            redbag.setUserId(userId);
            redbag.setId(exchangeId);
            redbag.setStatus(0);
            redbag = redBagBalanceServiceImpl.getExchangeRedbagRecord(redbag);
            if(redbag==null) {
                return HttpResult.error("兑换记录不存在");
            }
            //更新兑换记录状态
            redbag.setStatus(-2);
            redbag.setRemark("玩家申请退回");
            redbag.setUpdateTime(new Date());
            redBagBalanceServiceImpl.saveExchangeRedbagRecord(redbag);

            JSONArray costProp = new JSONArray();//rpc接口道具信息
            JSONArray properties = new JSONArray();//发送道具同步
            JSONObject data = new JSONObject();
            data.put("Route","GameDBServer.UpdateProp");
            JSONObject reqData = new JSONObject();
            reqData.put("UserID",userId);
            reqData.put("GIftID",0);
            reqData.put("AppID","");
            reqData.put("TradeNo","");
            reqData.put("SubChannelID",0);
            reqData.put("SourceID",0);
            JSONObject costItem = new JSONObject();
            costItem.put("prop_count",redbag.getMoney()*100);
            costItem.put("prop_id",8);
            costProp.add(costItem);
            properties.add(costItem);
            reqData.put("PropList",costProp);
            data.put("ReqData",reqData);
            JSONObject rs = exchangeProductServiceImpl.operatPropByRpc(data);
            //回补道具失败
            if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                return HttpResult.error(Result.CURRENCY_ADD_ERROR.getCode(),Result.CURRENCY_ADD_ERROR.getMsg());
            }

            //发送道具同步消息
            JSONObject saveField = new JSONObject();
            saveField.put("goodsid",0);
            saveField.put("canceldelay",0);
            saveField.put("isexchange",1);
            mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
            return HttpResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" getBackExchange  "+exchangeId+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    private  int getRandom(int min, int max)
    {
        int random = (int)(Math.random()*(max - min + 1) + min);
        return random;
    }



}
