package com.xue.demo.shardingjdbc.myself;

import io.shardingsphere.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: xuexiong@souche.com
 * @date: 2020-09-07 16:30
 * @description: 复合数据库-分库算法例子
 */
public class DbShardingAlgorithm implements ComplexKeysShardingAlgorithm {
    private static Logger logger = LoggerFactory.getLogger(DbShardingAlgorithm.class);
    // 取模因子
    public static final Integer MODE_FACTOR = 1331;

    /**
     * @param availableTargetNames 所有的数据库
     * @param shardingValues       分库键以及值
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames
            , Collection<ShardingValue> shardingValues) {

        List<String> shardingResults = new ArrayList<>();
        Long shardingIndex = getIndex(shardingValues) % availableTargetNames.size();
        // loop and match datasource
        for (String name : availableTargetNames) {
            // get logic datasource index suffix
            String nameSuffix = name.substring(name.length() - 1);
            if (nameSuffix.equals(shardingIndex.toString())) {
                shardingResults.add(name);
                break;
            }
        }

        logger.info("DataSource sharding index ： {}", shardingIndex);
        return shardingResults;
    }

    /**
     * get datasource sharding index <p>
     * sharding algorithm : shardingIndex = (orderId + userId.hashCode()) % db.size
     *
     * @param shardingValues
     * @return
     */
    private long getIndex(Collection<ShardingValue> shardingValues) {
        long shardingIndex = 0L;
        ListShardingValue listShardingValue;

        for (ShardingValue sVal : shardingValues) {
            listShardingValue = (ListShardingValue) sVal;
            if ("id".equals(listShardingValue.getColumnName())) {
                List<Long> shardingValue = (List<Long>) listShardingValue.getValues();
                shardingIndex += Math.abs(shardingValue.get(0)) % MODE_FACTOR;
            } else if ("user_id".equals(listShardingValue.getColumnName())) {
                List<String> shardingValue = (List<String>) listShardingValue.getValues();
                //这里  % 1313 仅仅只是防止溢出
                shardingIndex += Math.abs(shardingValue.get(0).hashCode()) % MODE_FACTOR;
            }
        }
        return shardingIndex;
    }
}
