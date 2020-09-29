package com.douzi.gamesc.pay.service.impl;

import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.mapper.PlatOrderPreMapper;
import com.douzi.gamesc.pay.service.PlatOrderPreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class PlatOrderPreServiceImpl implements PlatOrderPreService {

    @Autowired
    private PlatOrderPreMapper platOrderPreMapper;

    @Override
    public void insert(PlatOrderPre platOrderPre) {
        platOrderPreMapper.insert(platOrderPre);
    }

    @Override
    public void update(PlatOrderPre platOrderPre) {
        platOrderPreMapper.updateByPrimaryKey(platOrderPre);
    }

    @Override
    public PlatOrderPre getOneByOrderNo(String orderNo) {

        Example example = new Example(PlatOrderPre.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo",orderNo);
        return platOrderPreMapper.selectOneByExample(example);
    }
}
