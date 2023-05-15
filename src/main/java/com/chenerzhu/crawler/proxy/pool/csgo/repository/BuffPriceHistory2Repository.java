package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistory2;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistoryPk;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.Tag;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.TagPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuffPriceHistory2Repository extends JpaRepository<BuffPriceHistory2, BuffPriceHistoryPk> {

    @Query(value = "select DISTINCT max(up_time_stamp) from buff_price_history2  where item_id = ?1  limit 1", nativeQuery = true)
    long findlastUpByItemId(long item_id);
}
