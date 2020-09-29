package com.douzi.gamesc.common.pojo.exhange;

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

@Table(name = "exchange_bind_user")
@ApiModel(value = "ExchangeBindUser对象", description = "用户绑定记录类")
@Data
public class ExchangeBindUser implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "用户绑定记录主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 微信open或者支付宝账号
     */
    @Column(name = "open_id")
    @ApiModelProperty(value = "openId", name = "微信open或者支付宝账号", dataType = "String")
    private String openId;

    /**
     * 提现红包应用id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "提现红包应用id", dataType = "String")
    private String appId;

    /**
     * 产品Id
     */
    @Column(name = "product_id")
    @ApiModelProperty(value = "productId", name = "产品Id", dataType = "String")
    private String productId;

    /**
     * 提现类型 (0:公众号、1:app零钱、2支付宝转账)
     */
    @Column(name = "type")
    @ApiModelProperty(value = "type", name = "提现类型 (0:公众号、1:app零钱、2支付宝转账)", dataType = "Integer")
    private Integer type;

    /**
     * 绑定时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "绑定时间", dataType = "Date")
    private Date createTime;


    private static final long serialVersionUID = 1L;

}