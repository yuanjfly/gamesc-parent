package com.douzi.gamesc.common.pojo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "game_user_master_history")
@ApiModel(value = "GameUserMasterHistory对象", description = "大师榜实体类")
@Data
public class GameUserMasterHistory {

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 金币
     */
    @Column(name = "list_date")
    @ApiModelProperty(value = "listDate", name = "榜单开始日期", dataType = "String")
    private String listDate;

    /**
     * 等级
     */
    @Column(name = "master_level")
    @ApiModelProperty(value = "masterLevel", name = "等级", dataType = "Integer")
    private Integer masterLevel;

    /**
     * 获取的总分
     */
    @Column(name = "total_score")
    @ApiModelProperty(value = "totalScore", name = "获取的总分", dataType = "Long")
    private Long totalScore;

    /**
     * 奖励数量
     */
    @Column(name = "prize_num")
    @ApiModelProperty(value = "prizeNum", name = "奖励数量", dataType = "Integer")
    private Integer prizeNum;


    /**
     * 结算日
     */
    @Column(name = "close_day")
    @ApiModelProperty(value = "closeDay", name = "结算日", dataType = "String")
    private String closeDay;

    /**
     * 是否领取工资
     */
    @Column(name = "ifreceive")
    @ApiModelProperty(value = "ifreceive", name = "是否领取工资", dataType = "Integer")
    private Integer ifreceive;

    /**
     * 操作时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "操作时间", dataType = "Long")
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
