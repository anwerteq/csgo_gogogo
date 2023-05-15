package com.chenerzhu.crawler.proxy.pool.csgo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.pool.csgo.entity.*;
import com.chenerzhu.crawler.proxy.pool.csgo.feign.CsgoFeign;
import com.chenerzhu.crawler.proxy.pool.csgo.repository.*;
import com.chenerzhu.crawler.proxy.pool.csgo.util.HttpsSendUtil;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemGoodsService {


    @Autowired
    HttpsSendUtil httpsSendUtil;

    @Autowired
    IProxyIpRedisService proxyIpRedisService;

    @Autowired
    IItemGoodsRepository itemRepository;

    @Autowired
    GoodsInfoRepository goodsInfoRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CsgoFeign csgoFeign;

    @Autowired
    RestTemplate restTemplate;

    static AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    BuffPriceHistory2Repository history2Repository;

    @Autowired
    BuffPriceHistory1Repository history1Repository;

    @Autowired
    SteamPriceHistoryRepository historyRepository;

    //总页数
    Integer pageCount = 1;

    //csgo请求头数据
    static Map<String, String> map = new HashMap() {
        {
            put("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
            put("Accept", "application/json, text/javascript, */*; q=0.07");
            put("X-Requested-With", "XMLHttpRequest");
            put("sec-ch-ua-mobile", "?0");
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
            put("sec-ch-ua-platform", "\"Windows\"");
            put("Sec-Fetch-Site", "same-origin");
            put("Sec-Fetch-Mode", "cors");
            put("Sec-Fetch-Dest", "empty");
            put("Referer", "http://buff.163.com/market/csgo");
            put("Cookie", "_ntes_nnid=d9c42eaaee06546264904dce6ec2e618,1666248870670; _ntes_nuid=d9c42eaaee06546264904dce6ec2e618; __bid_n=184ddc62be224e2b824207; FPTOKEN=uy78EK2vFv2hziG1KX096cYrjbZuafzi6bDDytRNfnFArd5i+wqpdIOtJfhh+jjcQLpEEgLiwEmaxCeuVoqPZmN30wQfin89xYCpI6Bzj+G6ksg+CEWonmX1HPWt2H1eefaXTOBeX4MZ72DuWgYRFqEnuV3Gn2yrAuSZrRUJEabMTCm+VpWXuaV1Wgy25HYsssOW83ZvyijT0zKOv0H9ogQMOUU9KgFnaszby+LD+5oVFtCue4AFFIEPAPyPAaX0Z5FG5rLZJFR2DTuEJ265U4omGkx0I/FCH9hgDt48yrCx4RqpTZGMn7Fa3lavStNpMg1Jqzx4CLHHJxhrGhnSSGBdpTwBRND6dXeyBmNxLsk6quJqYDVyDIJUcaenhWrWc2Qb5gcovmFeRmez/9zlyQ==|avZCqYu3/fhWFXjSqhEypRvMOncZqeOLUIyHDUos96g=|10|33d785e611a48a89009352dd7deb5a6c; Device-Id=dClYQRNmcSAs5uUWiiRJ; timing_user_id=time_7Ix5JzrCca; Locale-Supported=zh-Hans; game=csgo; steam_info_to_bind=; NTES_YD_SESS=Y3W6DesW535sbxMzmdG2pLTLLFy94Il_wxLdVH9OAA56Xu9MX1ahwnlrxFKNykvj_2hlaO8LePxSbcWO.gx2hWOXC.l5tkQF55VgiR398doQ6y78QM_LC9xZyS_el_epoYaM4IKEAw4s4uvc1XjREef1vnWTbAU5GXHgBFWdtq1GuANXexwo8HqbBcDQuhrz13sMxHDCrVhaJr4lqNUcYciMeN0CY35g2GCdCPERTVsGy; S_INFO=1684131704|0|0&60##|15989173318; P_INFO=15989173318|1684131704|1|netease_buff|00&99|null&null&null#shh&null#10#0|&0||15989173318; remember_me=U1103827335|VekMfEHVSUzj0WydiCEOm3670Luyu4Mb; session=1-xqoGj7ecAA5hzi3EvNHvrgmvZwgpSJvkjRhUGM99HTNk2030407391; csrf_token=IjUwNmMzMDUyMjgwZjhlODI5OTJmZmMwOWVjZTQ2MGY3NDk4YzYzYTIi.F0NhDg.8KKnlpj1tnyjfTdgJ_jyM3OX_Q8");
        }
    };

    /**
     * 拉取csgo商品列表
     */
    public void pullItmeGoods() {
        int pageIndex = 1;
        while (pageCount >= pageIndex) {
            pageIndex = pullOnePage(pageIndex);
            pageIndex++;
            System.out.println("正在查询的页数：" + pageIndex);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer pullOnePage(int pageNum) {
        String url1 = "https://buff.163.com/api/market/goods?game=csgo&page_num=" + pageNum + "&use_suggestion=0&_=1684057330094&page_size=80";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url1, HttpMethod.GET, getHttpEntity(), String.class);

        if (responseEntity.getStatusCode().value() == 302) {
            atomicInteger.addAndGet(1);
            if (atomicInteger.get() > 5) {
                return pageNum;
            }
            pullOnePage(pageNum);
        }
        if (true){
            throw new RuntimeException("123");
        }
        ProductList productList = JSONObject.parseObject(responseEntity.getBody(), ProductList.class);
        //获取最新页数
        pageCount = productList.getData().getTotal_page();
        List<ItemGoods> itemGoodsList = productList.getData().getItems();

        for (ItemGoods itemGoods : itemGoodsList) {
            saveItem(itemGoods);
        }

        return pageNum;
    }

    public HttpEntity<MultiValueMap<String, String>> getHttpEntity() {
        HttpHeaders headers1 = new HttpHeaders();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity<>(headers1);
        try {
            double random = Math.random() * 2000;
            int shleepTime = (int) (random) + 1500;
            Thread.sleep(shleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return entity1;
    }

    public void saveItem(ItemGoods itemGoods) {
        itemRepository.save(itemGoods);

        Goods_info goods_info = itemGoods.getGoods_info();
        goods_info.setItem_id(itemGoods.getId());
        goodsInfoRepository.save(goods_info);
        Tags tags = goods_info.getInfo().getTags();
        saveTags(tags, itemGoods.getId());
    }

    public void saveTags(Tags tags, long item_id) {
        String tagsStr = JSON.toJSONString(tags);
        HashMap<String, JSONObject> tagsHash = JSON.parseObject(tagsStr, HashMap.class);
        for (Map.Entry<String, JSONObject> entry : tagsHash.entrySet()) {
            String tagStr = JSONObject.toJSONString(entry.getValue());
            Tag tag = JSONObject.parseObject(tagStr, Tag.class);
            tag.setItem_id(item_id);
            try {
                tagRepository.save(tag);

            } catch (Exception e) {
                System.out.println("12312");
                throw e;
            }
        }
    }

    public void pullHistoryPrice() {
        //
        String buffHistoryUrl2 = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=180&buff_price_type=2&_=1684155597514&goods_id=";
        String buffHistoryUrl1 = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=180&buff_price_type=1&_=1684155597514&goods_id=";
        String steamHistoryUrl = "https://buff.163.com/api/market/goods/price_history?game=csgo&currency=CNY&days=30&buff_price_type=2&_=1684161693973&goods_id=";
        List<Long> itemIds = itemRepository.findAllId();
        itemIds = itemIds.subList(0, 1);
        long timeMillis = System.currentTimeMillis();
        for (Long itemId : itemIds) {
            long lastUpStamp = history2Repository.findlastUpByItemId(itemId);
            if (timeMillis < lastUpStamp + 604800){
                //7天内更新过
                continue;
            }
            ResponseEntity<HistoryPriceRep> responseEntity = restTemplate.exchange(buffHistoryUrl2 + itemId, HttpMethod.GET, getHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()){
                continue;
            }
            saveBuffHistory2(responseEntity,itemId);
            responseEntity = restTemplate.exchange(buffHistoryUrl1 + itemId, HttpMethod.GET, getHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()){
                continue;
            }
            saveBuffHistory1(responseEntity,itemId);
            responseEntity = restTemplate.exchange(steamHistoryUrl + itemId, HttpMethod.GET, getHttpEntity(), HistoryPriceRep.class);
            if (200 != responseEntity.getStatusCode().value()){
                continue;
            }
            saveSteamHistory(responseEntity,itemId);

            System.out.println("123123");
        }
    }

    public static void main(String[] args) {
        long a =  1684080000- 1683475200 ;
        System.out.println(a);
    }

    /**
     * buff在售最低
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBuffHistory2(ResponseEntity<HistoryPriceRep> responseEntity,long itemId){
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<BuffPriceHistory2> buffPriceHistory2List = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            BuffPriceHistory2 buffPriceHistory2 = new BuffPriceHistory2();
            buffPriceHistory2.setItem_id(itemId);
            buffPriceHistory2.setTime_stamp(Long.decode(price_history.get(0)));
            buffPriceHistory2.setPrice(Double.parseDouble(price_history.get(1)));
            buffPriceHistory2.setUp_time_stamp(upLastTime);
            buffPriceHistory2List.add(buffPriceHistory2);
        }
        history2Repository.saveAll(buffPriceHistory2List);
    }


    /**
     * buff成交记录
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBuffHistory1(ResponseEntity<HistoryPriceRep> responseEntity,long itemId){
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<BuffPriceHistory1> buffPriceHistory1List = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            BuffPriceHistory1 buffPriceHistory1 = new BuffPriceHistory1();
            buffPriceHistory1.setItem_id(itemId);
            buffPriceHistory1.setTime_stamp(Long.decode(price_history.get(0)));
            buffPriceHistory1.setPrice(Double.parseDouble(price_history.get(1)));
            buffPriceHistory1.setUp_time_stamp(upLastTime);
            buffPriceHistory1List.add(buffPriceHistory1);
        }
        history1Repository.saveAll(buffPriceHistory1List);
    }

    /**
     * buff成交记录
     * @param responseEntity
     * @param itemId
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSteamHistory(ResponseEntity<HistoryPriceRep> responseEntity,long itemId){
        HistoryPriceRep historyPriceRep = responseEntity.getBody();
        List<List<String>> price_historys = historyPriceRep.getData().getPrice_history();
        List<SteamPriceHistory> steamPriceHistories = new ArrayList<>();
        long upLastTime = System.currentTimeMillis();
        for (List<String> price_history : price_historys) {
            SteamPriceHistory steamPriceHistory = new SteamPriceHistory();
            steamPriceHistory.setItem_id(itemId);
            steamPriceHistory.setTime_stamp(Long.decode(price_history.get(0)));
            steamPriceHistory.setPrice(Double.parseDouble(price_history.get(1)));
            steamPriceHistory.setUp_time_stamp(upLastTime);
            steamPriceHistories.add(steamPriceHistory);
        }
        historyRepository.saveAll(steamPriceHistories);
    }

}
