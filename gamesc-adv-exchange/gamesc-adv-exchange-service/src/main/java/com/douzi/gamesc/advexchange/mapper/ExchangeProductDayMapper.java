package com.douzi.gamesc.advexchange.mapper;

import com.douzi.gamesc.common.pojo.exhange.ExchangeProductDay;
import tk.mybatis.mapper.additional.aggregation.AggregationMapper;
import tk.mybatis.mapper.common.Mapper;

public interface ExchangeProductDayMapper extends Mapper<ExchangeProductDay>,AggregationMapper<ExchangeProductDay> {
}
