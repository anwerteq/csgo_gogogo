package com.chenerzhu.crawler.proxy.csgo.steamrepostory;

import com.chenerzhu.crawler.proxy.csgo.steamentity.SteamItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamItemRepository extends JpaRepository<SteamItem, String> {

}
