package com.douzi.gamesc.account.pojo;


import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="sms_verify_code_record")
@Data
public class SmsVerifyCodeRecord {

    @Id
    private String id;
    private String phone;
    private String business;
    private String code;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
