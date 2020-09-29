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

@Table(name = "exchange_current_record")
@ApiModel(value = "ExhangeCurrentRecord对象", description = "玩家兑换礼包当前延迟发放记录类")
@Data
public class ExchangeCurrentRecord implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "玩家兑换礼包当前延迟发放记录主键", required = true, dataType = "Long")
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
     * 延迟到账内容
     */
    @Column(name = "product_detail")
    @ApiModelProperty(value = "productDetail", name = "商品内容", dataType = "String")
    private String productDetail;

    /**
     * rpc消息体
     */
    @Column(name = "rpc_info")
    @ApiModelProperty(value = "rpcInfo", name = "rpc消息体", dataType = "String")
    private String rpcInfo;

    /**
     * 当日兑换次数
     */
    @Column(name = "lave_time")
    @ApiModelProperty(value = "laveTime", name = "延迟时间（单位秒）", dataType = "Integer")
    private Integer laveTime;

    /**
     * 当日兑换次数
     */
    @Column(name = "status")
    @ApiModelProperty(value = "status", name = "0:待审核、1已审核、-1审核不通过", dataType = "Integer")
    private Integer status;

    /**
     * 兑换时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "兑换时间", dataType = "Date")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "更新时间", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}