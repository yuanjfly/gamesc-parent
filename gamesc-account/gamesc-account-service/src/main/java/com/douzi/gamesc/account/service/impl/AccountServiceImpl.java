package com.douzi.gamesc.account.service.impl;

import com.douzi.gamesc.account.mapper.AccountMainInfoMapper;
import com.douzi.gamesc.account.mapper.AccountSecurityInfoMapper;
import com.douzi.gamesc.account.mapper.AccountThirdPartInfoMapper;
import com.douzi.gamesc.account.service.AccountService;
import com.douzi.gamesc.account.utils.AccountRedisUtils;
import com.douzi.gamesc.common.pojo.account.AccountMainInfo;
import com.douzi.gamesc.common.pojo.account.AccountSecurityInfo;
import com.douzi.gamesc.common.pojo.account.AccountThirdPartInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountSecurityInfoMapper accountSecurityInfoMapper;

    @Autowired
    private AccountThirdPartInfoMapper accountThirdPartInfoMapper;

    @Autowired
    private AccountMainInfoMapper accountMainInfoMapper;

    @Override
    public AccountMainInfo getAccountMainInfo(long userId){
       return accountMainInfoMapper.selectByPrimaryKey(userId);
    }

    @Override
    public AccountSecurityInfo getAccountSecurityInfoByPhone(String prodcutId,String phoneNum){
        return accountSecurityInfoMapper.getAccountSecurityInfoByPhone(prodcutId,phoneNum);
    }

    @Override
    public AccountSecurityInfo getAccountSecurityInfoByAliAccount(String prodcutId,String account){
        return accountSecurityInfoMapper.getAccountSecurityInfoByAliAccount(prodcutId,account);
    }

    @Override
    public AccountSecurityInfo getAccountSecurityInfoByUserId(long userId) {
        Example example = new Example(AccountSecurityInfo.class);
        example.createCriteria().andEqualTo("userId",userId);
        return accountSecurityInfoMapper.selectOneByExample(example);
    }

    @Override
    public void saveAccountSecurityInfo(AccountSecurityInfo info){
        accountSecurityInfoMapper.insertSelective(info);
    }

    @Override
    public void updateAccountSecurityInfo(AccountSecurityInfo info) {
        Example example = new Example(AccountSecurityInfo.class);
        example.createCriteria().andEqualTo("userId",info.getUserId());
        accountSecurityInfoMapper.updateByExampleSelective(info,example);
    }

    @Override
    public AccountThirdPartInfo getAccountThirdPartInfo(String appId,String unionId,int type){
        Example example = new Example(AccountThirdPartInfo.class);
        example.createCriteria().andEqualTo("thirdAppId",appId).andEqualTo("unionId",unionId).andEqualTo("accountType",type);
        return accountThirdPartInfoMapper.selectOneByExample(example);
    }

    @Override
    public AccountThirdPartInfo getAccountThirdPartInfoByUserId(long userId,String appId){
        Example example = new Example(AccountThirdPartInfo.class);
        example.createCriteria().andEqualTo("thirdAppId",appId).andEqualTo("userId",userId);
        return accountThirdPartInfoMapper.selectOneByExample(example);
    }
}
