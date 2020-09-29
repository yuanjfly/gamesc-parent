package com.douzi.gamesc.advexchange.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.service.ExchangeProductService;
import com.douzi.gamesc.advexchange.service.MqProductSendSevice;
import com.douzi.gamesc.advexchange.service.UserPropService;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeCurrentRecord;
import com.douzi.gamesc.common.pojo.exhange.ExchangeProductDay;
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

@RestController
@RequestMapping("/advexchange/caige")
@Slf4j
public class MahjongExchangeShopController {

    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;

    @Autowired
    private UserPropService userPropServiceImpl;

    @Autowired
    private ExchangeProductService exchangeProductServiceImpl;

    @Autowired
    private MqProductSendSevice mqProductSendSeviceImpl;

    /**
     * 读取趣头条麻将兑换商城配置信息
     * @return
     */
    @RequestMapping("/exchangecfg/{userId}/{vip}")
    public HttpResult exchangecfg(@PathVariable("userId")Long userId,@PathVariable("vip")Long vip) {
        try {
            JSONArray cfg = etcdConfigServiceImpl.getExchangeShopCfg();
            if(cfg==null){
                log.error("get mahjongshop cfg is null");
                return HttpResult.error(Result.CFG_EXIST_ERROR.getCode(),Result.CFG_EXIST_ERROR.getMsg());
            }
            //初始库存
            exchangeProductServiceImpl.updateDayStoreAll(cfg);
            //得到用户vip等级
            int vipLevel = 0;
            JSONObject vipCfg = userPropServiceImpl.getUserVipLevelByExp(vip,9);
            if(vipCfg!=null&&vipCfg.containsKey("viplv")){
                vipLevel =  vipCfg.getIntValue("viplv");
            }
            for(int i=0;i<cfg.size();i++){
                JSONObject item = cfg.getJSONObject(i);
                if(item.containsKey("seeVIP")){
                    JSONArray value =  item.getJSONArray("seeVIP");
                    if(value!=null&&value.size()>1){
                        if(value.getIntValue(0)>vipLevel||value.getIntValue(1)<vipLevel){
                            cfg.remove(i);
                            i--;
                            continue;
                        }
                    }
                }
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
            JSONObject data = new JSONObject();
            data.put("cfg",cfg);
            //获取当前是否有待审核记录
            /*/ExchangeCurrentRecord exhangeCurrentRecord = exchangeProductServiceImpl.getExchangeCurrentRecord(userId);
            if(exhangeCurrentRecord!=null){
                data.put("current",exhangeCurrentRecord);
                //过去该奖品的配置信息
                JSONObject productIdCfg = etcdConfigServiceImpl.getExchangeShopCfgByProductId(exhangeCurrentRecord.getProductId());
                if(productIdCfg!=null){
                    data.put("current_cfg",productIdCfg);
                }
            }*/
            return HttpResult.ok(data);
        }catch (Exception e) {
            log.error("get mahjongshop cfg  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     * 查询单个配置信息
     * @return
     */
    @RequestMapping("/getCfgByProductId/{productId}")
    public HttpResult getCfgByProductId(@PathVariable("productId")int productId) {
        try {
            JSONObject productIdCfg = etcdConfigServiceImpl.getExchangeShopCfgByProductId(productId);
            if(productIdCfg==null||!productIdCfg.containsKey("gift")||!productIdCfg.containsKey("cost")){
                return HttpResult.error(Result.CFG_EXIST_ERROR.getCode(),Result.CFG_EXIST_ERROR.getMsg());
            }
            return HttpResult.ok(productIdCfg);
        }catch (Exception e) {
            log.error("get mahjongshop cfg  by product "+productId+" error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     * 兑换物品
     * @return
     */
    @RequestMapping("/operate/{userId}/{productId}/{vip}")
    public HttpResult operate(@PathVariable("userId")Long userId,@PathVariable("productId")Integer productId,@PathVariable("vip")Long vip) {
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
            //当前是否有延迟兑换记录
            ExchangeCurrentRecord exhangeCurrentRecord = exchangeProductServiceImpl.getExchangeCurrentRecord(userId);
            if(exhangeCurrentRecord!=null){
                return HttpResult.error(Result.PRODUCT_HAVING_CHECK.getCode(),Result.PRODUCT_HAVING_CHECK.getMsg());
            }
            //获取该礼包每日的次数
            int giftDayTotal =0 ;
            if(productIdCfg.containsKey("store")){
                /*giftDayTotal = exchangeProductServiceImpl.getExchangeGiftDayByProductId(productId);
                int giftDayCfg = productIdCfg.getIntValue("store");
                if(giftDayTotal>=giftDayCfg){
                    return HttpResult.error(Result.PRODUCT_NOT_STORE.getCode(),Result.PRODUCT_NOT_STORE.getMsg());
                }*/
                //获取近两小时的库存缓存
                JSONArray cfg = etcdConfigServiceImpl.getExchangeShopCfg();
                int store = exchangeProductServiceImpl.getTwoHourStoreByProductId(productId,cfg);
                if(store<=0){
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
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),(viplevel-100>0?(viplevel-100):0)));
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
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),(viplevel-100>0?(viplevel-100):0)));
                    }
                }
            }

            JSONArray properties = new JSONArray();//发送道具同步
            JSONObject data = new JSONObject();
            data.put("Route","GameDBServer.UpdateProp");
            JSONObject reqData = new JSONObject();
            reqData.put("UserID",userId);
            reqData.put("GIftID",productIdCfg.containsKey("gift"));
            reqData.put("AppID","");
            reqData.put("TradeNo","");
            reqData.put("SubChannelID",0);
            reqData.put("SourceID",productIdCfg.containsKey("sourceid")?productIdCfg.getIntValue("sourceid"):0);
            //扣红包券
            JSONArray sendProp = new JSONArray();
            JSONObject operateItem = new JSONObject();
            int num = productIdCfg.getIntValue("cost");
            operateItem.put("prop_count",0-num);
            operateItem.put("prop_id",productIdCfg.containsKey("costype")?productIdCfg.getIntValue("costype"):3);
            sendProp.add(operateItem);
            properties.add(operateItem);
            reqData.put("PropList",sendProp);
            data.put("ReqData",reqData);
            JSONObject rs = exchangeProductServiceImpl.operatPropByRpc(data);
            //扣除道具失败
            if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                return HttpResult.error(Result.CURRENCY_COST_ERROR.getCode(),Result.CURRENCY_COST_ERROR.getMsg());
            }
            //是否免审
            /*int vipFastChange = 0 ;
            if(vipCfg!=null&&vipCfg.containsKey("vipFastChange")){
                vipFastChange =  vipCfg.getIntValue("vipFastChange");
            }*/
            result.put("status",0);//正常到账
            //解析道具
            JSONArray delayProp = null;
            //是否具有延迟发放必须非免审
            /*if(productIdCfg.containsKey("delay")){
                if(vipFastChange==0){//延迟到账
                    delayProp = new JSONArray();
                }else{//延迟但是vip优先秒到账
                    result.put("status",1);
                }
            }*/
            JSONArray propList = giftCfg.getJSONArray("gift");
            sendProp = new JSONArray();
            for(int i = 0;i<propList.size();i++){
                JSONArray item  = propList.getJSONArray(i);
                operateItem = new JSONObject();
                num = getRandom(item.getIntValue(1),item.getIntValue(2));
                operateItem.put("prop_count",num);
                operateItem.put("prop_id",item.getIntValue(0));
                if(item.getIntValue(0)==6&&delayProp!=null){//红包券延迟
                    delayProp.add(operateItem);
                }else{
                    sendProp.add(operateItem);
                    properties.add(operateItem);
                }
            }
            //加道具(排除延迟道具)
            if(sendProp.size()>0){
                reqData.put("PropList",sendProp);
                data.put("ReqData",reqData);
                rs = exchangeProductServiceImpl.operatPropByRpc(data);
                if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                    return HttpResult.error(Result.PROP_ADD_ERROR.getCode(),Result.PROP_ADD_ERROR.getMsg());
                }
            }

            //延迟道具发送延迟mq消息 查看vip是否免审
            if(delayProp!=null&&delayProp.size()>0){
                result.put("status",2);
                reqData.put("PropList",delayProp);
                data.put("ReqData",reqData);
                JSONObject mqInfo = new JSONObject();
                mqInfo.put("event","exchange_delay");
                mqInfo.put("distinct_id",userId);
                mqInfo.put("product_id",productId);
                mqInfo.put("properties",data);
                mqProductSendSeviceImpl.sendMessage(mqInfo.toJSONString(),productIdCfg.getIntValue("delay")*60*1000);
                //插入延迟商品信息
                ExchangeCurrentRecord record = exchangeProductServiceImpl.saveExhangeCurrentRecord(userId,productId,delayProp.toJSONString(),data.toJSONString(),productIdCfg.getIntValue("delay"));
                result.put("current",record);
                if(productIdCfg!=null){
                    result.put("current_cfg",productIdCfg);
                }
            }
            //添加兑换信息
            exchangeProductServiceImpl.saveExchanegProduct(userId,productId,giftUserDay);
            //更新库存
            exchangeProductServiceImpl.updateTwoHourStoreByProductId(userId,userTotal,productId,productIdCfg);
            //发送道具同步消息
            JSONObject saveField = new JSONObject();
            saveField.put("goodsid",productId);
            saveField.put("canceldelay",0);
            saveField.put("isexchange",1);
            mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
            //查询历史兑换次数
            result.put("total",exchangeProductServiceImpl.getAllExhangeTotal(userId));
            if(sendProp.size()>0){
                result.put("hadSend",sendProp);
            }
            return HttpResult.ok(result);
        }catch (Exception e) {
            log.error("userid:"+userId+"  productid:"+productId+" exchange   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }


    private  int getRandom(int min, int max)
    {
        int random = (int)(Math.random()*(max - min + 1) + min);
        return random;
    }


    /**
     * 领取已经审核通过的奖励
     * @return
     */
    @RequestMapping("/get/product/{userId}/{productId}")
    public HttpResult getProductByCheck(@PathVariable("userId")Long userId,@PathVariable("productId")Integer productId) {
        try {
            //查询是否存在待审核通过的奖品
            ExchangeCurrentRecord record = exchangeProductServiceImpl.getExchangeCurrentRecord(userId);
            if(record==null||record.getProductId().intValue()!=productId.intValue()){//奖品不存在
                return HttpResult.error(Result.PRODUCT_NOT_EXIST.getCode(),Result.PRODUCT_NOT_EXIST.getMsg());
            }
            if(record.getStatus()!=1){//奖品审核中
                return HttpResult.error(Result.PRODUCT_IN_CHECK.getCode(),Result.PRODUCT_IN_CHECK.getMsg());
            }
            //更新记录
            if(exchangeProductServiceImpl.updateExhangeCurrentRecord(userId,productId,2)){
                //发送道具
                JSONObject data = JSONObject.parseObject(record.getRpcInfo());
                JSONObject rs = exchangeProductServiceImpl.operatPropByRpc(data);
                if(rs==null||!rs.containsKey("Code")||rs.getIntValue("Code")!=HttpStatus.SC_OK){
                    return HttpResult.error(Result.PROP_ADD_ERROR.getCode(),Result.PROP_ADD_ERROR.getMsg());
                }
                //发送道具同步消息
                if(record.getProductDetail()!=null){
                    JSONArray properties = JSONArray.parseArray(record.getProductDetail());
                    JSONObject saveField = new JSONObject();
                    saveField.put("goodsid",productId);
                    saveField.put("canceldelay",0);
                    saveField.put("isexchange",0);
                    mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
                }
            }
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(userId+" get Product "+productId+" ByCheck  error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }


    /**
     * 商城道具类兑换
     * @return
     */
    @RequestMapping("/lobby/operateProp")
    public HttpResult gameOperate(@RequestParam("userId")Long userId,
            @RequestParam("productId")Integer productId,
            @RequestParam("vip")Long vip,
            @RequestParam(value="ip",required=false)String ip) {
        try {
            JSONObject result = new JSONObject();
            //得到用户vip等级
            int viplevel = 0;
            int radio = 0;//金币加成比例
            JSONObject vipCfg = userPropServiceImpl.getUserVipLevelByExp(vip,9);
            if(vipCfg!=null&&vipCfg.containsKey("viplv")){
                viplevel =  vipCfg.getIntValue("viplv");
            }
            //获取VIP道具加成比例
            if(vipCfg!=null&&vipCfg.containsKey("changeGold")){
                radio =  vipCfg.getIntValue("changeGold");
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
                /*giftDayTotal = exchangeProductServiceImpl.getExchangeGiftDayByProductId(productId);
                int giftDayCfg = productIdCfg.getIntValue("store");
                if(giftDayTotal>=giftDayCfg){
                    return HttpResult.error(Result.PRODUCT_NOT_STORE.getCode(),Result.PRODUCT_NOT_STORE.getMsg());
                }*/
                //获取近两小时的库存缓存
                JSONArray cfg = etcdConfigServiceImpl.getExchangeShopCfg();
                int store = exchangeProductServiceImpl.getTwoHourStoreByProductId(productId,cfg);
                if(store<=0){
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
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),(viplevel-100>0?(viplevel-100):0)));
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
                        return HttpResult.error(Result.PRODUCT_VIP_LIMIT.getCode(),String.format(Result.PRODUCT_VIP_LIMIT.getMsg(),(viplevel-100>0?(viplevel-100):0)));
                    }
                }
            }

            JSONArray properties = new JSONArray();//发送道具同步
            JSONArray sendProp = new JSONArray();//rpc接口道具信息
            JSONObject operateItem = null;//道具属性
            int num =0 ;//道具数量
            JSONArray propList = giftCfg.getJSONArray("gift");
            for(int i = 0;i<propList.size();i++){
                JSONArray item  = propList.getJSONArray(i);
                operateItem = new JSONObject();
                num = getRandom(item.getIntValue(1),item.getIntValue(2));
                if(item.getIntValue(0)==7&&radio>0){//金币要看是否加成
                    num = (int) Math.floor(num*(100+radio)/100.0);
                }
                operateItem.put("prop_count",num);
                operateItem.put("prop_id",item.getIntValue(0));
                properties.add(operateItem);
                sendProp.add(operateItem);
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
            exchangeProductServiceImpl.updateTwoHourStoreByProductId(userId,userTotal,productId,productIdCfg);
            //发送道具同步消息
            JSONObject saveField = new JSONObject();
            saveField.put("goodsid",productId);
            saveField.put("canceldelay",0);
            saveField.put("isexchange",1);
            mqProductSendSeviceImpl.sycGameProp(userId,"exchange_result",saveField,properties);
            //查询历史兑换次数
            result.put("total",exchangeProductServiceImpl.getAllExhangeTotal(userId));
            if(sendProp.size()>0){
                result.put("hadSend",sendProp);
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
}
