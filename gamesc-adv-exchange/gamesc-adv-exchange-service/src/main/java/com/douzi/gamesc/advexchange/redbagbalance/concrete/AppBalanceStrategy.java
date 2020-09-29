package com.douzi.gamesc.advexchange.redbagbalance.concrete;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.redbagbalance.ads.RebagBalanceStrategy;
import com.douzi.gamesc.advexchange.utils.WaChatPayUtil;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import com.douzi.gamesc.advexchange.vo.WechatChange;
import com.douzi.gamesc.advexchange.vo.WechatChangeBack;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppBalanceStrategy implements RebagBalanceStrategy {

    @Override
    public RedbagSendLog balanceOutWay(ExchangeRedbagRecord redbag,JSONObject redBagCfg) {

        WechatChange changepay =  WaChatPayUtil
                .createWechatChange(redbag.getOpenId(), redbag, redBagCfg);

        RedbagSendLog redbagSendlog = new RedbagSendLog();
        redbagSendlog.setUserId(redbag.getUserId());
        redbagSendlog.setNonceStr(changepay.getNonceStr());
        redbagSendlog.setMchBillno(changepay.getPartnerTradeno());
        redbagSendlog.setMchId(changepay.getMchId());
        redbagSendlog.setReOpenid(changepay.getOpenid());
        redbagSendlog.setTotalAmount(Integer.parseInt(changepay.getAmount()));
        redbagSendlog.setTotalNum(1);
        redbagSendlog.setClientIp(changepay.getSpbillCreateIp());
        redbagSendlog.setActName("");
        redbagSendlog.setRemark(changepay.getDesc());

        String xmlStr = WaChatPayUtil.getXmlByWechatChange(changepay);
        WechatChangeBack wenPayBack = WaChatPayUtil.weChatChangeSend(xmlStr, redBagCfg);
        if(wenPayBack.getReturnCode().equals("SUCCESS")&&wenPayBack.getResultCode().toUpperCase().equals("SUCCESS"))//发送通过
        {
            log.info(redbag.getUserId()+"企业支付零钱发送成功！！");
            redbagSendlog.setIsSend(1);
            redbagSendlog.setSendReson(wenPayBack.getReturnMsg());
        }else
        {
            log.info(redbag.getUserId()+"企业支付零钱发送失败。。。订单:"+changepay.getPartnerTradeno());
            redbagSendlog.setIsSend(0);
            redbagSendlog.setSendReson(wenPayBack.getErrCodeDes()==null?wenPayBack.getReturnMsg():wenPayBack.getErrCodeDes());
        }
        return  redbagSendlog ;
    }

    @Override
    public boolean isOK(int type) {
        return type==1;
    }
}
