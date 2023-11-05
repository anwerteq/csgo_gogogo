package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.csgo.BuffBuyItemEntity.Items;
import com.chenerzhu.crawler.proxy.csgofloat.CsgoFloatService;
import com.chenerzhu.crawler.proxy.steam.service.SteamMyhistoryService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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

    @Autowired
    CsgoFloatService csgoFloatService;

    /**
     * 设置成本的主要逻辑
     */
    public void assetRemarkChange() {
        List<Items> allStatusInventory = getAllStatusInventory();
        Map<String, String> urlAndPriceMap = new HashMap<>();
        int page_index = 1;
        while (true) {
            //饰品购买的价格
            Map<String, String> map = steamMyhistoryService.marketMyhistorys(page_index++);
            if (map == null) {
                break;
            }
            SleepUtil.sleep(5 * 1000);
            urlAndPriceMap.putAll(map);
        }
        //设置buff的磨损
        allStatusInventory = csgoFloatService.postBuffBulks(allStatusInventory);
        //获取磨损度和价格的映射
        Map<String, String> wearAndPriceMap = csgoFloatService.postBulks(urlAndPriceMap);
        remarkChanges(allStatusInventory, wearAndPriceMap);
    }


    /**
     * 获取steam的饰品数据
     *
     * @return
     */
    public Map<String, String> getSteamWearMap(Map<String, String> itemOnlyKeyAndPriceMap) {

        return null;
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
        Map<String, String> headerMap = BuffConfig.getHeaderMap1();

        Map<String, String> headerMap1 = new HashMap<>();
        headerMap1.put("Cookie", BuffConfig.getCookie());
        headerMap1.put("Referer", "https://buff.163.com/market/steam_inventory?game=csgo");
        headerMap1.put("X-Csrftoken", BuffConfig.getCookieOnlyKey("csrf_token"));
        headerMap1.put("Origin", "https://buff.163.com");
        headerMap1.put("Content-Type", "application/json");
        headerMap1.put("X-Requested-With", "XMLHttpRequest");
//        headerMap1.put("Accept", "*/*");
        HttpEntity<MultiValueMap<String, String>> httpEntity = BuffConfig.changeBuffHttpEntity(headerMap1);
//        ResponseEntity<String> responseEntity1 = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class, dataMap);
        String reponse = HttpClientUtils.sendPost(url, JSONObject.toJSONString(dataMap), headerMap1);
        JSONObject jsonObject = JSONObject.parseObject(reponse);
        String code = jsonObject.getString("code");
        if (StrUtil.isEmpty(code)) {
            log.error("饰品备注失败:错误信息为:{}", reponse);
            return;
        }
        log.error("饰品备注成功");
    }

    /**
     * 构建修改成本价参数
     *
     * @return
     */
    public Map<String, Object> buildRemarkParamer(List<Items> items, Map<String, String> map) {
        List<Map<String, Object>> assets = new ArrayList<>();
        for (Items item : items) {
            Map<String, Object> hashMap = new HashMap();
            String painwear = item.getPainwear();
            String dollar = map.getOrDefault(painwear, "");
            if (StrUtil.isEmpty(dollar)) {
                continue;
            }
            hashMap.put("remark", "人民币购买成本:" + getCostRmb(dollar) + "元");
            hashMap.put("assetid", Long.parseLong(item.getAsset_info().getAssetid()));
            assets.add(hashMap);
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
        Double rmb = dollarD * cark_cost;
        return rmb;
    }


}
