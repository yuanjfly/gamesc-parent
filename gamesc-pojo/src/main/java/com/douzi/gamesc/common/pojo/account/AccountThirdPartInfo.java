package com.douzi.gamesc.common.pojo.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "account_thirdpart_info")
@ApiModel(value = "AccountThirdPartInfo对象", description = "第三方账号信息类")
@Data
public class AccountThirdPartInfo {

    /**
     * 平台用户Id
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     *自平台应用ID即长渠道号
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "自平台应用ID即长渠道号", dataType = "String")
    private String appId;

    /**
     *产品线ID
     */
    @Column(name = "prod_id")
    @ApiModelProperty(value = "prodId", name = "产品线ID", dataType = "String")
    private String prodId;

    /**
     *第三方产品ID
     */
    @Column(name = "third_app_id")
    @ApiModelProperty(value = "thirdAppId", name = "第三方产品ID", dataType = "String")
    private String thirdAppId;

    /**
     *第三方用户项目唯一标识
     */
    @Column(name = "open_id")
    @ApiModelProperty(value = "openId", name = "第三方用户项目唯一标识", dataType = "String")
    private String openId;

    /**
     *身份证ID
     */
    @Column(name = "union_id")
    @ApiModelProperty(value = "unionId", name = "第三方用户公司唯一标识", dataType = "String")
    private String unionId;

    /**
     *第三方昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "nickName", name = "第三方昵称", dataType = "String")
    private String nickName;

    /**
     *第三方性别
     */
    @Column(name = "gender")
    @ApiModelProperty(value = "gender", name = "第三方性别", dataType = "Integer")
    private Integer gender;

    /**
     *注册来源  0:快速 1:渠道skd  2微信 3支付宝 4手机
     */
    @Column(name = "account_type")
    @ApiModelProperty(value = "accountType", name = "注册来源  0:快速 1:渠道skd  2微信 3支付宝 4手机", dataType = "Integer")
    private Integer accountType;

    /**
     *第三方头像
     */
    @Column(name = "head_url")
    @ApiModelProperty(value = "headUrl", name = "第三方头像", dataType = "String")
    private String headUrl;

    /**
     * 操作时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "创建时间", dataType = "Date")
    private Date createTime;

    /**
     * 操作时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(value = "updateTime", name = "操作时间", dataType = "Date")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
