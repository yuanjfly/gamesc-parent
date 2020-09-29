package com.douzi.gamesc.user.service.impl;

import com.douzi.gamesc.common.pojo.game.GameUserBackpack;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;
import com.douzi.gamesc.user.mapper.GameUserBackpackMapper;
import com.douzi.gamesc.user.mapper.GameUserCurrencyMapper;
import com.douzi.gamesc.user.service.UserPropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserPropServiceImpl implements UserPropService {

    @Autowired
    private GameUserCurrencyMapper gameUserCurrencyMapper;

    @Autowired
    private GameUserBackpackMapper gameUserBackpackMapper;


    @Override
    public GameUserCurrency getGameUserCurrency(long userId) {
        Example example = new Example(GameUserCurrency.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        return  gameUserCurrencyMapper.selectOneByExample(example);
    }

    @Override
    public GameUserBackpack getGameUserBackpack(long userId,int propId){
        Example example = new Example(GameUserBackpack.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId).andEqualTo("propId",propId);
        return  gameUserBackpackMapper.selectOneByExample(example);
    }


}
