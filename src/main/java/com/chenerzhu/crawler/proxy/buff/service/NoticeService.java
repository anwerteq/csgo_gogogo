package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.applicationRunners.BuffApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.JsonsRootBean;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.ToDeliverOrder;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.Data;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.ItemsToTrade;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.JsonsRootBeanDeliver;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.service.SteamTradeofferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * buff的相关信息
 */

@Service
@Slf4j
public class NoticeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SteamTradeofferService steamTradeofferService;


    /**
     * 获取buff的相关通知信息
     */
    public JsonsRootBean steamTrade() {
        for (BuffUserData buffUserData : BuffApplicationRunner.buffUserDataList) {
            BuffApplicationRunner.buffUserDataThreadLocal.set(buffUserData);
        }
        BuffUserData buffUserData = BuffApplicationRunner.buffUserDataThreadLocal.get();
        String url = "https://buff.163.com/api/message/notification";
        CookiesConfig.buffCookies.set(buffUserData.getCookie());
        ResponseEntity<JsonsRootBean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), JsonsRootBean.class);
        JsonsRootBean jsonsRootBean = responseEntity.getBody();
        if (!"OK".equals(jsonsRootBean.getCode())) {
            log.error("获取网易buff通知信息失败");
        }

        int csgoDeliverOrderCount = getCsgoDeliverOrderCount(jsonsRootBean);
        if (0 != csgoDeliverOrderCount) {
            //key：交易订单，value:商品信息
            Map<String, List<ItemsToTrade>> orderTradeofferid = getDeliverOrderTradeofferid();
            steamTradeofferService.trader(buffUserData.getSteamId(), orderTradeofferid);

        }
        log.info("确认收货完成和上架完成");
        return jsonsRootBean;
    }

    /**
     * 获取待处理的csgo订单
     *
     * @return
     */
    public int getCsgoDeliverOrderCount(JsonsRootBean jsonsRootBean) {
        ToDeliverOrder toDeliverOrder = jsonsRootBean.getData().getToDeliverOrder();
        log.info("buff待处理的csgo订单数量为：{}", toDeliverOrder.getCsgo());
        return toDeliverOrder.getCsgo();
    }


    /**
     * 获取buff待处理订单信息
     *
     * @return
     */
    private JsonsRootBeanDeliver getDeliverOrder() {
        String url = "https://buff.163.com/api/market/steam_trade";
        ResponseEntity<JsonsRootBeanDeliver> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), JsonsRootBeanDeliver.class);
        JsonsRootBeanDeliver body = responseEntity.getBody();
        return body;
    }

    /**
     * 获取steam待确认的交易订单id集合
     *
     * @return
     */
    public Map<String, List<ItemsToTrade>> getDeliverOrderTradeofferid() {
        JsonsRootBeanDeliver deliverOrder = getDeliverOrder();
        List<Data> datas = deliverOrder.getData();
        Map<String, List<ItemsToTrade>> tradeofferidMap = datas.stream().collect(Collectors.toMap(Data::getTradeofferid, Data::getItemsToTrade));
        return tradeofferidMap;
    }
}
