package com.xiaojuzi.steam.repository;

import com.xiaojuzi.steam.entity.SteamCostEntity;
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
    @Query(value = "select * from steam_cost where hash_name  = ?1 and buy_status = 0  limit 1  ", nativeQuery = true)
    SteamCostEntity selectByHashName(String hashName);

    @Query(value = "select * from steam_cost where hash_name  = ?1  ORDER BY   steam_cost desc  limit 1  ", nativeQuery = true)
    SteamCostEntity selectByHashNameNotStatus(String hashName);




    /**
     * 查询下订单产生的信息
     * @param hashName
     * @return
     */
    @Query(value = "select * from steam_cost where assetid = ?1 and classid = ?2   limit 1  ", nativeQuery = true)
    SteamCostEntity selectByAssetId(String assetid,String classid);



    /**
     * 查询下订单产生的信息
     * @param
     * @return
     */
    @Query(value = "select * from steam_cost where assetid = ?1 and classid = ?2   limit 1  ", nativeQuery = true)
    SteamCostEntity selectByAssetIdNotBuyStatus(String assetid,String classid);
}
