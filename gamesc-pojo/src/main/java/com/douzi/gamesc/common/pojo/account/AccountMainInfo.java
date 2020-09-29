package com.douzi.gamesc.common.pojo.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "account_main_info")
@ApiModel(value = "AccountMainInfo对象", description = "平台账号信息类")
@Data
public class AccountMainInfo {

    /**
     * 平台用户Id
     */
    @Id
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     *账号
     */
    @Column(name = "account")
    @ApiModelProperty(value = "account", name = "账号", dataType = "String")
    private String account;

    /**
     *明码经过md5加密
     */
    @Column(name = "passwd")
    @ApiModelProperty(value = "passwd", name = "明码经过md5加密", dataType = "String")
    private String passwd;

    /**
     *app_id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "对外业务ID", dataType = "String")
    private String appId;

    /**
     *第三方昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "nickName", name = "第三方昵称", dataType = "String")
    private String nickName;

    /**
     *对内业务ID
     */
    @Column(name = "site_id")
    @ApiModelProperty(value = "siteId", name = "对内业务ID", dataType = "Integer")
    private Integer siteId;

    /**
     *性别
     */
    @Column(name = "gender")
    @ApiModelProperty(value = "gender", name = "性别", dataType = "Integer")
    private Integer gender;

    /**
     *业务渠道编号
     */
    @Column(name = "channel_id")
    @ApiModelProperty(value = "channelId", name = "业务渠道编号", dataType = "Integer")
    private Integer channelId;

    /**
     *机器码下账号序列
     */
    @Column(name = "account_index")
    @ApiModelProperty(value = "accountIndex", name = "机器码下账号序列", dataType = "Integer")
    private Integer accountIndex;

    /**
     *注册来源  0:快速 1:渠道skd  2微信 3支付宝 4手机
     */
    @Column(name = "account_type")
    @ApiModelProperty(value = "accountType", name = "注册来源  0:快速 1:渠道skd  2微信 3支付宝 4手机", dataType = "Integer")
    private Integer accountType;

    /**
     *账号状态 1:开启 0:禁用
     */
    @Column(name = "account_status")
    @ApiModelProperty(value = "accountStatus", name = "账号状态 1:开启 0:禁用", dataType = "Integer")
    private Integer accountStatus;

    /**
     *注册日期
     */
    @Column(name = "registe_date")
    @ApiModelProperty(value = "registeDate", name = "注册日期", dataType = "Date")
    private Date registeDate;

    /**
     *注册IP
     */
    @Column(name = "registe_ip")
    @ApiModelProperty(value = "registeIp", name = "注册IP", dataType = "String")
    private String registeIp;

    /**
     *注册机器码
     */
    @Column(name = "registe_macid")
    @ApiModelProperty(value = "registeMacid", name = "注册机器码", dataType = "String")
    private String registeMacid;

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
