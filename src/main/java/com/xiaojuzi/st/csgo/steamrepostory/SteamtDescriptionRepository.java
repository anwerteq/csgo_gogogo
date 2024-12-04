package com.xiaojuzi.st.csgo.steamrepostory;

import com.xiaojuzi.st.csgo.steamentity.AssetDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamtDescriptionRepository extends JpaRepository<AssetDescription, Integer> {

}
