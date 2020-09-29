package com.douzi.gamesc.advexchange.redbagbalance.concrete;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.douzi.gamesc.advexchange.redbagbalance.ads.RebagBalanceStrategy;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.user.utils.RandomChars;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliPayBalanceStrategy implements RebagBalanceStrategy {

    @Override
    public RedbagSendLog balanceOutWay(ExchangeRedbagRecord redbag,JSONObject redBagCfg) {

        RedbagSendLog redbagSendlog = new RedbagSendLog();
        redbagSendlog.setUserId(redbag.getUserId());
        redbagSendlog.setNonceStr(RandomChars.getRandomChars(32).toUpperCase());
        redbagSendlog.setMchBillno(redBagCfg.getString("mchid")+redbag.getOrderNo());
        redbagSendlog.setMchId(redBagCfg.getString("mchid"));
        redbagSendlog.setReOpenid(redbag.getOpenId());
        redbagSendlog.setTotalAmount(redbag.getMoney());
        redbagSendlog.setTotalNum(1);
        redbagSendlog.setClientIp(redbag.getIp());
        redbagSendlog.setActName("");
        redbagSendlog.setRemark("福利红包");

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        AlipayClient alipayClient = new DefaultAlipayClient(redBagCfg.getString("small_change_path"),
                redBagCfg.getString("appid"),
                redBagCfg.getString("privateKey"),//支付私钥
                "json",
                "UTF-8",
                redBagCfg.getString("alipayPublicKey"),//支付宝公钥
                "RSA2");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_biz_no", redbag.getOrderNo());
        bizContent.put("payee_type", "ALIPAY_LOGONID");
        bizContent.put("payee_account", redbag.getOpenId());
        bizContent.put("amount", ""+redbag.getMoney()/100.0);
        bizContent.put("remark", "福利红包");
        request.setBizContent(bizContent.toString());
        try {
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            log.info("alipay.fund.trans.toaccount.transfer 接口结果："+response.getMsg());
            if(response!=null&&response.isSuccess())
            {
                log.info("支付宝发送成功！！");
                redbagSendlog.setIsSend(1);
                redbagSendlog.setSendReson(response.getMsg());
            }else
            {
                log.error(redbag.getUserId()+"支付宝发送失败。。。订单:"+redbagSendlog.getMchBillno());
                redbagSendlog.setIsSend(0);
                redbagSendlog.setSendReson(response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("调用alipay.fund.trans.toaccount.transfer转账接口异常："+e.getMessage());
            e.printStackTrace();
        }
        return redbagSendlog;
    }

    @Override
    public boolean isOK(int type) {
        return type==2;
    }

}
