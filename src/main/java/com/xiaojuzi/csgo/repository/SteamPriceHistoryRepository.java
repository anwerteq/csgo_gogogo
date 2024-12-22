package com.xiaojuzi.csgo.repository;

import com.xiaojuzi.csgo.entity.BuffPriceHistoryPk;
import com.xiaojuzi.csgo.entity.SteamPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamPriceHistoryRepository extends JpaRepository<SteamPriceHistory, BuffPriceHistoryPk> {

}
