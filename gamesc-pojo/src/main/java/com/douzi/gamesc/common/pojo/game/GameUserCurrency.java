package com.douzi.gamesc.common.pojo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "game_user_currency")
@ApiModel(value = "GameUserCurrency对象", description = "货币实体类")
@Data
public class GameUserCurrency {

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 金币
     */
    @Column(name = "game_coin")
    @ApiModelProperty(value = "gameCoin", name = "金币", dataType = "Long")
    private Long gameCoin;

    /**
     * 奖券
     */
    @Column(name = "prize_ticket")
    @ApiModelProperty(value = "prizeTicket", name = "奖券", dataType = "Long")
    private Long prizeTicket;

    /**
     * 钻石
     */
    @Column(name = "diamond")
    @ApiModelProperty(value = "diamond", name = "钻石", dataType = "Long")
    private Long diamond;

    /**
     * 操作时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "操作时间", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
