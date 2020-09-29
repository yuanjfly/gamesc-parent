package com.douzi.gamesc.common.pojo.exhange;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "exchange_product_day")
@ApiModel(value = "ExchangeproductDay对象", description = "用户日兑换类")
@Data
public class ExchangeProductDay implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "用户日兑换主键", required = true, dataType = "Long")
    private Long id;

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
    @ApiModelProperty(value = "hasNum", name = "当日兑换次数", dataType = "Integer")
    private Integer hasNum;

    /**
     * 每日首次兑换时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "每日首次兑换时间", dataType = "Date")
    private Date createTime;

    /**
     * 每日最后一次兑换时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "每日最后一次兑换时间", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}