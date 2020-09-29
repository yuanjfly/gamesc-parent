package com.douzi.gamesc.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "OrderParam对象", description = "订单参数实体类")
@Data
public class OrderParam {

    /**
     * 渠道ID
     */
    @ApiModelProperty(value = "渠道ID", name = "channel", required = true, dataType = "String")
    String channel;
    /**
     * 应用Id
     */
    @ApiModelProperty(value = "应用Id", name = "appId", required = true, dataType = "String")
    String appId;
    /**
     * 商户Id
     */
    @ApiModelProperty(value = "商户Id", name = "masterId", required = true, dataType = "String")
    String masterId;
    /**
     *支付商品Id 应用申请商品的id
     */
    @ApiModelProperty(value = "支付商品Id", name = "productId", required = true, dataType = "Integer")
    Integer productId;
    /**
     *支付商品价格 单位分
     */
    @ApiModelProperty(value = "支付商品价格", name = "productPrice", required = true, dataType = "Integer")
    Integer productPrice;
    /**
     *商品名称
     */
    @ApiModelProperty(value = "商品名称", name = "productName",  dataType = "String" ,allowEmptyValue = true)
    String productName;
    /**
     *商品描述
     */
    @ApiModelProperty(value = "商品描述", name = "productDesc",  dataType = "String" ,allowEmptyValue = true)
    String productDesc;
    /**
     *应用用户标示
     */
    @ApiModelProperty(value = "应用用户标示", name = "userId", required = true, dataType = "Long")
    Long userId;
    /**
     *商户应用标示
     */
    @ApiModelProperty(value = "商户应用标示", name = "openId",  dataType = "String" ,allowEmptyValue = true)
    String openId;
    /**
     *支付客户端IP
     */
    String ip;
    /**
     *支付扩展信息 json字符串
     */
    @ApiModelProperty(value = "支付扩展信息", name = "extension", dataType = "String",allowEmptyValue = true)
    String extension;
    /**
     *支付客户端 0：未知；1：安卓；2：IOS；3：PC
     */
    @ApiModelProperty(value = "支付客户端(0：未知；1：安卓；2：IOS；3：PC)", name = "clientType", required = true, dataType = "Integer")
    Integer clientType;

    /**
     *请求时间戳
     */
    @ApiModelProperty(value = "时间戳", name = "timeStamp", required = true, dataType = "String")
    String timeStamp;

    /**
     *签名信息
     */
    @ApiModelProperty(value = "签名信息", name = "sign", required = true, dataType = "String")
    String sign;

}
