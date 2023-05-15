package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.ItemGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IItemGoodsRepository extends JpaRepository<ItemGoods, Long> {

    @Query(value = "select id from item_goods", nativeQuery = true)
    List<Long> findAllId();

}
