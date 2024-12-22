package com.xiaojuzi.csgo.repository;

import com.xiaojuzi.csgo.entity.BuffPriceHistory1;
import com.xiaojuzi.csgo.entity.BuffPriceHistoryPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuffPriceHistory1Repository extends JpaRepository<BuffPriceHistory1, BuffPriceHistoryPk> {

}
