package com.douzi.gamesc.account.mapper;

import com.douzi.gamesc.common.pojo.account.AccountSecurityInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface AccountSecurityInfoMapper extends Mapper<AccountSecurityInfo> {

    @Select("SELECT ac.* "
            + "from account_security_info ac LEFT JOIN account_main_info a on ac.user_id=a.user_id "
            + "where a.prod_id=#{prodcutId} and ac.phone_num=#{phoneNum}")
    @Results({
            @Result(property = "userId",column = "user_id"),
            @Result(property = "idcId",column = "idc_id"),
            @Result(property = "phoneNum",column = "phone_num"),
            @Result(property = "emailAddr",column = "email_addr"),
            @Result(property = "alipayAddr",column = "alipay_addr"),
            @Result(property = "homeAddr",column = "home_addr"),
            @Result(property = "wxAppId",column = "wx_app_id"),
            @Result(property = "wxOpenId",column = "wx_open_id"),
            @Result(property = "wxUnionId",column = "wx_union_id"),
            @Result(property = "createTime",column = "create_time"),
            @Result(property = "updateTime",column = "update_time")
    })
    public AccountSecurityInfo getAccountSecurityInfoByPhone(@Param("prodcutId")String prodcutId,@Param("phoneNum") String phoneNum);


    @Select("SELECT ac.* "
            + "from account_security_info ac LEFT JOIN account_main_info a on ac.user_id=a.user_id "
            + "where a.prod_id=#{prodcutId} and ac.alipay_addr=#{account}")
    @Results({
            @Result(property = "userId",column = "user_id"),
            @Result(property = "idcId",column = "idc_id"),
            @Result(property = "phoneNum",column = "phone_num"),
            @Result(property = "emailAddr",column = "email_addr"),
            @Result(property = "alipayAddr",column = "alipay_addr"),
            @Result(property = "homeAddr",column = "home_addr"),
            @Result(property = "wxAppId",column = "wx_app_id"),
            @Result(property = "wxOpenId",column = "wx_open_id"),
            @Result(property = "wxUnionId",column = "wx_union_id"),
            @Result(property = "createTime",column = "create_time"),
            @Result(property = "updateTime",column = "update_time")
    })
    public AccountSecurityInfo getAccountSecurityInfoByAliAccount(@Param("prodcutId")String prodcutId,@Param("account") String account);
}
