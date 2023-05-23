package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellSteamProfitRepository extends JpaRepository<SellSteamProfitEntity, Long> {

    @Query(value =  "select * from sell_steam_profit  where sell_num > 40 and buff_price < 10   ORDER BY interest_rate   limit 10 " , nativeQuery = true)
    public List<SellSteamProfitEntity> select();

}
