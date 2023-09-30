package com.chenerzhu.crawler.proxy.csgo.repository;

import com.chenerzhu.crawler.proxy.csgo.entity.Goods_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsInfoRepository extends JpaRepository<Goods_info, Long> {

}
