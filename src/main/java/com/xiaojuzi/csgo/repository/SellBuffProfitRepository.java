package com.xiaojuzi.csgo.repository;

import com.xiaojuzi.csgo.profitentity.SellBuffProfitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellBuffProfitRepository extends JpaRepository<SellBuffProfitEntity, Long> {

    /**
     * steam上架物品需要用来判断
     * @return
     */
    @Query(value = "select * from sell_buff_profit where sell_num > 30 and sell_min_price < 50  ORDER BY interest_rate desc", nativeQuery = true)
    List<SellBuffProfitEntity> selectSellBuffItem();

}
