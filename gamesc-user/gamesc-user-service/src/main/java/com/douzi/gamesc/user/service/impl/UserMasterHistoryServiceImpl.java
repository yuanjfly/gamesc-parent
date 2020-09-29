package com.douzi.gamesc.user.service.impl;

import com.douzi.gamesc.common.pojo.exhange.ExchangeBindUser;
import com.douzi.gamesc.common.pojo.game.GameUserMasterHistory;
import com.douzi.gamesc.user.mapper.GameUserMasterHistoryMapper;
import com.douzi.gamesc.user.service.UserMasterHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class UserMasterHistoryServiceImpl implements UserMasterHistoryService {

    @Autowired
    private GameUserMasterHistoryMapper gameUserMasterHistoryMapper;

    @Override
    public GameUserMasterHistory getGameUserMasterHistoryByListDate(long userId, String listDate) {
        Example example = new Example(GameUserMasterHistory.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("listDate",listDate);
        return gameUserMasterHistoryMapper.selectOneByExample(example);
    }

    @Override
    public int updateGameUserMasterHistory(GameUserMasterHistory record,long userId, String listDate) {
        Example example = new Example(GameUserMasterHistory.class);
        example.createCriteria()
                .andEqualTo("userId",userId)
                .andEqualTo("listDate",listDate)
                .andEqualTo("ifreceive",0);
        return  gameUserMasterHistoryMapper.updateByExampleSelective(record,example);
    }
}
