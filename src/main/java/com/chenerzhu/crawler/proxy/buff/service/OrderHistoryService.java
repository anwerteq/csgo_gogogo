package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.thread.ThreadUtil;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.csgo.entity.BuffCostEntity;
import com.chenerzhu.crawler.proxy.csgo.repository.BuffCostRepository;
import com.chenerzhu.crawler.proxy.csgo.service.BuffCostService;
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

    @Autowired
    BuffCostRepository buffCostRepository;


    public void pullOrderHistory(){
        int num = 1;
        while ( pullOrderHistory(num++));
    }

    /**
     * 拉取buff的历史订单数据
     * @param num
     */
    public Boolean pullOrderHistory(int num){
        log.info("buff拉取，第{}页",num);
        ThreadUtil.sleep(10 * 1000);
        String url = "https://buff.163.com/market/buy_order/history?game=csgo&state=success&page_num="+ num;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        String body = responseEntity.getBody();
        return parseBod(body);
    }

    /**
     * 解析parsebody
     * @param body
     */
    public Boolean parseBod(String body) {
        List<BuffCostEntity> arrayList = new ArrayList();
        Document document = Jsoup.parse(body);
        Elements list_tb_csgo = document.getElementsByClass("list_tb_csgo");
        Element element = list_tb_csgo.get(0);
        Elements tr = element.getElementsByTag("tr");
        String nodata = tr.get(0).getElementsByClass("nodata").text();
        if (nodata.length()< 10){
            return false;
        }
        for (Element elementTr : tr) {
            //解析出需要的对象
            BuffCostEntity buffCostEntity = new BuffCostEntity();
            Elements tds = elementTr.getElementsByTag("td");
            String statucText = tds.get(6).text();
            if ("购买成功".equals(statucText) || statucText.contains("求购成功")){
                String name = tds.get(2).getElementsByTag("h3").get(0).text();
                buffCostEntity.setName(name);
                //assId 和classId在第一个
                Element assidAndClassId = tds.get(1).child(0);
                String assetid =assidAndClassId.attr("data-assetid");
                buffCostEntity.setAssetid(assetid);
                String classid =assidAndClassId.attr("data-classid");
                buffCostEntity.setClassid(classid);
                String instanceid = assidAndClassId.attr("data-instanceid");
                buffCostEntity.setInstanceid(instanceid);
                buffCostEntity.refreashCdkey_id();
                Element element1 = tds.get(3);
                Element child = element1.child(0);
                String priceRmb = child.text().replace("¥ ","");
                buffCostEntity.setBuff_cost(Double.valueOf(priceRmb));
                arrayList.add(buffCostEntity);
            }
        }
        if (arrayList.size() > 0){
            buffCostRepository.saveAll(arrayList);
        }
        return true;
    }
}
