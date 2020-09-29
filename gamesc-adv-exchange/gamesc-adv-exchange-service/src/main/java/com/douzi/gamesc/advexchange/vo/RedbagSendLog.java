package com.douzi.gamesc.advexchange.vo;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="redbag_send_log")
@Data
public class RedbagSendLog {

    @Id
    private String id;
    private Long userId;
    private String nonceStr;
    private String mchBillno;
    private String mchId;
    private String reOpenid;
    private int totalAmount;
    private int totalNum;
    private String clientIp;
    private String actName;
    private String remark;
    private Date createTime;
    private int isSend;
    private String sendReson;
}
