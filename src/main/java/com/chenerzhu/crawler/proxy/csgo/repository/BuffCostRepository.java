package com.chenerzhu.crawler.proxy.csgo.repository;

import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuffCostRepository extends JpaRepository<BuffCostEntity, Long> {

    /**
     * 校验商品是否存在记录表
     * @param assetid
     * @param classid
     * @return
     */
    @Query(value = "select count(1) from buff_cost where assetid= ?1 and classid =?2", nativeQuery = true)
    int exitClassAndAssetid(long assetid, long classid);

    /**
     * 查询商品购买记录
     * @param assetid
     * @param classid
     * @return
     */
    @Query(value = "select * from buff_cost where assetid= ?1 limit 1", nativeQuery = true)
    BuffCostEntity selectOne(long assetid);


    /**
     * 查询商品购买记录
     * @param assetid
     * @param classid
     * @return
     */
    @Query(value = "select * from buff_cost where assetid= ?1 and classid = ?2  limit 1", nativeQuery = true)

    BuffCostEntity selectOne(long assetid,long classid);


    /**
     * 查询商品购买记录
     * @param hashName
     * @return
     */
    @Query(value = "select * from buff_cost where  hash_name = ?1 and is_mate = 0 order by create_time limit 1", nativeQuery = true)
    BuffCostEntity selectOne(String  hashName);

    @Query(value = " select * from buff_cost where  hash_name = ?1  order by buff_cost desc  limit 1", nativeQuery = true)
    BuffCostEntity selectOneNotMate(String  hashName);
}
