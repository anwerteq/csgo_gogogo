package com.chenerzhu.crawler.proxy.steam.service;

import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.applicationRunners.SteamApplicationRunner;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.ItemsToTrade;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.SteamConfig;
import com.chenerzhu.crawler.proxy.steam.service.steamtrade.*;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.SteamTheadeUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.ConfirmUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamLoginUtil;
import com.chenerzhu.crawler.proxy.util.steamlogin.SteamUserDate;
import com.chenerzhu.crawler.proxy.util.steamlogin.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * steam确认收货
 */
@Service
@Slf4j
public class SteamTradeofferService {


    /**
     * @param steamId:使用哪个steamId
     * @param orderTradeofferidMap:订单信息
     */
    public void trader(String steamId, Map<String, List<ItemsToTrade>> orderTradeofferidMap) {
        boolean flag = SteamApplicationRunner.setThreadLocalSteamId(steamId);
        if (!flag) {
            log.info("buff账号{}，未加载对应steam账号");
            return;
        }
        //可确认id集合
        Set<String> confirmTradeSet = new HashSet<>();
        for (Map.Entry<String, List<ItemsToTrade>> entry : orderTradeofferidMap.entrySet()) {
            //一个次buff交易的标识集合
            Set<String> buffOnlyKeys = entry.getValue().stream().map(ItemsToTrade::getOnlyKey).collect(Collectors.toSet());
            //获取steam的订单信息
            SteamTradeOfferData tradeOffer = getTradeOffer(entry.getKey());
            Offer offer = tradeOffer.getResponse().getOffer();
            if (2 != offer.getTradeOfferState()) {
                //不是活跃状态
                continue;
            }
            //一次steam交易的标识集合
            Set<String> steamOnlyKeys = offer.getItemsToGive().stream().map(ItemsToGive::getOnlyKey).collect(Collectors.toSet());
            buffOnlyKeys.removeAll(steamOnlyKeys);
            if (buffOnlyKeys.isEmpty()) {
                //校验通过，交易没有被修改,进行steam确认操作
                String partner = getPartner(entry.getKey());
                //接受报价
                TradeofferAcceptData tradeofferAcceptData = doTradeofferAccept(partner, entry.getKey());
                confirmTradeSet.add(entry.getKey());
            }
        }

        List<Conf> confs = fetchConfirmationsPage();
        for (Conf conf : confs) {
            //获取一个确认清单的明细数据
            String tradeId = fetchConfirmationDetailsPage(conf);
            if (confirmTradeSet.contains(tradeId)) {
                sendConfirmation(conf);
            }
        }

    }


    /**
     * 确认操作
     *
     * @param conf
     */
    private void sendConfirmation(Conf conf) {
        SleepUtil.sleep(6000);
        String tag = "allow";
        Map<String, String> confirmationParams = createConfirmationParams(tag);
        confirmationParams.put("op", tag);
        confirmationParams.put("cid", conf.getId());
        confirmationParams.put("ck", conf.getNonce());
        Map<String, String> steamHeader = SteamConfig.getSteamHeader();
        steamHeader.put("X-Requested-With", "XMLHttpRequest");
        String url = "https://steamcommunity.com/mobileconf/ajaxop";
        String resStr = HttpClientUtils.sendGet(url, steamHeader, confirmationParams);
        Map map = JSONObject.parseObject(resStr, Map.class);
        log.info("确认操作，steam返回的数据为：{}", resStr);

    }


    /**
     * 获取交易订单信息
     *
     * @param tradeId
     */
    public SteamTradeOfferData getTradeOffer(String tradeId) {
        SleepUtil.sleep(5000);
        SteamUserDate steamUserDate = SteamTheadeUtil.steamUserDateTL.get();
        CookiesConfig.steamCookies.set(steamUserDate.getCookies().toString());
        String accessToken = steamUserDate.getSession().getAccessToken();
        String cookies = CookiesConfig.steamCookies.get();
        String apikey = steamUserDate.getApikey();
        String tradeofferid = tradeId;
        String url = "https://api.steampowered.com/IEconService/GetTradeOffer/v1?access_token=" + accessToken + "&tradeofferid=" + tradeofferid + "&language=english";
        Map<String, String> headers = new HashMap() {{
            put("Cookie", cookies);
        }};
        String resStr = HttpClientUtils.sendGet(url, headers);
        SteamTradeOfferData steamTradeOfferData = JSONObject.parseObject(resStr, SteamTradeOfferData.class);
        return steamTradeOfferData;
    }


