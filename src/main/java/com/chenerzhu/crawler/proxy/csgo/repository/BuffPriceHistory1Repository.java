package com.chenerzhu.crawler.proxy.csgo.repository;

import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistory1;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffPriceHistoryPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuffPriceHistory1Repository extends JpaRepository<BuffPriceHistory1, BuffPriceHistoryPk> {

}
