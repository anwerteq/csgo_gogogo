package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellBuffProfitEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.SellSteamProfitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SellSteamProfitRepository extends JpaRepository<SellSteamProfitEntity, Long> {

    @Query(value = "select * from sell_steam_profit where sell_num > 100 and buff_price < 50  and buff_price > 0.1  and interest_rate < '0.73' and in_fact_sell_steam_price >=  1  and up_date >   CONCAT(curdate(),' 00:00:00') ORDER BY interest_rate ", nativeQuery = true)
    List<SellSteamProfitEntity> selectOrderAsc();


    @Query(value = " select item_id ,hash_name from sell_steam_profit  ORDER BY interest_rate ", nativeQuery = true)
    List<Map<String,String>> selectItemIdANdHashName();

}


