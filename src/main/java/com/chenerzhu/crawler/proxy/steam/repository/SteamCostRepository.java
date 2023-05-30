package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamCostRepository extends JpaRepository<SteamCostEntity, Long> {

}
