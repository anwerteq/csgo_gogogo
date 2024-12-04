package com.xiaojuzi.st.csgo.repository;

import com.xiaojuzi.st.csgo.entity.BuffPriceHistoryPk;
import com.xiaojuzi.st.csgo.entity.SteamPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamPriceHistoryRepository extends JpaRepository<SteamPriceHistory, BuffPriceHistoryPk> {

}
