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

@Table(name = "activity_share_mapping_record")
@ApiModel(value = "ActivityShareMappingRecord对象", description = "分享好友助力记录类")
@Data
public class ActivityShareMappingRecord implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "用户分享记录主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 分享记录Id
     */
    @Column(name = "main_id")
    @ApiModelProperty(value = "mainId", name = "分享记录Id", dataType = "Long")
    private Long mainId;

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
     * 昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "nickName", name = "昵称", dataType = "String")
    private String nickName;

    /**
     * 图像
     */
    @Column(name = "head_url")
    @ApiModelProperty(value = "headUrl", name = "图像", dataType = "String")
    private String headUrl;

    /**
     * 助力者微信openid
     */
    @Column(name = "open_id")
    @ApiModelProperty(value = "openId", name = "助力者微信openid", dataType = "String")
    private String openId;

    /**
     * 助力者微信unionid
     */
    @Column(name = "union_id")
    @ApiModelProperty(value = "unionId", name = "助力者微信unionid", dataType = "String")
    private String unionId;
    /**
     * 助力金额
     */
    @Column(name = "help_num")
    @ApiModelProperty(value = "help_num", name = "助力金额", dataType = "Integer")
    private Integer helpNum;

    /**
     * 助力时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "助力时间", dataType = "Date")
    private Date createTime;




    private static final long serialVersionUID = 1L;

}