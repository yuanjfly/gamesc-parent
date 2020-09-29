package com.douzi.gamesc.advexchange.redbagbalance.concrete;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.redbagbalance.ads.RebagBalanceStrategy;
import com.douzi.gamesc.advexchange.vo.RedbagSendLog;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.user.utils.HttpClient;
import com.douzi.gamesc.user.utils.RandomChars;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 趣头条金币兑换
 */
@Slf4j
public class QuttBalanceStrategy implements RebagBalanceStrategy {

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
            Map<String,String> param = new HashMap<>();
            param.put("app_id", redBagCfg.getString("appid"));
            param.put("open_id", redbag.getOpenId()); // 上传业务参数键值对
            param.put("coin_num", redbag.getMoney()*100+"");//趣金币比例1:10000
            param.put("trade_no", redbag.getOrderNo());
            String s = sign(param,redBagCfg.getString("appKey"));
            param.put("sign", s);
            httpClient.setParameter(param);
            httpClient.post();
            JSONObject rs = JSONObject.parseObject(httpClient.getContent());
            log.info(redbag.getUserId()+"兑换  趣金币返回结果："+rs.toJSONString());
            if(rs.containsKey("code")&&rs.getIntValue("code")==0)
            {
                log.info(redbag.getUserId()+"===趣金币兑换成功发送成功！！");
                redbagSendlog.setIsSend(1);
                redbagSendlog.setSendReson("");
            }else
            {
                log.error(redbag.getUserId()+"===趣金币兑换失败。。。订单:"+redbagSendlog.getMchBillno());
                redbagSendlog.setIsSend(0);
                redbagSendlog.setSendReson(rs.getString("message"));
            }
        }catch (Exception e) {
            log.error(redbag.getUserId()+"兑换 调用趣金币兑换接口异常："+e.getMessage());
            e.printStackTrace();
        }
        return redbagSendlog;
    }

    @Override
    public boolean isOK(int type) {
        return type==4;
    }

    public String getMD5(String need2Encode) throws NoSuchAlgorithmException {
        byte[] buf = need2Encode.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buf);
        byte[] tmp = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : tmp) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public String sign(Map<String,String> val,String appKey) {
        val.remove("sign");
        val.put("app_key", appKey);
        ArrayList<String> keys = new ArrayList<>();
        for (String key : val.keySet()) {
            keys.add(key);
        }
        keys.sort(new Comparator<String>() {
            @Override
            public int compare(String l,String r) {
                int i = l.compareTo(r);
                if (i>0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        String r = "";
        String hashed = "";
        for (String i : keys) {
            r += i+val.get(i);
        }
        try {
            hashed = getMD5(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        val.remove("app_key");
        return hashed;
    }

}
