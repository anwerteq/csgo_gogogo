package com.chenerzhu.crawler.proxy.pool.csgo.steamrepostory;

import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistory2;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.BuffPriceHistoryPk;
import com.chenerzhu.crawler.proxy.pool.csgo.steamentity.AssetDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamtDescriptionRepository extends JpaRepository<AssetDescription, Integer> {

}
