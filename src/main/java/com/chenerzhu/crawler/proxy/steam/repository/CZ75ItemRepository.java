package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.steam.entity.CZ75Item;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CZ75ItemRepository extends JpaRepository<CZ75Item, Long> {




}
