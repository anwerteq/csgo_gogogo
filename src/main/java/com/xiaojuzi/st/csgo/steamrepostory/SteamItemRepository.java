package com.xiaojuzi.st.csgo.steamrepostory;

import com.xiaojuzi.st.csgo.steamentity.SteamItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamItemRepository extends JpaRepository<SteamItem, String> {

}
