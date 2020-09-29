package com.douzi.gamesc.user.service;

import com.douzi.gamesc.common.pojo.game.GameUserBackpack;
import com.douzi.gamesc.common.pojo.game.GameUserCurrency;

public interface UserPropService {

    public GameUserCurrency getGameUserCurrency(long userId);

    public GameUserBackpack getGameUserBackpack(long userId,int propId);


}
