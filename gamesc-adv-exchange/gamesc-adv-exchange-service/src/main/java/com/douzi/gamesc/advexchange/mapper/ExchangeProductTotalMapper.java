package com.douzi.gamesc.advexchange.mapper;

import com.douzi.gamesc.common.pojo.exhange.ExchangeProductTotal;
import tk.mybatis.mapper.additional.aggregation.AggregationMapper;
import tk.mybatis.mapper.common.Mapper;

public interface ExchangeProductTotalMapper extends Mapper<ExchangeProductTotal>,AggregationMapper<ExchangeProductTotal> {
}
