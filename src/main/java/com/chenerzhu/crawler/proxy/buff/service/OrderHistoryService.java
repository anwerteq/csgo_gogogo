package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.pool.csgo.service.BuffCostService;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderHistoryService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BuffCostService buffCostService;


    public void pullOrderHistory(){
        int num = 0;
        while (num < 400){
            num = num +1;
            log.info("buff拉取，第{}页",num);
            pullOrderHistory(num);
        }
    }

    /**
     * 拉取buff的历史订单数据
     * @param num
     */
    public void pullOrderHistory(int num){
        String url = "https://buff.163.com/market/buy_order/history?game=csgo&page_num="+ num;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        String body = responseEntity.getBody();
        parseBod(body);
    }

    /**
     * 解析parsebody
     * @param body
     */
    public void parseBod(String body) {
        List<BuffCostEntity> arrayList = new ArrayList();
        Document document = Jsoup.parse(body);
        Elements list_tb_csgo = document.getElementsByClass("list_tb_csgo");
        Element element = list_tb_csgo.get(0);
        Elements tr = element.getElementsByTag("tr");
        for (Element elementTr : tr) {
            //解析出需要的对象
            BuffCostEntity buffCostEntity = new BuffCostEntity();
            Elements tds = elementTr.getElementsByTag("td");
            String statucText = tds.get(6).text();
            if (!"购买成功".equals(statucText)){
                continue;
            }
            String name = tds.get(2).getElementsByTag("h3").get(0).text();
            buffCostEntity.setName(name);
            //assId 和classId在第一个
            Element assidAndClassId = tds.get(1).child(0);
            String assetid =assidAndClassId.attr("data-assetid");
            buffCostEntity.setAssetid(Long.valueOf(assetid));
            String classid =assidAndClassId.attr("data-classid");
            buffCostEntity.setClassid(Long.valueOf(classid));
            String instanceid = assidAndClassId.attr("data-instanceid");
            Element element1 = tds.get(3);
            Element child = element1.child(0);
            String priceRmb = child.text().replace("¥ ","");
            buffCostEntity.setBuff_cost(Double.valueOf(priceRmb));
            arrayList.add(buffCostEntity);
        }
        SleepUtil.sleep(1500);
        buffCostService.byOrderHistorySave(arrayList);
    }
}
