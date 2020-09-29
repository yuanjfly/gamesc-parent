package com.douzi.gamesc.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.OrderParam;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.order.PlatAppConfig;
import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatMasterConfig;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.common.pojo.order.PlatProductConfig;
import com.douzi.gamesc.pay.cache.SDKCacheManager;
import com.douzi.gamesc.pay.sdk.ISDKOrderListener;
import com.douzi.gamesc.pay.sdk.ISDKScript;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.service.PlatAppConfigService;
import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import com.douzi.gamesc.pay.service.PlatMasterConfigService;
import com.douzi.gamesc.pay.service.PlatOrderPreService;
import com.douzi.gamesc.pay.service.PlatProductConfigService;
import com.douzi.gamesc.user.utils.BeanUtils;
import com.douzi.gamesc.user.utils.DateExtendUtil;
import com.douzi.gamesc.user.utils.FastDFSClient;
import com.douzi.gamesc.user.utils.Md5Utils;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Autowired
    private PlatMasterConfigService platMasterConfigServiceImpl;

    @Autowired
    private PlatOrderPreService platOrderPreServiceImpl;

    @Autowired
    private PlatProductConfigService platProductConfigServiceImpl;

    @Autowired
    private PlatAppConfigService platAppConfigServiceImpl;

    @Autowired
    SDKCacheManager sdkCacheManager;

    /**
     * 下单
     * @param orderParam
     * @return
     */
    @RequestMapping("/createOrder")
    public HttpResult createOrder(@RequestBody OrderParam orderParam) {
        try {
            PlatChannelMaster platChannelMaster = platChannelMasterServiceImpl.
                    getPlatChannelMaster(orderParam.getChannel(),orderParam.getAppId(),orderParam.getMasterId());
            if(platChannelMaster==null||platChannelMaster.getPayOpenFlag()==0){
                return HttpResult.error(Result.REQUEST_PARAM_ERROR.getCode(),Result.REQUEST_PARAM_ERROR.getMsg());
            }
            PlatMasterConfig platMasterConfig = platMasterConfigServiceImpl
                    .getPlatMasterConfig(orderParam.getMasterId());

            //签名验证
            if(!verifySign(orderParam,"sign")){
                return HttpResult.error(Result.PARAM_SIGN_ERROR.getCode(),Result.PARAM_SIGN_ERROR.getMsg());
            }
            //判断订单价格
            /*PlatProductConfig productConfig = platProductConfigServiceImpl.getPlatProductConfig(orderParam.getAppId(),orderParam.getProductId());
            if(productConfig==null||productConfig.getProductPrice().intValue()!=orderParam.getProductPrice().intValue()){//该应用对应的支付商品不存在
                log.error("the productConfig is not found. appId:"+orderParam.getAppId());
                return HttpResult.error(Result.PRODUCT_ID_ERROR.getCode(),Result.PRODUCT_ID_ERROR.getMsg());
            }*/

            //创建订单
            ISDKScript script = sdkCacheManager.getSDKScript(platChannelMaster);
            if(script == null){
                log.error("the ISDKScript is not found. channelID:"+orderParam.getChannel());
                return HttpResult.error(Result.REQUEST_PARAM_ERROR.getCode(),Result.REQUEST_PARAM_ERROR.getMsg());
            }

            JSONObject data = new JSONObject();
            log.info("start create order....:"+orderParam.getChannel()+"....:"+orderParam.getUserId());
            PlatOrderPre orderPre = createPlatOrderPre(platChannelMaster,orderParam);
            platOrderPreServiceImpl.insert(orderPre);

            script.onGetOrderID(platChannelMasterServiceImpl, platMasterConfigServiceImpl,orderPre, new ISDKOrderListener() {
                @Override
                public void onSuccess(String jsonStr) {
                    data.put("orderNo", orderPre.getOrderNo());
                    data.put("extension", jsonStr);
                    log.info("The onGetOrderID extension is "+data.toString());
                }
                @Override
                public void onFailed(String err) {
                    log.error(err);
                    data.put("orderID", orderPre.getOrderNo());
                    data.put("extension", "");
                }
            });
            return HttpResult.ok(data);
        } catch (Exception e) {
            log.error("get order create error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.ORDER_CREATE_ERROR.getCode(),Result.ORDER_CREATE_ERROR.getMsg());
    }

    /**
     * 创建订单实体类
     * @param orderParam
     * @return
     */
    private PlatOrderPre createPlatOrderPre(PlatChannelMaster platChannelMaster,OrderParam orderParam){
        Date date = new Date();
        String orderNo = DateExtendUtil.formatDate2String(date,"yyyyMMddHHmmssSSS") + RandomChars
                .getRandomNumber(6);
        PlatOrderPre orderPre = new PlatOrderPre();
        orderPre.setChannelId(orderParam.getChannel());
        orderPre.setOrderNo(orderNo);
        orderPre.setMoney(orderParam.getProductPrice());
        orderPre.setState(0);
        orderPre.setAppId(orderParam.getAppId());
        orderPre.setUserId(orderParam.getUserId());
        orderPre.setMasterId(orderParam.getMasterId());
        orderPre.setOpenId(orderParam.getOpenId());
        orderPre.setCpAppId(platChannelMaster.getCpAppId());
        orderPre.setGiftId(orderParam.getProductId());
        orderPre.setGiftName(orderParam.getProductName());
        orderPre.setGiftDesc(orderParam.getProductDesc());
        orderPre.setIp(orderParam.getIp());
        orderPre.setClientType(orderParam.getClientType());
        orderPre.setExtension(orderParam.getExtension());
        orderPre.setCreateTime(date);
        return orderPre ;
    }

    /**
     * 验证签名
     * @param orderParam
     * @param signField
     * @return
     * @throws Exception
     */
    private boolean verifySign(OrderParam orderParam,String signField) throws Exception {
        //获取app相关信息
        PlatAppConfig appConfig = platAppConfigServiceImpl.getPlatAppConfig(orderParam.getAppId());
        Map<String,String> map = BeanUtils.convertObjToMap(orderParam);
        //特殊处理，去掉ip字段
        if(map.containsKey("ip")){
            map.remove("ip");
        }
        if(!map.containsKey(signField))
        {
            return false;
        }
        String sign = map.remove(signField);
        SortedMap<String, String> sort = new TreeMap<String, String>(map);
        String preSign = Md5Utils.paramByMapToUrlString(sort,"key",appConfig.getAppKey());
        if(!preSign.equals(sign)){
            log.error("api sign not pass preSign:"+preSign+"....sign="+sign);
            return false ;
        }
        return  true;
    }
}
