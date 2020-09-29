package com.douzi.gamesc.advexchange.redbagbalance.concrete;


import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.redbagbalance.ads.RebagBalanceStrategy;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.user.utils.HttpClient;
import com.douzi.gamesc.user.utils.Md5Utils;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneBillBalanceStrategy implements RebagBalanceStrategy {

    @Override
    public RedbagSendLog balanceOutWay(ExchangeRedbagRecord redbag, JSONObject redBagCfg) {
        RedbagSendLog redbagSendlog = new RedbagSendLog();
        redbagSendlog.setUserId(redbag.getUserId());
        redbagSendlog.setNonceStr(RandomChars.getRandomChars(32).toUpperCase());
        redbagSendlog.setMchBillno(redBagCfg.getString("appid")+redbag.getOrderNo());
        redbagSendlog.setMchId(redBagCfg.getString("appid"));
        redbagSendlog.setReOpenid(redbag.getOpenId());
        redbagSendlog.setTotalAmount(redbag.getMoney());
        redbagSendlog.setTotalNum(1);
        redbagSendlog.setClientIp(redbag.getIp());
        redbagSendlog.setActName("");
        redbagSendlog.setRemark("福利红包");
        try{

            HttpClient httpClient = new HttpClient(redBagCfg.getString("order_path"));
            Map<String, String> param = new HashMap<String, String>();
            param.put("CompanyID",redBagCfg.getString("appid"));
            param.put("InterfacePwd",redBagCfg.getString("InterfacePwd"));
            param.put("Mobile",redbag.getOpenId());
            param.put("Amount",redbag.getMoney()+"");
            param.put("OrderID",redbag.getOrderNo());
            String key = Md5Utils.md5(new StringBuffer()
                    .append(param.get("CompanyID"))
                    .append(param.get("InterfacePwd"))
                    .append(param.get("Mobile"))
                    .append(param.get("Amount"))
                    .append(param.get("OrderID"))
                    .append(redBagCfg.getString("RequestKey"))
                    .toString()).toLowerCase();
            param.put("key",key);
            httpClient.setParameter(param);
            httpClient.post();
            String xmlStr = httpClient.getContent();
            log.info(redbag.getUserId()+"兑换  畅天游话费下单返回结果："+xmlStr);
            if(xmlStr!=null&&xmlStr.indexOf("<result>0000</result>")>-1)
            {
                log.info("畅天游话费下单发送成功！！");
                redbagSendlog.setIsSend(1);
                redbagSendlog.setSendReson("");
            }else
            {
                log.error(redbag.getUserId()+"畅天游话费下单发送失败。。。订单:"+redbagSendlog.getMchBillno());
                redbagSendlog.setIsSend(0);
                redbagSendlog.setSendReson(xmlStr);
            }
        }catch (Exception e) {
            log.error(redbag.getUserId()+"兑换 调用畅天游话费流量充值接口异常："+e.getMessage());
            e.printStackTrace();
        }
        return redbagSendlog;
    }

    @Override
    public boolean isOK(int type) {
        return type==3;
    }
}
