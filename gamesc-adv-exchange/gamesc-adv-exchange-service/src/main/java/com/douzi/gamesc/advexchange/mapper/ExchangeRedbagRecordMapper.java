package com.douzi.gamesc.advexchange.mapper;

import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import tk.mybatis.mapper.additional.aggregation.AggregationMapper;
import tk.mybatis.mapper.common.Mapper;

public interface ExchangeRedbagRecordMapper extends Mapper<ExchangeRedbagRecord>,AggregationMapper<ExchangeRedbagRecord> {

}
