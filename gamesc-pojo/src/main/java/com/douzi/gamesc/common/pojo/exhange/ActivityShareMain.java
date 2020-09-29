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

@Table(name = "activity_share_main")
@ApiModel(value = "ActivityShareMain对象", description = "用户分享记录类")
@Data
public class ActivityShareMain implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "用户分享记录主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 平台用户Id
     */
    @Column(name = "activity_id")
    @ApiModelProperty(value = "activityId", name = "平台用户Id", dataType = "Integer")
    private Integer activityId;

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     * 分享记录订单号
     */
    @Column(name = "order_id")
    @ApiModelProperty(value = "order_id", name = "分享记录订单号", dataType = "String")
    private String orderId;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "nickName", name = "昵称", dataType = "String")
    private String nickName;

    /**
     * 产品Id
     */
    @Column(name = "head_url")
    @ApiModelProperty(value = "headUrl", name = "图像", dataType = "String")
    private String headUrl;

    /**
     * 活动分享开始时间
     */
    @Column(name = "start_time")
    @ApiModelProperty(value = "startTime", name = "活动分享开始时间", dataType = "Date")
    private Date startTime;

    /**
     * 活动分享结束时间
     */
    @Column(name = "end_time")
    @ApiModelProperty(value = "endTime", name = "活动分享结束时间", dataType = "Date")
    private Date endTime;

    /**
     * 活动分享时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "活动分享时间", dataType = "Date")
    private Date createTime;

    /**
     * 扩展信息json结构
     */
    @Column(name = "extend")
    @ApiModelProperty(value = "extend", name = "扩展信息json结构", dataType = "String")
    private String extend;


    private static final long serialVersionUID = 1L;

}