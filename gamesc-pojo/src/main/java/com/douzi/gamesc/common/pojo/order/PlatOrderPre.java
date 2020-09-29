package com.douzi.gamesc.common.pojo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "plat_order_pre")
@ApiModel(value = "PlatOrderPre对象", description = "订单实体类")
@Data
public class PlatOrderPre implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @Column(name = "id")
    @ApiModelProperty(value = "id", name = "主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    @ApiModelProperty(value = "orderNo", name = "订单号", dataType = "String")
    private String orderNo;

    /**
     * 渠道号
     */
    @Column(name = "channel")
    @ApiModelProperty(value = "channelId", name = "渠道号", dataType = "String")
    private String channelId;

    /**
     * 商户订单号
     */
    @Column(name = "master_order_no")
    @ApiModelProperty(value = "masterOrderNo", name = "商户订单号", dataType = "String")
    private String masterOrderNo;

    /**
     * 预支付金额、单位分
     */
    @Column(name = "money")
    @ApiModelProperty(value = "money", name = "预支付金额", dataType = "Integer")
    private Integer money;

    /**
     * 实际支付金额、单位分
     */
    @Column(name = "real_money")
    @ApiModelProperty(value = "realMoney", name = "实际支付金额", dataType = "Integer")
    private Integer realMoney;

    /**
     * 订单状态 0：待支付；1：支付成功
     */
    @Column(name = "state")
    @ApiModelProperty(value = "state", name = "订单状态", dataType = "Integer")
    private Integer state;

    /**
     * 应用Id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "应用Id", dataType = "String")
    private String appId;

    /**
     * 应用用户id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "应用用户id", dataType = "Long")
    private Long userId;

    /**
     * 商户应用id
     */
    @Column(name = "master_id")
    @ApiModelProperty(value = "masterId", name = "商户id", dataType = "String")
    private String masterId;

    /**
     * 商户应用id
     */
    @Column(name = "open_id")
    @ApiModelProperty(value = "openId", name = "商户用户标示", dataType = "String")
    private String openId;


    /**
     * 商户应用id
     */
    @Column(name = "cp_app_id")
    @ApiModelProperty(value = "cpAppId", name = "商户应用id", dataType = "String")
    private String cpAppId;

    /**
     * 创建日期
     */
    @Column(name = "create_time",insertable = false)
    @ApiModelProperty(value = "createTime", name = "创建日期", dataType = "Date")
    private Date createTime;

    /**
     * 完成日期
     */
    @Column(name = "finish_time")
    @ApiModelProperty(value = "finishTime", name = "完成日期", dataType = "Date")
    private Date finishTime;

    /**
     * 礼包id
     */
    @Column(name = "gift_id")
    @ApiModelProperty(value = "giftId", name = "礼包id", dataType = "Integer")
    private Integer giftId;

    /**
     * 礼包名称
     */
    @Column(name = "gift_name")
    @ApiModelProperty(value = "giftName", name = "礼包名称", dataType = "String")
    private String giftName;


    /**
     * 礼包描述
     */
    @Column(name = "gift_desc")
    @ApiModelProperty(value = "giftDesc", name = "礼包描述", dataType = "String")
    private String giftDesc;

    /**
     * 礼包描述
     */
    @Column(name = "ip")
    @ApiModelProperty(value = "ip", name = "ip", dataType = "String")
    private String ip;

    /**
     * 客户端类型\0：未知；1：安卓；2：IOS；3：PC
     */
    @Column(name = "client_type")
    @ApiModelProperty(value = "clientType", name = "客户端类型", dataType = "Integer")
    private Integer clientType;

    /**
     * 其他信息
     */
    @Column(name = "extension")
    @ApiModelProperty(value = "extension", name = "其他信息", dataType = "String")
    private String extension;

    /**
     * 支付费率
     */
    @Column(name = "pay_rate")
    @ApiModelProperty(value = "payRate", name = "支付费率", dataType = "Double")
    private Double payRate;

    /**
     * 支付分成比例
     */
    @Column(name = "pay_share_rate")
    @ApiModelProperty(value = "payShareRate", name = "支付分成比例", dataType = "Double")
    private Double payShareRate;

    private static final long serialVersionUID = 1L;

}