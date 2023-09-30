package com.chenerzhu.crawler.proxy.csgo.repository;

import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistoryPk;
import com.chenerzhu.crawler.proxy.csgo.entity.SteamPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamPriceHistoryRepository extends JpaRepository<SteamPriceHistory, BuffPriceHistoryPk> {

}
