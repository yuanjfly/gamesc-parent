package com.douzi.gamesc.exchange.controller;


import com.douzi.gamesc.advexchange.fegin.ApiAdvExchangeFeign;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.common.pojo.game.GameUserBackpack;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.http.HttpStatus;
import com.douzi.gamesc.http.IpUtils;
import com.douzi.gamesc.user.feign.ApiUserFeign;
import com.douzi.gamesc.user.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/***
 *  游戏内红包提现
 * @Author:yuanjf
 * @Description:yuanjf
 * @date: 2020/3/4 16:19
 *
 ****/
@RestController
@Api(tags = "猜歌达人游戏内红包提现", description = "操作接口")
@Slf4j
@RequestMapping("/caige")
public class AdvMahjongGameInnerExchangeController {

    @Autowired
    private ApiAdvExchangeFeign apiAdvExchangeFeign;

    @Autowired
    private ApiUserFeign apiUserFeign;

    private final  String DeFual_ProductId = "yule_caige";//广告版

    /***
     * 匹配最近提现面额
     */
    @ApiOperation(value = "获取最近提现面额")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="type",dataType="Int",required=true,value="提现类型（1零钱、2支付宝、3话费、4趣金币）"),
            @ApiImplicitParam(name="costType",dataType="Int",required=true,value="道具消耗ID"),
    })
    @RequestMapping(value = "/game/matching/{userId}/{token}/{type}/{costType}",method = RequestMethod.POST)
    public HttpResult gameMatchingV2(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("type")int type,
            @PathVariable("costType")int costType,
            HttpServletRequest request){
        try {
            if(type<1||type>4){
                return HttpResult.error(Result.REQUEST_PARAM_ERROR.getCode(),Result.REQUEST_PARAM_ERROR.getMsg());
            }
            int minBalance = (type==1?30:(type==2?10:30));
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //获取用户信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
            //获取新版vip经验值 道具9
            long vip_exp =0 ;
            if(info.containsKey("userBackPack")){
                Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                vip_exp = userBackPack.containsKey("9")?Long.parseLong((String)userBackPack.get("9")):0;
            }
            long self_prize_ticket = 0;
            //获取玩家身上实时货币
            if(costType==3){
                result = apiUserFeign.getGameUserCurrency(userId);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserCurrency gameUserCurrency =  BeanUtils.convertMapToObject(object,GameUserCurrency.class);
                    self_prize_ticket =gameUserCurrency.getPrizeTicket();
                }
            }else{
                result = apiUserFeign.getGameUserBackpack(userId,costType);
                if(result.getCode()!= HttpStatus.SC_OK){//数据不存在
                    return result;
                }
                if(result.getData()!=null){
                    Map<Object, Object> object = (Map<Object, Object>)result.getData();
                    GameUserBackpack gameUserBackpack =  BeanUtils.convertMapToObject(object,GameUserBackpack.class);
                    self_prize_ticket =gameUserBackpack.getPropCount();
                }
            }

            if(self_prize_ticket<=0){
                String msg = (type==3?"TextID_Change_Tip_NotEnoughFee":"TextID_Change_Tip_NotEnough");
                return HttpResult.error(Result.PRIZE_TICKET_SHORT.getCode(),msg);
            }
            return  apiAdvExchangeFeign.gameMatching(userId,vip_exp,minBalance,self_prize_ticket,type,costType);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner matching product   error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /***
     * 游戏内提现
     */
    @ApiOperation(value = "红包提现")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="productId",dataType="Int",required=true,value="兑换商品Id"),
            @ApiImplicitParam(name="appId",dataType="String",required=true,value="提现应用Id"),
            @ApiImplicitParam(name="type",dataType="Int",required=true,value="提现类型（1零钱、2支付宝、3话费、4趣金币）"),
    })
    @RequestMapping(value = "/game/operate/{userId}/{token}/{productId}/{appId}/{type}",method = RequestMethod.POST)
    public HttpResult gameOperate(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("productId")int productId,
            @PathVariable("appId")String appId,
            @PathVariable("type")int type,
            HttpServletRequest request){
        try {
            if(type<1||type>4){
                return HttpResult.error(Result.REQUEST_PARAM_ERROR.getCode(),Result.REQUEST_PARAM_ERROR.getMsg());
            }
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            //获取用户信息
            Map<Object, Object> info  = (Map<Object, Object>) result.getData();
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
            }else if(type==4){//趣金币
                if(!info.containsKey("accountThirdInfo")){
                    return HttpResult.error(Result.DATA_EXIST_ERROR.getCode(),Result.DATA_EXIST_ERROR.getMsg());
                }
                Map<Object, Object> accountThirdInfo = (Map<Object, Object>) info.get("accountThirdInfo");
                if(!accountThirdInfo.containsKey("open_id")||!appId.equals(accountThirdInfo.get("third_app_id"))
                        || StringUtils.isBlank((String)accountThirdInfo.get("open_id"))){
                    return HttpResult.error(Result.REDBAG_QUTT_UNBIND.getCode(),Result.REDBAG_QUTT_UNBIND.getMsg());
                }
                openId = (String)accountThirdInfo.get("open_id");
            }
            //取出用户VIP等级
            long vip_exp =0 ;
            if(info.containsKey("userBackPack")){
                Map<Object, Object> userBackPack = (Map<Object, Object>) info.get("userBackPack");
                vip_exp = userBackPack.containsKey("9")?Long.parseLong((String)userBackPack.get("9")):0;
            }
            //取出红包券缓存
            long prize_ticket = 0;
            if(info.containsKey("userCurrency")){
                Map<Object, Object> userProperty = (Map<Object, Object>) info.get("userCurrency");
                prize_ticket = userProperty.containsKey("prize_ticket")?Long.parseLong((String)userProperty.get("prize_ticket")):0;
            }

            result = apiAdvExchangeFeign.getCfgByProductId(productId);
            if(result.getCode()!= HttpStatus.SC_OK){//配置不存在
                return result;
            }
            Map<Object, Object> productIdCfg = (Map<Object, Object>)result.getData();
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
            return apiAdvExchangeFeign.gameOperate(userId,productId,vip_exp,appId,type,openId,IpUtils
                    .getIpAddr(request));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner operate product "+productId+"  error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

    /***
     * 兑换记录
     */
    @ApiOperation(value = "兑换记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
    })
    @RequestMapping(value = "/exchange/list/{userId}/{token}",method = RequestMethod.GET)
    public HttpResult exchangeList(@PathVariable("userId")Long userId,
            @PathVariable("token")String token){
        try {
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            return apiAdvExchangeFeign.exchangeList(userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner exchange list   error: "+e.getMessage());
        }
        return  HttpResult.error();
    }


    /***
     * 申请退款
     */
    /*@ApiOperation(value = "申请退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",dataType="Long",required=true,value="用户的Id"),
            @ApiImplicitParam(name="token",dataType="String",required=true,value="用户登录token"),
            @ApiImplicitParam(name="exchangeId",dataType="Long",required=true,value="兑换记录Id"),
    })*/
    @RequestMapping(value = "/exchange/refund/{userId}/{token}/{exchangeId}",method = RequestMethod.POST)
    public HttpResult exchangeRefund(@PathVariable("userId")Long userId,
            @PathVariable("token")String token,
            @PathVariable("exchangeId")Long exchangeId){
        try {
            //验证token
            HttpResult result = apiUserFeign.getToken(userId,token,DeFual_ProductId);
            if(result.getCode()!= HttpStatus.SC_OK){//token验证失败
                return result ;
            }
            return apiAdvExchangeFeign.getBackExchange(userId,exchangeId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(userId+" game inner exchange refund   error: "+e.getMessage());
        }
        return  HttpResult.error();
    }

}