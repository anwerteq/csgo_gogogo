package com.xiaojuzi.csgo.repository;

import com.xiaojuzi.csgo.entity.Goods_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsInfoRepository extends JpaRepository<Goods_info, Long> {

}
