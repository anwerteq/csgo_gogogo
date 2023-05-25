package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellBuffProfitRepository extends JpaRepository<SellBuffProfitEntity, Long> {

    @Query(value = "select * from sell_buff_profit where sell_num > 30 and sell_min_price < 50 and up_date >   CONCAT(curdate(),' 00:00:00') ORDER BY interest_rate desc", nativeQuery = true)
    List<SellBuffProfitEntity> selectSellBuffItem();

}
