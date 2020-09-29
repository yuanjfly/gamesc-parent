package com.douzi.gamesc.common.pojo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "game_user_backpack")
@ApiModel(value = "GameUserBackpack对象", description = "背包实体类")
@Data
public class GameUserBackpack {

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 道具ID
     */
    @Column(name = "prop_id")
    @ApiModelProperty(value = "propId", name = "道具ID", dataType = "Integer")
    private Integer propId;

    /**
     * 道具数量
     */
    @Column(name = "prop_count")
    @ApiModelProperty(value = "propCount", name = "道具数量", dataType = "Long")
    private Long propCount;

    /**
     * 道具时效
     */
    @Column(name = "prop_time")
    @ApiModelProperty(value = "propTime", name = "道具时效", dataType = "Long")
    private Long propTime;


    /**
     * 更新日期
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "更新日期", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
