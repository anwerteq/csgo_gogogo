package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置buff购买成本
 */
@Service
@Slf4j
public class BuffSetMemoService {

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    public void assetRemarkChange() {
        List<Items> list = new ArrayList<>();
        int pageIndex = 1;
        while (true) {
            List<Items> list1 = steamInventorySerivce.steamAllStatusInventory(pageIndex++);
            if (list1.isEmpty()) {
                break;
            }
            list.addAll(list1);
        }

    }

    public void remarkChange() {
        String url = "https://buff.163.com/api/market/steam_asset_remark/change";
    }

    /**
     * 构建修改成本价参数
     *
     * @return
     */
    public Map<String, Object> buildRemarkParamer(List<Items> items, Map<String, String> map) {
        List<Map<String, String>> assets = new ArrayList<>();
        for (Items item : items) {
            Map<String, String> hashMap = new HashMap();
            hashMap.put("remark", "");
            hashMap.put("assetid", item.getAsset_info().getAssetid());
        }
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("game", "csgo");
        hashMap.put("assets", assets);
        return hashMap;
    }

    public String getCostRmb(String dollar) {

        return "";
    }


}
