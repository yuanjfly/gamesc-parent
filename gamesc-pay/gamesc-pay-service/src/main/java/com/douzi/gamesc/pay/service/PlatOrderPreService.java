package com.douzi.gamesc.pay.service;

import com.douzi.gamesc.common.pojo.order.PlatOrderPre;

public interface PlatOrderPreService {

    public void insert(PlatOrderPre platOrderPre);

    public void update(PlatOrderPre platOrderPre);

    public PlatOrderPre getOneByOrderNo(String orderNo);

}
