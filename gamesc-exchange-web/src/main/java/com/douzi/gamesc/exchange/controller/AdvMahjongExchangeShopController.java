package com.douzi.gamesc.exchange.controller;


import com.douzi.gamesc.account.feign.ApiAccountFeign;
import com.douzi.gamesc.advexchange.fegin.ApiAdvExchangeFeign;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.account.AccountThirdPartInfo;
import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.game.GameUserBackpack;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.http.IpUtils;
import com.douzi.gamesc.user.feign.ApiUserFeign;
import com.douzi.gamesc.user.utils.BeanUtils;
import com.douzi.gamesc.user.utils.Md5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/***
 *
 * @Author:yuanjf
 * @Description:yuanjf
 * @date: 2020/3/4 16:19
 *
 ****/
@RestController
@Api(tags = "猜歌达人大厅兑换", description = "操作接口")
@Slf4j
@RequestMapping("/caige")
public class AdvMahjongExchangeShopController {

    @Autowired
    private ApiAdvExchangeFeign apiAdvExchangeFeign;

    @Autowired
    private ApiUserFeign apiUserFeign;

    private final  String DeFual_ProductId = "yule_caige";//广告版

    /***
     * 获取兑换配置
     */
    @ApiOperation(value = "获取兑换商城配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token")
    })
    @RequestMapping(value = "/mahjong/exchangecfg/{userId}/{token}",method = RequestMethod.POST)
    public HttpResult createOrder(@PathVariable("userId")Long userId,@PathVariable("token")String token){
        try {
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //取出用户VIP等级
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            /*if(!info.containsKey("userProperty")){
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            Map<Object, Object> userProperty = (Map<Object, Object>) info.get("userProperty");
            long vip_exp = userProperty.containsKey("vip_exp")?Long.parseLong((String)userProperty.get("vip_exp")):0;*/

            //获取新版vip经验值 道具9
            long vip_exp =0 ;
            if(info.containsKey("userBackPack")){
                Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                vip_exp = userBackPack.containsKey("9")?Long.parseLong((String)userBackPack.get("9")):0;
            }
            return  apiAdvExchangeFeign.getExchangeShopCfg(userId,vip_exp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" get  mahjong exchangecfg  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /**
     * 兑换商城道具类兑换

    @ApiOperation(value = "纯道具类兑换")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="productId",dataType="Int",required=true,value="兑换商品Id"),
    })*/
    @RequestMapping(value = "/lobby/operateProp/{userId}/{token}/{productId}",method = RequestMethod.POST)
    public HttpResult gameOperateProp(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("productId")int productId,
            HttpServletRequest request){
        try {
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //获取用户信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            if(info.containsKey("userOnline")){//纯道具兑换游戏中无法兑换
                return HttpResult.error(Result.EXCHANE_USER_ONLINE.getCode(),"TextID_Change_Tip_PLaying");
            }
            //获取商品配置
            result = apiAdvExchangeFeign.getCfgByProductId(productId);
            if(result.getCode()!= HttpStatus.SC_OK){//配置不存在
                return result;
            }
            Map<Object, Object> productIdCfg = (Map<Object, Object>)result.getData();
            Integer giftKind = productIdCfg.containsKey("giftKind")?((Integer)productIdCfg.get("giftKind")):0;
            if(giftKind.intValue()!=4){//非道具类商品拒绝兑换
                return  HttpResult.error(Result.EXCHANE_NOT_PROP.getCode(),Result.EXCHANE_NOT_PROP.getMsg());
            }
            //获取新版vip经验值 道具9
            long vip_exp =0 ;
            if(info.containsKey("userBackPack")){
                Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                vip_exp = userBackPack.containsKey("9")?Long.parseLong((String)userBackPack.get("9")):0;
            }

            Integer propId = productIdCfg.containsKey("costype")?((Integer)productIdCfg.get("costype")):3;
            if(productIdCfg.containsKey("costype")&&propId!=3){//配置了其他道具
                long backPack = 0;
                if(info.containsKey("userBackPack")){
                    Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                    backPack = userBackPack.containsKey(String.valueOf(propId))?Long.parseLong((String)userBackPack.get(String.valueOf(propId))):0;
                    if(backPack<(Integer)productIdCfg.get("cost")){
                        return HttpResult.error(Result.PROP_SHORT.getCode(),Result.PROP_SHORT.getMsg());
                    }
                }
                //获取玩家身上实道具时
                result = apiUserFeign.getGameUserBackpack(userId,propId);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                long self_prize_ticket = 0;
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserBackpack gameUserBackpack =  BeanUtils.convertMapToObject(object,GameUserBackpack.class);
                    self_prize_ticket =gameUserBackpack.getPropCount();
                }
                if(self_prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PROP_SHORT.getCode(),Result.PROP_SHORT.getMsg());
                }
            }else{
                //取出红包券缓存
                long prize_ticket = 0;
                if(info.containsKey("userCurrency")){
                    Map<Object, Object> userProperty = (Map<Object, Object>) info.get("userCurrency");
                    prize_ticket = userProperty.containsKey("prize_ticket")?Long.parseLong((String)userProperty.get("prize_ticket")):0;
                }
                if(prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),Result.PRIZE_TICKET_SHORT.getMsg());
                }
                //获取玩家身上实时货币
                result = apiUserFeign.getGameUserCurrency(userId);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                long self_prize_ticket = 0;
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserCurrency gameUserCurrency =  BeanUtils.convertMapToObject(object,GameUserCurrency.class);
                    self_prize_ticket =gameUserCurrency.getPrizeTicket();
                }
                if(self_prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),Result.PRIZE_TICKET_SHORT.getMsg());
                }
            }
            //进入兑换流程
            return apiAdvExchangeFeign.lobbyOperateProp(userId,productId,vip_exp,IpUtils.getIpAddr(request));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner operate product "+productId+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /***
     * 商城兑换
     */
    /*@ApiOperation(value = "商城兑换")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="productId",dataType="Int",required=true,value="兑换商品Id")
    })*/
    @RequestMapping(value = "/operate/{userId}/{token}/{productId}",method = RequestMethod.POST)
    public HttpResult operate(@PathVariable("userId")Long userId,@PathVariable("token")String token,@PathVariable("productId")int productId){
        try {
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //获取用户信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();

            result = apiAdvExchangeFeign.getCfgByProductId(productId);
            if(result.getCode()!= HttpStatus.SC_OK){//配置不存在
                return result;
            }
            Map<Object, Object> productIdCfg = (Map<Object, Object>)result.getData();

            //取出用户VIP等级
            /*long vip_exp =0 ;
            if(info.containsKey("userProperty")){
                Map<Object, Object> userProperty = (Map<Object, Object>) info.get("userProperty");
                vip_exp = userProperty.containsKey("vip_exp")?Long.parseLong((String)userProperty.get("vip_exp")):0;
            }*/
            //获取新版vip经验值 道具9
            long vip_exp =0 ;
            if(info.containsKey("userBackPack")){
                Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                vip_exp = userBackPack.containsKey("9")?Long.parseLong((String)userBackPack.get("9")):0;
            }
            int costType = productIdCfg.containsKey("costype")?(Integer)productIdCfg.get("costype"):3;
            if(costType!=3){//配置了其他道具
                long backPack = 0;
                if(info.containsKey("userBackPack")){
                    Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                    backPack = userBackPack.containsKey(String.valueOf(costType))?Long.parseLong((String)userBackPack.get(String.valueOf(costType))):0;
                    if(backPack<(Integer)productIdCfg.get("cost")){
                        return HttpResult.error(Result.PROP_SHORT.getCode(),Result.PROP_SHORT.getMsg());
                    }
                }
                //获取玩家身上实道具时
                result = apiUserFeign.getGameUserBackpack(userId,costType);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                long self_prize_ticket = 0;
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserBackpack gameUserBackpack =  BeanUtils.convertMapToObject(object,GameUserBackpack.class);
                    self_prize_ticket =gameUserBackpack.getPropCount();
                }
                if(self_prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PROP_SHORT.getCode(),Result.PROP_SHORT.getMsg());
                }
            }else{
                //取出红包券缓存
                long prize_ticket = 0;
                if(info.containsKey("userCurrency")){
                    Map<Object, Object> userProperty = (Map<Object, Object>) info.get("userCurrency");
                    prize_ticket = userProperty.containsKey("prize_ticket")?Long.parseLong((String)userProperty.get("prize_ticket")):0;
                }
                if(prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),Result.PRIZE_TICKET_SHORT.getMsg());
                }
                //获取玩家身上实时货币
                result = apiUserFeign.getGameUserCurrency(userId);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                long self_prize_ticket = 0;
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserCurrency gameUserCurrency =  BeanUtils.convertMapToObject(object,GameUserCurrency.class);
                    self_prize_ticket =gameUserCurrency.getPrizeTicket();
                }
                if(self_prize_ticket<(Integer)productIdCfg.get("cost")){
                    return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),Result.PRIZE_TICKET_SHORT.getMsg());
                }
            }
            //进入兑换流程
            return apiAdvExchangeFeign.operate(userId,productId,vip_exp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" operate product "+productId+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /**
     * 领取审核通过的奖品
     * @param userId
     * @param token
     * @param productId
     * @return
     */
    /*@ApiOperation(value = "领取审核通过的奖品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="productId",dataType="Int",required=true,value="兑换商品Id")
    })*/
    @RequestMapping(value ="/get/product/{userId}/{token}/{productId}",method = RequestMethod.POST)
    public HttpResult getProductByCheck(@PathVariable("userId")Long userId,@PathVariable("token")String token,@PathVariable("productId")Integer productId){
        try {
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            return apiAdvExchangeFeign.getProductByCheck(userId,productId) ;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" get ProductByCheck product "+productId+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /**
     * app零钱提现
     * @param userId
     * @param token
     * @return
     */
   /* @ApiOperation(value = "app零钱提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="appId",dataType="String",required=true,value="提现应用Id"),
            @ApiImplicitParam(name="type",dataType="Int",required=true,value="提现类型（1零钱、2支付宝、3话费）"),
            @ApiImplicitParam(name="money",dataType="Int",required=true,value="提现金额（单位分）")
    })*/
    @RequestMapping(value ="/app/cashBalance/{userId}/{token}/{appId}/{type}/{money}",method = RequestMethod.POST)
    public HttpResult cashBalance(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("appId")String appId,
            @PathVariable("type")Integer type,
            @PathVariable("money")Integer money,
            HttpServletRequest request){
        try {
            if(type<1||type>3){
                return HttpResult.error(Result.REQUEST_PARAM_ERROR.getCode(),Result.REQUEST_PARAM_ERROR.getMsg());
            }
            //验证token
            HttpResult userInfo = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(userInfo.getCode()!= HttpStatus.SC_OK){//token验证失败
                return userInfo ;
            }
            //获取用户信息
            Map<Object, Object> info  = (Map<Object, Object>) userInfo.getData();
            String openId = "";
            if(!info.containsKey("accountSecurity")){
                return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
            }
            Map<Object, Object> accountSecurity = (Map<Object, Object>) info.get("accountSecurity");
            if(type==1){//微信零钱提现
                if(!accountSecurity.containsKey("wx_app_id")||!accountSecurity.containsKey("wx_open_id")){
                    return HttpResult.error(Result.REDBAG_WECHAT_UNBIND.getCode(),Result.REDBAG_WECHAT_UNBIND.getMsg());
                }
                String wx_app_id = (String)accountSecurity.get("wx_app_id");
                String wx_open_id = (String)accountSecurity.get("wx_open_id");
                if(StringUtils.isAnyBlank(wx_app_id,wx_open_id)||!wx_app_id.equals(appId)){
                    return HttpResult.error(Result.REDBAG_WECHAT_UNBIND.getCode(),Result.REDBAG_WECHAT_UNBIND.getMsg());
                }
                openId = wx_open_id;
            }else if(type==2){//支付宝转账
                if(!accountSecurity.containsKey("alipay_addr")
                        || StringUtils.isBlank((String)accountSecurity.get("alipay_addr"))){
                    return HttpResult.error(Result.REDBAG_ALIPAY_UNBIND.getCode(),Result.REDBAG_ALIPAY_UNBIND.getMsg());
                }
                openId = (String)accountSecurity.get("alipay_addr");
            }else if(type==3){//话费
                if(!accountSecurity.containsKey("phone_num")
                        || StringUtils.isBlank((String)accountSecurity.get("phone_num"))){
                    return HttpResult.error(Result.REDBAG_PHONE_UNBIND.getCode(),Result.REDBAG_PHONE_UNBIND.getMsg());
                }
                openId = (String)accountSecurity.get("phone_num");
            }
            //获取道具是否足够
            HttpResult result = apiUserFeign.getGameUserBackpack(userId,6);
            if(result.getCode()!= HttpStatus.SC_OK){
                return result ;
            }
            int propCount = 0 ;
            Map<Object, Object> object;
            if(result.getData()!=null){
                object = (Map<Object, Object>)result.getData();
                propCount = (Integer) object.get("propCount");
            }
            if(propCount<money){
                return HttpResult.error(Result.REDBAG_PROP_NOT_ENOUGH.getCode(),Result.REDBAG_PROP_NOT_ENOUGH.getMsg());
            }
            return  apiAdvExchangeFeign.appAdvance(userId,appId,type,openId,money,IpUtils
                    .getIpAddr(request));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" app  cashBalance  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /**
     * 话费充值回调
     * @return
     */
    @RequestMapping(value ="/phonebill/callback",method = RequestMethod.GET)
    public String phonebillCallback(@RequestParam("CompanyID")String companyId,
            @RequestParam("Mobile")String mobile,
            @RequestParam("Amount")Integer amount,
            @RequestParam("OrderID")String orderId,
            @RequestParam("Result")Integer rs,
            @RequestParam("Key")String key,
            @RequestParam(value = "Message",required=false)String message,
            HttpServletRequest request){
        try {
            HttpResult result = apiAdvExchangeFeign.getAppInfo(companyId);
            if(result.getCode()!= HttpStatus.SC_OK){//获取配置失败
                return tranRequestXml("商户不存在") ;
            }
            //取出配置信息
            Map<Object, Object> info  = (Map<Object, Object>)result.getData();
            String preKey = Md5Utils.md5(new StringBuffer()
                    .append(companyId)
                    .append(mobile)
                    .append(amount)
                    .append(orderId)
                    .append(rs)
                    .append(info.get("AccessKey"))
                    .toString()).toLowerCase();
            if(key==null||!preKey.equals(key)){//签名验证失败
                return tranRequestXml("签名不通过") ;
            }
            result = apiAdvExchangeFeign.phonebillcheck(companyId,mobile,orderId,amount,rs,message);
            return tranRequestXml(result.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(orderId+"  phonebill Callback  error: "+e.getMessage());
        }
        return  tranRequestXml("error") ;
    }

    private  String tranRequestXml(String result){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        sb.append("<ctuport><result>");
        sb.append(result);
        sb.append("</result></ctuport>");
        return sb.toString();
    }

}