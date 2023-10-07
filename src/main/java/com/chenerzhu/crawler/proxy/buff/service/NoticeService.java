package com.chenerzhu.crawler.proxy.buff.service;

import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.JsonsRootBean;
import com.chenerzhu.crawler.proxy.buff.service.buffnotice.ToDeliverOrder;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.Data;
import com.chenerzhu.crawler.proxy.buff.service.deliverOrder.JsonsRootBeanDeliver;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * buff的相关信息
 */

@Service
@Slf4j
public class NoticeService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 获取buff的相关通知信息
     */
    public JsonsRootBean steamTrade() {
        String cookie = "Device-Id=wAzsoRK1TlGO7PRdSbtl; Locale-Supported=zh-Hans; _ntes_nnid=5d589fbd49dd48a8cee4d25e469a18b4,1683952089901; _ntes_nuid=5d589fbd49dd48a8cee4d25e469a18b4; _ga_C6TGHFPQ1H=GS1.1.1684643501.1.0.1684643501.0.0.0; _ga=GA1.1.1530874508.1684643501; Qs_lvt_382223=1684643501; Qs_pv_382223=3466784944872162000; _clck=1306jcq|2|fbs|0|1236; NTES_CMT_USER_INFO=536393132%7C%E6%9C%89%E6%80%81%E5%BA%A6%E7%BD%91%E5%8F%8B0v_bmI%7Chttp%3A%2F%2Fcms-bucket.nosdn.127.net%2F2018%2F08%2F13%2F078ea9f65d954410b62a52ac773875a1.jpeg%7Cfalse%7CeWQuNzZlNjQwMjdhMTE5NGM1YThAMTYzLmNvbQ%3D%3D; to_steam_processing_click230609T19053143101=1; r_ntcid=730:163; game=csgo; NTES_P_UTID=lYkxckkwdcNOQJBiS8SbnfOIXGWFrSgk|1693619292; nts_mail_user=undefined:-1:0; to_steam_processing_click230902T19585480081=1; unbind_steam_result=; ANTICSRF=f6671204e4d021e7d1e43662504461a4; NTES_YD_SESS=yHi2Z5t_tO_mLeiOlr_rz6Uydj_kxgqDSxKnjGcajp8M3iTX3xgZlsx.MJwNbn40UjJXSeSoJahCxa1lweIkk3OYNXb1J42b6HJy93qvR9OaO7hr.jyZsCv0y8iNnSU6UmMyy7ccNzagsynrx_vU9dn4ElIeVT8nhg8ZGuEQvuk8nvNQih4MSNa73ZuaRoJGeo.q_.APDPUoXyGh_URGDl815pWtLxXcUUNQKrVPOdVm9; S_INFO=1696686200|0|0&60##|15347971344; P_INFO=15347971344|1696686200|1|netease_buff|00&99|hongkong&1696536037&netease_buff#shh&null#10#0#0|&0||15347971344; remember_me=U1103739664|a3vCaKbKanK57816hxJhVS3ix85qAPGx; session=1-1safle-MTicAwTwipxnDddBCGRm8ku_y-4km7zqQlCDw2030511176; client_id=Id6FrMODhUzpEEr3tJbxgg; csrf_token=IjA3YTI2ZmJiZGY3ZTRlYTZhMGNiZjQwZGI4ZDk1YmQ3YmU4YWZmNTQi.GAL4Ew.PIuHJ5rlLw298bt2Qpve3rUgXpE";
        String url = "https://buff.163.com/api/message/notification";
        CookiesConfig.buffCookies.set(cookie);
        ResponseEntity<JsonsRootBean> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), JsonsRootBean.class);
        JsonsRootBean jsonsRootBean = responseEntity.getBody();
        if (!"OK".equals(jsonsRootBean.getCode())) {
            log.error("获取网易buff通知信息失败");
        }

        int csgoDeliverOrderCount = getCsgoDeliverOrderCount(jsonsRootBean);
        if (0 != csgoDeliverOrderCount) {
            List<String> deliverOrderTradeofferid = getDeliverOrderTradeofferid();
            System.out.println("123123");
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
    public List<String> getDeliverOrderTradeofferid() {
        JsonsRootBeanDeliver deliverOrder = getDeliverOrder();
        List<Data> data = deliverOrder.getData();
        List<String> tradeofferids = data.stream().map(Data::getTradeofferid).collect(Collectors.toList());
        return tradeofferids;
    }
}
