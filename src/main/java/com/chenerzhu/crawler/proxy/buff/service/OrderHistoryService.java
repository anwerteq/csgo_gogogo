package com.chenerzhu.crawler.proxy.buff.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderHistoryService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BuffCostService buffCostService;

    @Autowired
    BuffCostRepository buffCostRepository;


    /**
     * 拉取buff的历史订单数据
     *
     * @param
     */
    public void pullOrderHistory() {
        List<BuffCostEntity> allByMobileNumber = buffCostRepository.findAllByMobileNumberOrderByNumberAsc(BuffApplicationRunner.buffUserDataThreadLocal.get().getAcount());
        Set<Integer> numbers = allByMobileNumber.stream().map(BuffCostEntity::getNumber).collect(Collectors.toSet());
        int pageCount = getPageCount();
        //pageCount没有数据
        List<BuffCostEntity> onebuffCostEntities = pullOnePageHistory(pageCount);
        if (CollectionUtil.isEmpty(onebuffCostEntities)) {
            pageCount--;
        }
        //pageCount 页不满10条
        List<BuffCostEntity> twoBuffCostEntitiesLadt = pullOnePageHistory(pageCount);
        int count = 0;
        Collections.reverse(twoBuffCostEntitiesLadt);
        for (BuffCostEntity buffCostEntity : twoBuffCostEntitiesLadt) {
            buffCostEntity.setNumber(++count);
            buffCostEntity.refreashCdkey_id();
        }
        buffCostRepository.saveAll(twoBuffCostEntitiesLadt);
        pageCount--;
        //
        for (int i = 1; i <= pageCount * 10; i++) {
            if (!numbers.contains(count + i)) {
                List<BuffCostEntity> threeBuffCostEntities = pullOnePageHistory(pageCount);
                Collections.reverse(threeBuffCostEntities);
                int count_i = 0;
                for (BuffCostEntity buffCostEntity : threeBuffCostEntities) {
                    buffCostEntity.setNumber(count + i + (count_i++));
                    buffCostEntity.refreashCdkey_id();
                }
                buffCostRepository.saveAll(threeBuffCostEntities);
                pageCount--;
                i = (i / 10) * 10 + 10;
            }
        }
    }

    /**
     * 获取buff 历史交易的页数
     *
     * @return
     */
    public int getPageCount() {
        //最大页数
        int top_count = 100 * 10;
        int temp_count = top_count / 2;
        int down_count = 0;

        //二分法，算出有数据的最后一页
        while (true) {
            List<BuffCostEntity> buffCostEntities = pullOnePageHistory(temp_count);
            //最后一页，不足10个
            if (buffCostEntities.size() != 0 && buffCostEntities.size() < 10) {
                break;
            }
            //是空集合
            if (CollectionUtil.isEmpty(buffCostEntities)) {
                //往下
                top_count = temp_count;
                temp_count = (temp_count - down_count) / 2 + down_count;
            } else {
                //往上
                down_count = temp_count;
                temp_count = (top_count - temp_count) / 2 + down_count;
            }
            //最后一页是十个则退出
            if (top_count == temp_count || down_count == temp_count) {
                break;
            }
        }
        return temp_count;
    }

    /**
     * 拉取buff的历史订单数据
     *
     * @param num
     */
    public List<BuffCostEntity> pullOnePageHistory(int num) {
        log.info("buff拉取，第{}页", num);
        ThreadUtil.sleep(10 * 1000);
        String url = "https://buff.163.com/market/buy_order/history?game=csgo&page_num=" + num;
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        String body = responseEntity.getBody();
        List<BuffCostEntity> buffCostEntities = parseBod(body);
        return buffCostEntities;
    }

    /**
     * 解析parsebody
     *
     * @param body
     */
    public List<BuffCostEntity> parseBod(String body) {
        List<BuffCostEntity> arrayList = new ArrayList();
        Document document = Jsoup.parse(body);
        Elements list_tb_csgo = document.getElementsByClass("list_tb_csgo");
        Element element = list_tb_csgo.get(0);
        Elements tr = element.getElementsByTag("tr");
        String nodata = tr.get(0).getElementsByClass("nodata").text();
        if (nodata.length() > 3) {
            log.info("拉取buff历史交易记录返回的值为：" + nodata);
            return new ArrayList<>();
        }
        for (Element elementTr : tr) {

            //解析出需要的对象
            BuffCostEntity buffCostEntity = new BuffCostEntity();
            //订单id
            String id = elementTr.id();
            buffCostEntity.setId(id);
            //购买标题
            Elements tds = elementTr.getElementsByTag("td");
            String statucText = tds.text();
            buffCostEntity.setStatucText(statucText);
            //饰品名称
            String name = tds.get(2).getElementsByTag("h3").get(0).text();
            buffCostEntity.setName(name);

            //assId 和classId在第一个
            Element assidAndClassId = tds.get(1).child(0);
            String assetid = assidAndClassId.attr("data-assetid");
            buffCostEntity.setAssetid(assetid);
            String classid = assidAndClassId.attr("data-classid");
            buffCostEntity.setClassid(classid);
            String instanceid = assidAndClassId.attr("data-instanceid");
            buffCostEntity.setInstanceid(instanceid);

            buffCostEntity.setMobileNumber(BuffApplicationRunner.buffUserDataThreadLocal.get().getAcount());
            buffCostEntity.refreashCdkey_id();
            Element element1 = tds.get(3);
            Element child = element1.child(0);
            String priceRmb = child.text().replace("¥ ", "");
            //购买价格
            buffCostEntity.setBuff_cost(Double.valueOf(priceRmb));
            arrayList.add(buffCostEntity);
        }
        log.info("拉取buff历史交易记录条数为为：" + arrayList.size());
        return arrayList;
    }
}
