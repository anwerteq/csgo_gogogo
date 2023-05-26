package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.Goods_info;
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
    @Query(value = "select * from buff_cost where assetid= ?1 and classid =?2  limit 1", nativeQuery = true)
    BuffCostEntity selectOne(long assetid, long classid);

}
