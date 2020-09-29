package com.douzi.gamesc.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 返回信息封装类
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private int code;
    private String msg;

    public Result fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new Result(code, message);
    }

    public static final Result ADD_SUCCESS = new Result(200, "新增成功");
    public static final Result BATCH_ADD_SUCCESS = new Result(200, "批量新增成功");
    public static final Result EDIT_SUCCESS = new Result(200, "修改成功");
    public static final Result DEL_SUCCESS = new Result(200, "删除成功");
    public static final Result RESPONSE_SUCCESS = new Result(200, "请求成功");
    public static final Result LOGIN_SUCCESS = new Result(200, "登录成功");
    public static final Result BIND_ERROR = new Result(100001, "参数异常");
    public static final Result ILLEGAL_USER = new Result(100002,"用户不存在");
    public static final Result PASSWORD_ERROR = new Result(100003,"密码错误");
    public static final Result SERVER_ERROR = new Result(100004, "服务端异常");
    public static final Result REQUEST_PARAM_ERROR = new Result(100005,"请求参数异常");
    public static final Result REQUEST_PARAM_NULL = new Result(100006,"请求参数缺失");
    public static final Result SESSION_ERROR = new Result(100007,"session超时");
    public static final Result REQUEST_ILLEGAL = new Result(100008,"请求非法");
    public static final Result ORDER_CREATE_ERROR = new Result(100009, "下单失败");
    public static final Result PRODUCT_ID_ERROR = new Result(100011, "支付商品不存在");
    public static final Result PARAM_SIGN_ERROR = new Result(100012, "签名验证失败");
    public static final Result ORDER_EXIST_ERROR = new Result(100013, "订单不存在");
    public static final Result CFG_EXIST_ERROR = new Result(100011, "配置不存在");
    public static final Result DATA_EXIST_ERROR = new Result(100011, "数据不存在");
    public static final Result PRODUCT_HAVING_CHECK = new Result(100012, "您有其他奖品正在审核，是否先查看审核进度");
    public static final Result PRODUCT_NOT_STORE = new Result(100013, "红包今日已提完，每日数量有限哦");
    public static final Result PRODUCT_VIP_LIMIT = new Result(100014, "亲，你已提过%s，每个用户限%d次噢");
    public static final Result PRODUCT_NOT_EXIST = new Result(100015, "奖品不存在");
    public static final Result PRODUCT_IN_CHECK = new Result(100016, "奖品审核中");
    public static final Result CURRENCY_COST_ERROR = new Result(100115, "扣除货币失败");
    public static final Result CURRENCY_ADD_ERROR = new Result(100116, "添加货币失败");
    public static final Result PROP_COST_ERROR = new Result(100117, "扣除道具失败");
    public static final Result PROP_ADD_ERROR = new Result(100118, "添加道具失败");
    public static final Result PRIZE_TICKET_SHORT = new Result(100119, "你的余额不足，继续猜歌赚红包吧");
    public static final Result PROP_SHORT = new Result(100120, "你的余额不足，继续猜歌赚红包吧");
    public static final Result PHONE_FORMAT_ERROR = new Result(200001, "手机号码为空或手机号码不正确");
    public static final Result PHONE_SEND_FAIL = new Result(200002, "手机短信发送失败");
    public static final Result PHONE_CODE_ERROR = new Result(200003, "手机验证码不存在或已过时");
    public static final Result ACCOUNT_HAD_BIND = new Result(200004, "账号已绑定手机号码");
    public static final Result PHONE_USER_UNBIND = new Result(200005, "手机号码未绑定账号");
    public static final Result PHONE_HAD_BIND = new Result(200006, "手机号%s已绑定其他账号，每个手机号只可以绑定一次哦，请更换手机号再试");
    public static final Result ACCOUNT_HAD_BIND_ALI = new Result(200007, "账号已绑定支付宝账号");
    public static final Result ALI_HAD_BIND = new Result(200008, "TextID_Change_Tip_Error2");
    public static final Result ALI_FORMAT_ERROR = new Result(200009, "支付宝账号为空或格式不正确");

    public static final Result REDBAG_USER_BIND = new Result(300001, "该账号已经绑定");
    public static final Result REDBAG_USER_UNBIND = new Result(300002, "该账号未绑定");
    public static final Result REDBAG_APP_CONFIG = new Result(300003, "提现商户配置不存在");
    public static final Result REDBAG_APP_ERROR = new Result(300004, "红包提现失败");
    public static final Result REDBAG_PROP_NOT_ENOUGH = new Result(300005, "红包余额不足");
    public static final Result REDBAG_DAY_LIMIT = new Result(300006, "您已到达当日提现上限");
    public static final Result REDBAG_ALL_LIMIT = new Result(300007, "您已到达提现上限");
    public static final Result REDBAG_WECHAT_UNBIND = new Result(300008, "该账号未绑定微信");
    public static final Result REDBAG_ALIPAY_UNBIND = new Result(300009, "该账号未绑定支付宝");
    public static final Result REDBAG_PHONE_UNBIND = new Result(300010, "该账号未绑定手机号码");
    public static final Result REDBAG_QUTT_UNBIND = new Result(300011, "非正式账号无法兑换");

    public static final Result EXCHANE_USER_ONLINE = new Result(300012, "您还在游戏中，无法兑换");
    public static final Result EXCHANE_NOT_PROP = new Result(300013, "无法兑换该类商品");


    public static final Result SHARE_HELP_FAIL = new Result(400000, "助力失败");
    public static final Result SHARE_END = new Result(400001, "分享已经结束");

    public static final Result FREQUENT_OPERATION = new Result(900000, "请勿频繁操作");
}

