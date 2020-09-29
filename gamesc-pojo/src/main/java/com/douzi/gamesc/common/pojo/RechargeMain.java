package com.douzi.gamesc.common.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "game_recharge_main")
@ApiModel(value = "订单对象", description = "订单实体类")
@Data
public class RechargeMain implements Serializable {
    /**
     * 订单id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "id", name = "订单唯一标识", required = true, dataType = "Long")
    private Long id;


    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "项目id", dataType = "String")
    private String appId;


    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 邮费。精确到2位小数;单位:元。如:200.07，表示:200元7分
     */
    @Column(name = "open_id")
    @ApiModelProperty(value = "openId", name = "趣头条用户唯一标示", dataType = "String")
    private String openId;

    /**
     * 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
     */
    @Column(name = "trade_no")
    @ApiModelProperty(value = "tradeNo", name = "充值订单号", dataType = "String")
    private String tradeNo;

    /**
     * 单位分，1元=100
     */
    @Column(name = "total_fee")
    @ApiModelProperty(value = "totalFee", name = "充值订单金额", dataType = "Integer")
    private Integer totalFee;

    @Column(name = "game_id")
    @ApiModelProperty(value = "gameId", name = "游戏ID", dataType = "Integer")
    private Integer gameId;

    @Column(name = "room_id")
    @ApiModelProperty(value = "roomId", name = "房间ID", dataType = "Integer")
    private Integer roomId;

    @Column(name = "game_coin")
    @ApiModelProperty(value = "gameCoin", name = "本次充值获得积分", dataType = "Long")
    private Long gameCoin;

    @Column(name = "gift_id")
    @ApiModelProperty(value = "giftId", name = "礼包ID", dataType = "Integer")
    private Integer giftId;
    /**
     * 订单更新时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "充值时间", dataType = "Date")
    private Date createTime;


}