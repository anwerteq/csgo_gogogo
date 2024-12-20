package com.chenerzhu.crawler.proxy.steam.repository;

import com.chenerzhu.crawler.proxy.steam.entity.Descriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescriptionsRepository extends JpaRepository<Descriptions, String> {



    // 根据 steamId 删除记录
    void deleteBySteamId(String steamId);

    /**
     * 查询某个id的库存信息
     * @param steamId
     * @return
     */
    List<Descriptions> findAllBySteamId(String steamId);

}
