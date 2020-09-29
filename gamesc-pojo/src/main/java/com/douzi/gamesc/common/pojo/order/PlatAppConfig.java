package com.douzi.gamesc.common.pojo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "plat_app_config")
@ApiModel(value = "PlatAppConfig对象", description = "应用表类")
@Data
public class PlatAppConfig implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //会返回主键自增至
    @ApiModelProperty(value = "id", name = "应用表类主键", required = true, dataType = "Long")
    private Long id;

    /**
     * 应用Id
     */
    @Column(name = "app_id")
    @ApiModelProperty(value = "appId", name = "应用Id", dataType = "String")
    private String appId;

    /**
     * 应用密钥
     */
    @Column(name = "app_key")
    @ApiModelProperty(value = "appKey", name = "应用密钥", dataType = "String")
    private String appKey;

    /**
     * 应用名称
     */
    @Column(name = "app_name")
    @ApiModelProperty(value = "appName", name = "应用名称", dataType = "String")
    private String appName;

    /**
     * 创建日期
     */
    @Column(name = "create_time")
    @ApiModelProperty(value = "createTime", name = "创建日期", dataType = "Date")
    private Date createTime;

    private static final long serialVersionUID = 1L;

}