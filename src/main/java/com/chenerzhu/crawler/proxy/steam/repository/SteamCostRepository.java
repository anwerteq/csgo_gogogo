package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamCostRepository extends JpaRepository<SteamCostEntity, Long> {


    /**
     * 查询下订单产生的信息
     * @param hashName
     * @return
     */
    @Query(value = "select * from steam_cost where hash_name ?1 and buy_status = 0  limit 1  ", nativeQuery = true)
    SteamCostEntity selectByHashName(String hashName);

}
