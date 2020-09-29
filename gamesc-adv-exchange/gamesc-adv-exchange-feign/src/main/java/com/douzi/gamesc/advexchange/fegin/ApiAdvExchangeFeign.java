package com.douzi.gamesc.advexchange.fegin;


import com.douzi.gamesc.http.HttpResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("gamesc-adv-exchange")
public interface ApiAdvExchangeFeign {

    @RequestMapping("/advexchange/caige/exchangecfg/{userId}/{vip}")
    HttpResult getExchangeShopCfg(@PathVariable("userId")Long userId,@PathVariable("vip")Long vip);

    @RequestMapping("/advexchange/caige/getCfgByProductId/{productId}")
    public HttpResult getCfgByProductId(@PathVariable("productId")int productId);

    @RequestMapping("/advexchange/caige/operate/{userId}/{productId}/{vip}")
    public HttpResult operate(@PathVariable("userId")Long userId,@PathVariable("productId")Integer productId,@PathVariable("vip")Long vip);

    @RequestMapping("/advexchange/caige/get/product/{userId}/{productId}")
    public HttpResult getProductByCheck(@PathVariable("userId")Long userId,@PathVariable("productId")Integer productId);


    @RequestMapping("/advexchange/redbag/getAppInfo/{appId}")
    public HttpResult getAppInfo(@PathVariable("appId")String appId);

    @RequestMapping("/advexchange/redbag/getUser/{type}/{appId}/{productId}/{openId}")
    public HttpResult getUser(@PathVariable("type")Integer type,
            @PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("openId")String openId);

    @RequestMapping("/advexchange/redbag/bindUser/{type}/{userId}/{appId}/{productId}/{openId}")
    public HttpResult bindUser(@PathVariable("type")Integer type,
            @PathVariable("userId")Long userId,
            @PathVariable("appId")String appId,
            @PathVariable("productId")String productId,
            @PathVariable("openId")String openId);

    @RequestMapping("/advexchange/redbag/advance")
    public HttpResult advance(@RequestParam("userId")Long userId,
            @RequestParam("appId")String appId,
            @RequestParam("productId")String productId,
            @RequestParam("money")Integer money,
            @RequestParam(value="ip",required=false)String ip);

    /**
     *零钱提现
     * @return
     */
    @RequestMapping("/advexchange/app/advance")
    public HttpResult appAdvance(@RequestParam("userId")Long userId,
            @RequestParam("appId")String appId,
            @RequestParam("type")Integer type,
            @RequestParam("openId")String openId,
            @RequestParam("money")Integer money,
            @RequestParam(value="ip",required=false)String ip);

    /**
     * 话费回调
     */
    @RequestMapping("/advexchange/app/phonebill/check")
    public HttpResult phonebillcheck(@RequestParam("appId")String appId,
            @RequestParam("openId")String openId,
            @RequestParam("orderNo")String orderNo,
            @RequestParam("money")Integer money,
            @RequestParam("rs")Integer rs,
            @RequestParam(value = "message",required=false)String message);


    @RequestMapping("/advexchange/gameshare/help/{activityId}/{openId}/{sharer}/{redbag}")
    public HttpResult help(@PathVariable("activityId")Integer activityId,
            @PathVariable("openId")String openId,
            @PathVariable("sharer")Long sharer,
            @PathVariable("redbag")Integer redbag) ;

    /**
     * 游戏内提现
     * @return
     */
    @RequestMapping("/advexchange/caige/game/operate")
    public HttpResult gameOperate(@RequestParam("userId")Long userId,
            @RequestParam("productId")Integer productId,
            @RequestParam("vip")Long vip,
            @RequestParam("appId")String appId,
            @RequestParam("type")Integer type,
            @RequestParam("openId")String openId,
            @RequestParam(value="ip",required=false)String ip);

    /**
     * 匹配最近面额
     * @return
     */
    @RequestMapping(value = "/advexchange/caige/game/matching/{userId}/{vip}/{minBalance}/{selfTicket}/{type}/{costType}")
    public HttpResult gameMatching(@PathVariable("userId")Long userId,
            @PathVariable("vip")Long vip,
            @PathVariable("minBalance")Integer minBalance,
            @PathVariable("selfTicket")Long selfTicket,
            @PathVariable("type")Integer type,
            @PathVariable("costType")Integer costType);

    /**
     * 我的兑换记录
     */
    @RequestMapping(value = "/advexchange/caige/exchange/list/{userId}")
    public HttpResult exchangeList(@PathVariable("userId")Long userId);

    /**
     * 商城道具类兑换
     */
    @RequestMapping("/advexchange/caige/lobby/operateProp")
    public HttpResult lobbyOperateProp(@RequestParam("userId")Long userId,
            @RequestParam("productId")Integer productId,
            @RequestParam("vip")Long vip,
            @RequestParam(value="ip",required=false)String ip);

    /**
     * 退回兑换奖励
     * @return
     */
    @RequestMapping(value = "/advexchange/caige/exchange/refund/{userId}/{exchangeId}")
    public HttpResult getBackExchange(@PathVariable("userId")Long userId,@PathVariable("exchangeId")Long exchangeId);

}
