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

@Table(name = "plat_channel_master")
@ApiModel(value = "PlatChannelMaster对象", description = "渠道商户类")
@Data
public class PlatChannelMaster implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "地址信息唯一标识", required = true, dataType = "Long")
    private Long id;

    /**
     * 渠道号
     */
    @Column(name = "channel")
    @ApiModelProperty(value = "channelId", name = "渠道号", dataType = "String")
    private String channelId;

    /**
     * 应用Id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "应用Id", dataType = "String")
    private String appId;

    /**
     * 商户id
     */
    @Column(name = "master_id")
    @ApiModelProperty(value = "masterId", name = "商户id", dataType = "String")
    private String masterId;

    /**
     * 商户应用id
     */
    @Column(name = "cp_app_id")
    @ApiModelProperty(value = "cpAppId", name = "商户应用id", dataType = "String")
    private String cpAppId;

    /**
     * 商户应用key
     */
    @Column(name = "cp_app_key")
    @ApiModelProperty(value = "cpAppKey", name = "商户应用key", dataType = "String")
    private String cpAppKey;

    /**
     * 商户支付号
     */
    @Column(name = "cp_pay_id")
    @ApiModelProperty(value = "cpPayId", name = "商户支付号", dataType = "String")
    private String cpPayId;

    /**
     * 商户支付密钥
     */
    @Column(name = "cp_pay_secret")
    @ApiModelProperty(value = "cpPaySecret", name = "商户支付密钥", dataType = "String")
    private String cpPaySecret;

    /**
     * 支付是否打开 0关闭、1打开
     */
    @Column(name = "pay_open_flag")
    @ApiModelProperty(value = "payOpenFlag", name = "支付是否打开", dataType = "Integer")
    private Integer payOpenFlag;

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

    /**
     * 支付完成通知业务mq信息
     */
    @Column(name = "mq_info")
    @ApiModelProperty(value = "mqInfo", name = "支付完成通知业务mq信息", dataType = "String")
    private String mqInfo;

    /**
     * 创建日期
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "创建日期", dataType = "Date")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}