package com.douzi.gamesc.pay.controller.sdk;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.pay.feign.ApiPayFeign;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pay/qutt")
@Slf4j
public class QuttCallPackController {

    @Autowired
    private ApiPayFeign apiPayFeign;
    /**
     *
     * @param app_id 项目 id
     * @param open_id 用户在当前项目内的唯一标示
     * @param trade_no 订单号
     * @param total_fee 订单金额，单位分，1元=100
     * @param ext 扩展信息，json格式，比如充值的游戏区服等信息，透明转发
     * @param time unix 时间戳
     * @param sign
     * @return 签名
     */
    @RequestMapping("payCallback")
    public String payCallback(@RequestParam("app_id")String app_id,
            @RequestParam("open_id")String open_id,
            @RequestParam("trade_no")String trade_no,
            @RequestParam("total_fee")String total_fee,
            @RequestParam("ext")String ext,
            @RequestParam("time")String time,
            @RequestParam("sign")String sign){
        try{
            if(StringUtils.isAnyBlank(app_id,open_id,trade_no,total_fee,ext,time,sign))
            {
                return renderState(3, "ext 错误");
            }
            JSONObject extJson = JSONObject.parseObject(ext);
            if(!extJson.containsKey("orderNo"))
            {
                return renderState(3, "订单错误");
            }
            //收集参数
            Map<String, String> params =new HashMap<String, String>();
            params.put("app_id", app_id);
            params.put("open_id", open_id);
            params.put("trade_no", trade_no);
            params.put("total_fee", total_fee);
            params.put("ext", ext);
            params.put("time", time);
            params.put("sign", sign);

            HttpResult result = apiPayFeign.quttCallback(params);
            if(result.getCode()!=HttpStatus.SC_OK){
                return renderState(0, result.getMsg());
            }
            return  renderState(0, "ok");
        }catch (Exception e){
            e.printStackTrace();
            log.error(trade_no+" call back error:"+e.getMessage());
        }
        return renderState(94, "未知错误");
    }

    private String renderState(int code, String msg){

        JSONObject json = new JSONObject();
        json.put("message", msg);
        return json.toString();
    }
}
