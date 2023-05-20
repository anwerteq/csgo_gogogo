package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistory1;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistoryPk;
import com.chenerzhu.crawler.proxy.pool.csgo.profitentity.ProfitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitRepository extends JpaRepository<ProfitEntity, Long> {

}
