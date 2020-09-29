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

@Table(name = "plat_master_config")
@ApiModel(value = "PlatMasterConfig", description = "商户类")
@Data
public class PlatMasterConfig implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "商户主键", required = true, dataType = "Integer")
    private Integer id;

    /**
     * 商户标示
     */
    @Column(name = "master_id")
    @ApiModelProperty(value = "masterId", name = "商户标示",required = true, dataType = "String")
    private String masterId;

    /**
     * 商户名称
     */
    @Column(name = "master_name")
    @ApiModelProperty(value = "masterName", name = "商户名称", dataType = "String")
    private String masterName;

    /**
     * 下单地址
     */
    @Column(name = "order_url")
    @ApiModelProperty(value = "orderUrl", name = "下单地址", dataType = "String")
    private String orderUrl;

    /**
     * 支付回调地址
     */
    @Column(name = "notify_url")
    @ApiModelProperty(value = "notifyUrl", name = "支付回调地址", dataType = "String")
    private String notifyUrl;

    /**
     * 支付SDK
     */
    @Column(name = "verify_class")
    @ApiModelProperty(value = "verifyClass", name = "支付SDK", dataType = "String")
    private String verifyClass;

    /**
     * 创建日期
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "创建日期", dataType = "Date")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}