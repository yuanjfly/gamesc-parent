package com.douzi.gamesc.common.pojo.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Table(name = "account_security_info")
@ApiModel(value = "AccountSecurityInfo对象", description = "账户安全实体类")
@Data
public class AccountSecurityInfo {

    /**
     * 平台用户Id
     */
    @Id
    @Column(name = "user_id")
    @ApiModelProperty(value = "userId", name = "平台用户Id", dataType = "Long")
    private Long userId;

    /**
     *身份证ID
     */
    @Column(name = "idc_id")
    @ApiModelProperty(value = "idcId", name = "身份证ID", dataType = "String")
    private String idcId;

    /**
     *电话号码
     */
    @Column(name = "phone_num")
    @ApiModelProperty(value = "phoneNum", name = "电话号码", dataType = "String")
    private String phoneNum;

    /**
     *邮箱地址
     */
    @Column(name = "email_addr")
    @ApiModelProperty(value = "emailAddr", name = "邮箱地址", dataType = "String")
    private String emailAddr;

    /**
     *支付宝账号
     */
    @Column(name = "alipay_addr")
    @ApiModelProperty(value = "alipayAddr", name = "支付宝账号", dataType = "String")
    private String alipayAddr;

    /**
     *微信AppID
     */
    @Column(name = "wx_app_id")
    @ApiModelProperty(value = "wxAppId", name = "微信AppID", dataType = "String")
    private String wxAppId;


    /**
     *微信openid
     */
    @Column(name = "wx_open_id")
    @ApiModelProperty(value = "wxOpenId", name = "微信openid", dataType = "String")
    private String wxOpenId;


    /**
     *微信unionid
     */
    @Column(name = "wx_union_id")
    @ApiModelProperty(value = "wxUnionId", name = "微信unionid", dataType = "String")
    private String wxUnionId;

    /**
     *家庭地址
     */
    @Column(name = "home_addr")
    @ApiModelProperty(value = "homeAddr", name = "家庭地址", dataType = "String")
    private String homeAddr;

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