    /**
     * 获取steam交易用户的id
     * * @param tradeIds
     */
    public String getPartner(String tradeId) {
        String url = "https://steamcommunity.com/tradeoffer/" + tradeId;
        String resStr = HttpClientUtils.sendGet(url, SteamConfig.getSteamHeader());
        String[] split = resStr.split("var g_ulTradePartnerSteamID = '");
        String[] split1 = split[1].split("';");
        return split1[0];
    }

    /**
     * steam接收订单操作
     */
    public void steamaccept(Set<String> tradeIds) {

        tradeIds.stream().forEach(tradeId -> {
            String partner = getPartner(tradeId);
            SleepUtil.sleep(2000);
            doTradeofferAccept(partner, tradeId);
        });
    }

    /**
     * 开始发送steam确认请求
     *
     * @param tradeofferid
     * @param partner
     */
    public TradeofferAcceptData doTradeofferAccept(String partner, String tradeofferid) {
        SleepUtil.sleep(3000);
        String url = "https://steamcommunity.com/tradeoffer/" + tradeofferid + "/accept";
        Map<String, String> paramerMap = new HashMap<>();
        Map<String, String> saleHeader = SteamConfig.getSaleHeader();
        saleHeader.put("Referer", "https://steamcommunity.com/tradeoffer/" + tradeofferid);
        paramerMap.put("sessionid", SteamConfig.getCookieOnlyKey("sessionid"));
        paramerMap.put("serverid", "1");
        paramerMap.put("captcha", "");
        paramerMap.put("tradeofferid", tradeofferid);
        paramerMap.put("partner", partner);
        String responseStr = HttpClientUtils.sendPostForm(url, "", saleHeader, paramerMap);
        TradeofferAcceptData acceptData = JSONObject.parseObject(responseStr, TradeofferAcceptData.class);
        log.info("确认收货{}：,接口返回的参数{}", partner, responseStr);
        return acceptData;
    }


    public void getConfirmations() {

    }


    /**
     * 获取steam待确认清单
     */
    public List<Conf> fetchConfirmationsPage() {
        String tag = "conf";
        Map<String, String> confirmationParams = createConfirmationParams(tag);
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        saleHeader.put("X-Requested-With", "com.valvesoftware.android.steam.community");
        String url = "https://steamcommunity.com/mobileconf/getlist";
        String responseStr = HttpClientUtils.sendGet(url, saleHeader, confirmationParams);
        GetlistResponse getlistResponse = JSONObject.parseObject(responseStr, GetlistResponse.class);

        return getlistResponse.getConf();
    }


    /**
     * 获取steam的订单
     *
     * @param conf
     * @return
     */
    public String fetchConfirmationDetailsPage(Conf conf) {
        String tag = "details" + conf.getId();
        Map<String, String> confirmationParams = createConfirmationParams(tag);
        String url = "https://steamcommunity.com/mobileconf/details/" + conf.getId();
        Map<String, String> saleHeader = SteamConfig.getSteamHeader();
        String responseStr = HttpClientUtils.sendGet(url, saleHeader, confirmationParams);
        DetailsConfData detailsConfData = JSONObject.parseObject(responseStr, DetailsConfData.class);
        String eleId = detailsConfData.getHtml().split("<div class=\"tradeoffer\" id=\"")[1].split("\"")[0];
        String id = eleId.split("_")[1];
//        detailsConfData.getHtml().split("")

        return id;
    }


    /**
     * 创建确认操作参数  1696758679000
     */
    private Map<String, String> createConfirmationParams(String tag) {
        SteamUserDate steamUserDate = SteamTheadeUtil.steamUserDateTL.get();
        long timestamp = TimeUtil.getTimeStamp();
        String confirmation_key = ConfirmUtil.getKey(steamUserDate.getIdentity_secret(), tag, timestamp);
        String steamId = steamUserDate.getSession().getSteamID();
        Map<String, String> map = new HashMap<>();
        map.put("p", ConfirmUtil.getDeviceID(steamId));
        map.put("a", steamId);
        map.put("k", confirmation_key);
        map.put("t", String.valueOf(timestamp));
        map.put("m", "android");
        map.put("tag", tag);
        return map;
    }


}
