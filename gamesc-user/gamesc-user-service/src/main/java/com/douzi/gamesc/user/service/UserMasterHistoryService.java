package com.douzi.gamesc.user.service;

import com.douzi.gamesc.common.pojo.game.GameUserMasterHistory;

public interface UserMasterHistoryService {

    GameUserMasterHistory getGameUserMasterHistoryByListDate(long userId,String listDate);

    int updateGameUserMasterHistory(GameUserMasterHistory record,long userId, String listDate);
}
