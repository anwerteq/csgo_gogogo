package com.chenerzhu.crawler.proxy.csgo.repository;

import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistory2;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistoryPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuffPriceHistory2Repository extends JpaRepository<BuffPriceHistory2, BuffPriceHistoryPk> {

    @Query(value = "select DISTINCT max(up_time_stamp) from buff_price_history2  where item_id = ?1  limit 1", nativeQuery = true)
    Long findlastUpByItemId(long item_id);
}
