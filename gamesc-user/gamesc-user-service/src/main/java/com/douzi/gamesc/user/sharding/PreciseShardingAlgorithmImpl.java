package com.douzi.gamesc.user.sharding;

import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;


public class PreciseShardingAlgorithmImpl implements PreciseShardingAlgorithm<Long>{

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        String dbName = "ds";
        Long val = shardingValue.getValue();
        int tag = (val <= 5000000 ? 0 : 1);
        dbName += tag;
        for (String each : availableTargetNames) {
            if (each.equals(dbName)) {
                System.out.println("select db:"+each);
                return each;
            }
        }
        throw new IllegalArgumentException();
    }

    /*@Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        String columnName = shardingValue.getColumnName();
        String tableName = shardingValue.getLogicTableName();
        String target = shardingValue.getValue().toString();
        log.debug("column:{},tableName:{},value:{}", new Object[]{columnName, tableName, target});
        int shardingCount = availableTargetNames.size();
        int tag = Long.parseLong(target) <= 10 ? 0 : 1;
        Iterator it = availableTargetNames.iterator();
        String targetName;
        do {
            if (!it.hasNext()) {
                throw new UnsupportedOperationException();
            }
            targetName = (String) it.next();
        } while (!targetName.endsWith(tag + ""));

        log.debug("recent return target is : {}", targetName);
        return targetName;
    }*/
}
