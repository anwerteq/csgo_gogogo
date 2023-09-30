package com.chenerzhu.crawler.proxy.csgo.steamrepostory;

import com.chenerzhu.crawler.proxy.csgo.steamentity.AssetDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamtDescriptionRepository extends JpaRepository<AssetDescription, Integer> {

}
