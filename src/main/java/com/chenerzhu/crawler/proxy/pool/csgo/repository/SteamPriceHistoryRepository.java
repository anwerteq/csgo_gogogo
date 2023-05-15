package com.chenerzhu.crawler.proxy.pool.csgo.repository;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistory1;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistoryPk;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.SteamPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamPriceHistoryRepository extends JpaRepository<SteamPriceHistory, BuffPriceHistoryPk> {

}
