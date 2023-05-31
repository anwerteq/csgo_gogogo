package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import com.chenerzhu.crawler.proxy.steam.entity.SteamCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CookeisRepository extends JpaRepository<Cookeis, Long> {




}
