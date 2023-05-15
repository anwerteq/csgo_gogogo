package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.ItemGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IItemGoodsRepository extends JpaRepository<ItemGoods, Long> {

}
