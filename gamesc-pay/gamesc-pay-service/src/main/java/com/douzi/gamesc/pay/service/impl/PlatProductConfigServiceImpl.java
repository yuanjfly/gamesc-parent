package com.douzi.gamesc.pay.service.impl;

import com.douzi.gamesc.common.pojo.order.PlatProductConfig;
import com.douzi.gamesc.pay.mapper.PlatProductConfigMapper;
import com.douzi.gamesc.pay.service.PlatProductConfigService;
import com.douzi.gamesc.pay.utils.RedisUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class PlatProductConfigServiceImpl implements PlatProductConfigService {

    @Autowired
    private PlatProductConfigMapper platProductConfigMapper;

    @Override
    public PlatProductConfig getPlatProductConfig(String appId,int productId) {

        Example example = new Example(PlatProductConfig.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appId",appId).andEqualTo("productId",productId);
        PlatProductConfig config = platProductConfigMapper.selectOneByExample(example);
        return config;
    }
}
