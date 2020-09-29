package com.douzi.gamesc.account.service;

import com.douzi.gamesc.common.pojo.account.AccountMainInfo;
import com.douzi.gamesc.common.pojo.account.AccountSecurityInfo;
import com.douzi.gamesc.common.pojo.account.AccountThirdPartInfo;

public interface AccountService {

    /**
     * 获取用户主体信息
     * @param userId
     * @return
     */
    public AccountMainInfo getAccountMainInfo(long userId);

    /**
     * 获取用户安全信息
     * @param prodcutId 产品Id
     * @param phoneNum
     * @return
     */
    public AccountSecurityInfo getAccountSecurityInfoByPhone(String prodcutId,String phoneNum);

    /**
     * 获取用户安全信息
     * @param prodcutId 产品Id
     * @param account 支付宝账号
     * @return
     */
    public AccountSecurityInfo getAccountSecurityInfoByAliAccount(String prodcutId,String account);

    /**
     * 获取用户安全信息
     * @param userId
     * @return
     */
    public AccountSecurityInfo getAccountSecurityInfoByUserId(long userId);

    /**
     * 新增账号安全信息
     * @param info
     */
    public void saveAccountSecurityInfo(AccountSecurityInfo info);

    /**
     * 更新账号安全信息
     * @param info
     */
    public void updateAccountSecurityInfo(AccountSecurityInfo info);

    /**
     * 获取第三方账号信息
     * @param appId 第三方产品ID
     * @param unionId 第三方用户公司唯一标识
     * @param type 注册来源  0:快速 1:渠道skd  2微信 3支付宝 4手机
     * @return
     */
    public AccountThirdPartInfo getAccountThirdPartInfo(String appId,String unionId,int type);

    /**
     * 根据平台用户获取第三方账号信息
     * @param userId
     * @return
     */
    public AccountThirdPartInfo getAccountThirdPartInfoByUserId(long userId,String appId);
}
