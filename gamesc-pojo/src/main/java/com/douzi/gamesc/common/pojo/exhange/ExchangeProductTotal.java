package com.douzi.gamesc.common.pojo.exhange;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "exchange_product_total")
@ApiModel(value = "ExchangeProductTotal对象", description = "用户总兑换类")
@Data
public class ExchangeProductTotal implements Serializable {

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 礼包ID
     */
    @Column(name = "product_id")
    @ApiModelProperty(value = "productId", name = "商品ID", dataType = "Integer")
    private Integer productId;

    /**
     * 当日兑换次数
     */
    @Column(name = "has_num")
    @ApiModelProperty(value = "hasNum", name = "兑换次数", dataType = "Integer")
    private Integer hasNum;

    /**
     * 每日首次兑换时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "首次兑换时间", dataType = "Date")
    private Date createTime;

    /**
     * 每日最后一次兑换时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "最后一次兑换时间", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}