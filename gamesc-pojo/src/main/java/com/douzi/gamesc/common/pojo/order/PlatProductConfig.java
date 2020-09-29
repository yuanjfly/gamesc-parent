package com.douzi.gamesc.common.pojo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "plat_product_config")
@ApiModel(value = "PlatProductConfig对象", description = "支付商品类")
@Data
public class PlatProductConfig implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "应用表类主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 应用Id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "应用Id", dataType = "String")
    private String appId;

    /**
     * 应用密钥
     */
    @Column(name = "product_id")
    @ApiModelProperty(value = "productId", name = "商品Id", dataType = "Integer")
    private Integer productId;

    /**
     * 商品价格(单位：分)
     */
    @Column(name = "product_price")
    @ApiModelProperty(value = "productPrice", name = "商品价格", dataType = "Integer")
    private Integer productPrice;

    /**
     * 商品描述
     */
    @Column(name = "product_desc")
    @ApiModelProperty(value = "productDesc", name = "商品描述", dataType = "String")
    private String productDesc;

    /**
     * 商品状态（0下降、1上架）
     */
    @Column(name = "product_state")
    @ApiModelProperty(value = "productState", name = "商品状态", dataType = "Integer")
    private Integer productState;

    /**
     * 创建日期
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "创建日期", dataType = "Date")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}