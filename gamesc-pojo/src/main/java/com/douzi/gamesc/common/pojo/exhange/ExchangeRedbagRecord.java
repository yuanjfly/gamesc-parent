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

@Table(name = "exchange_redbag_record")
@ApiModel(value = "ExchangeRedbagRecord对象", description = "用户兑换红包记录类")
@Data
public class ExchangeRedbagRecord implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "用户兑换红包记录主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 兑换红包金额（分）
     */
    @Column(name = "money")
    @ApiModelProperty(value = "money", name = "兑换红包金额（分）", dataType = "Integer")
    private Integer money;

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
     * 提现类型 (0:公众号、1:app零钱、2支付宝转账)
     */
    @Column(name = "type")
    @ApiModelProperty(value = "type", name = "提现类型 (0:公众号、1:app零钱、2支付宝转账)", dataType = "Integer")
    private Integer type;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    @ApiModelProperty(value = "orderNo", name = "订单号", dataType = "String")
    private String orderNo;

    /**
     * 订单状态(0:待审核、1:审核通过、-1:审核未通过)
     */
    @Column(name = "status")
    @ApiModelProperty(value = "status", name = "订单状态(0:待审核、1:审核通过、-1:审核未通过)", dataType = "Integer")
    private Integer status;

    /**
     * 兑换红包金额（分）
     */
    @Column(name = "prize_ticket")
    @ApiModelProperty(value = "prizeTicket", name = "消耗红包点数", dataType = "Integer")
    private Integer prizeTicket;

    /**
     * ip
     */
    @Column(name = "ip")
    @ApiModelProperty(value = "ip", name = "ip地址", dataType = "String")
    private String ip;

    /**
     * 备注
     */
    @Column(name = "remark")
    @ApiModelProperty(value = "remark", name = "备注", dataType = "String")
    private String remark;

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