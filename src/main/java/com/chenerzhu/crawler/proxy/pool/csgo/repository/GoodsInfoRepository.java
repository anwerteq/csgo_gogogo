package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.Goods_info;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.ItemGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsInfoRepository extends JpaRepository<Goods_info, Long> {

}
