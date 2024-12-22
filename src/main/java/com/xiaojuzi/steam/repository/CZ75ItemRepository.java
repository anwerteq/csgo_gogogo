package com.xiaojuzi.steam.repository;

import com.xiaojuzi.steam.entity.CZ75Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CZ75ItemRepository extends JpaRepository<CZ75Item, Long> {

    // 如果需要查询多个结果，可以返回 List<CZ75Item>
    List<CZ75Item> findAllBySteamInventoryMarkId(String steamInventoryMarkId);


    /**
     * 根据多个 steamInventoryMarkId 查询 CZ75Item 列表
     *
     * @param steamInventoryMarkIds steamInventoryMarkId 的集合
     * @return List<CZ75Item> 匹配的 CZ75Item 实体列表
     */
    List<CZ75Item> findBySteamInventoryMarkIdIn(List<String> steamInventoryMarkIds);
    List<CZ75Item> findBySteamInventoryMarkIdInAndTheTypeOfTransaction(List<String> steamInventoryMarkIds,String theTypeOfTransaction);





}
