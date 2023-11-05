package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.steam.service.SteamMyhistoryService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    @Value("${cark_cost}")
    Double cark_cost;

    @Autowired
    SteamInventorySerivce steamInventorySerivce;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamMyhistoryService steamMyhistoryService;

    /**
     * 设置成本的主要逻辑
     */
    public void assetRemarkChange() {
        List<Items> allStatusInventory = getAllStatusInventory();
        Map<String, String> itemOnlyKeyAndPriceMap = new HashMap<>();
        int page_index = 1;
        while (true) {
            //饰品购买的价格
            Map<String, String> map = steamMyhistoryService.marketMyhistorys(page_index++);
            if (map == null) {
                break;
            }
            SleepUtil.sleep(5 * 1000);
            itemOnlyKeyAndPriceMap.putAll(map);
        }
        remarkChanges(allStatusInventory, itemOnlyKeyAndPriceMap);
    }


    /**
     * 获取库存的全部信息
     *
     * @return
     */
    public List<Items> getAllStatusInventory() {
        List<Items> list = new ArrayList<>();
        int pageIndex = 1;
        while (true) {
            List<Items> list1 = steamInventorySerivce.steamAllStatusInventory(pageIndex++);
            list.addAll(list1);
            if (list1.isEmpty() || list1.size() < 500) {
                break;
            }

        }
        return list;
    }

    /**
     * 批量设置备注信息
     *
     * @param items
     * @param map
     */
    public void remarkChanges(List<Items> items, Map<String, String> map) {
        List<Items> itemsChanges = new ArrayList();
        for (Items item : items) {
            itemsChanges.add(item);
            if (itemsChanges.size() > 40) {
                remarkChange(itemsChanges, map);
                itemsChanges.clear();
            }
        }
        remarkChange(itemsChanges, map);
    }

    /**
     * 批量设置备注信息
     *
     * @param items
     * @param map
     */
    public void remarkChange(List<Items> items, Map<String, String> map) {
        String url = "https://buff.163.com/api/market/steam_asset_remark/change";
        Map<String, Object> dataMap = buildRemarkParamer(items, map);
        Map<String, String> headerMap1 = BuffConfig.getHeaderMap1();
        headerMap1.put("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headerMap1.put("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        HttpEntity<MultiValueMap<String, String>> httpEntity = BuffConfig.changeBuffHttpEntity(headerMap1);
        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class, dataMap);
        System.out.println("123123");


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
            String dollar = map.getOrDefault(item.getClassidInstanceid(), "");
            if (StrUtil.isEmpty(dollar)) {
                continue;
            }
            hashMap.put("remark", "人民币购买成本:" + getCostRmb(dollar));
            hashMap.put("assetid", item.getAsset_info().getAssetid());
        }
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("game", "csgo");
        hashMap.put("assets", assets);
        return hashMap;
    }


    /**
     * 转化成rmb
     *
     * @param dollar
     * @return
     */
    public Double getCostRmb(String dollar) {
        Double dollarD = Double.valueOf(dollar);
        Double rmb = BuffApplicationRunner.cny * dollarD * cark_cost;
        return rmb;
    }


}
