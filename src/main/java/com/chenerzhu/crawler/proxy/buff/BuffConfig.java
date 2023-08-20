package com.chenerzhu.crawler.proxy.buff;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.entity.Cookeis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * buff配置类
 */
@Configuration
public class BuffConfig {


    @Value("buff_session")
   public static String buffCookie = "_ntes_nnid=d9c42eaaee06546264904dce6ec2e618,1666248870670; _ntes_nuid=d9c42eaaee06546264904dce6ec2e618; __bid_n=184ddc62be224e2b824207; FPTOKEN=uy78EK2vFv2hziG1KX096cYrjbZuafzi6bDDytRNfnFArd5i+wqpdIOtJfhh+jjcQLpEEgLiwEmaxCeuVoqPZmN30wQfin89xYCpI6Bzj+G6ksg+CEWonmX1HPWt2H1eefaXTOBeX4MZ72DuWgYRFqEnuV3Gn2yrAuSZrRUJEabMTCm+VpWXuaV1Wgy25HYsssOW83ZvyijT0zKOv0H9ogQMOUU9KgFnaszby+LD+5oVFtCue4AFFIEPAPyPAaX0Z5FG5rLZJFR2DTuEJ265U4omGkx0I/FCH9hgDt48yrCx4RqpTZGMn7Fa3lavStNpMg1Jqzx4CLHHJxhrGhnSSGBdpTwBRND6dXeyBmNxLsk6quJqYDVyDIJUcaenhWrWc2Qb5gcovmFeRmez/9zlyQ==|avZCqYu3/fhWFXjSqhEypRvMOncZqeOLUIyHDUos96g=|10|33d785e611a48a89009352dd7deb5a6c; timing_user_id=time_7Ix5JzrCca; Device-Id=eNsnpKlEE3KrEa39TVdI; hb_MA-BFF5-63705950A31C_source=www.toolchest.cn; Locale-Supported=zh-Hans; game=csgo; NTES_YD_SESS=DwGdCZQslIqXc_BUzYRZMDwpglEqY_2x_Sa4Mwvk33OlybvYyrnuZzIQS6gHcVK8TAuInk.aRGSheLXkBsSAuXky_BIOJVm6OOMsd79v.45mlcx.mYTa_vSichTRITRN5DnYpqg13Zp2pbKLry871RErKzXUe3jO0.Ghrnudedj1qvL47s79oGSS.GAjCGI6x4P0ufr2fkdoThzy5OmvdtdYRHF_D9OsA0_4_G17UM20c; S_INFO=1684997545|0|0&60##|15989173318; P_INFO=15989173318|1684997545|1|netease_buff|00&99|null&null&null#shh&null#10#0|&0|null|15989173318; remember_me=U1103827335|PnqjzOe33iWb1qUdDK4WSwDYtGtUxq4L; session=1-Ufp9uuj_-cXLTtOqwLgfo3CrM8X1DWcpmI52_RQv5w-F2030407391; csrf_token=IjM1NTQ2NjNjNGU3MTJmMGY3ZmRiZDE4MmNlN2FlMmU2ZDU5MzljOTEi.F1CYnQ.XQwUvX2soKZJ14IOz6O0R4GkO5Y";


    public  static Map<String, String> map = new HashMap() {
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
        }
    };


    public static HttpEntity<MultiValueMap<String, String>> getBuffCreateBillHttpEntity() {
        HttpHeaders headers1 =getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        return entity1;
    }


    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity() {
        HttpHeaders headers1 = getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        syncCookie();
        return entity1;
    }

    /**
     * 获取请求头参数
     * @return
     */
    public static HttpHeaders getHeaderMap(){
        HttpHeaders headers1 = new HttpHeaders();
        Map<String, String> headerMap = map;
        headerMap.put("Cookie",getCookie());
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        return headers1;
    }

    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity(Map<String, Object> whereMap) {
        HttpHeaders headers1 = getHeaderMap();
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(whereMap, headers1);
        syncCookie();
        return entity1;
    }

    /**
     * 控制整个项目使用cookie的频率
     */
    public static void syncCookie() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getCookie(){
        if(true){
            return buffCookie;
        }
        String cookie = CookiesConfig.buffCookies.get();
        if (StrUtil.isNotEmpty(cookie)){
            return cookie;
        }
        long millis = System.currentTimeMillis();
        int size = CookiesConfig.cookeisList.size();
        //线程没有绑定cooke,随机获取一个cookie
        int index = (int) (millis % size);
        Cookeis cookeis = CookiesConfig.cookeisList.get(index);
        CookiesConfig.buffCookies.set(cookeis.getBuff_cookie());
        return cookeis.getBuff_cookie();
    }

    /**
     * 获取cookie中的value属性
     * @param key
     * @return
     */
    public static  String getCookieOnlyKey(String key){
        String[] split = getCookie().split(key + "=");
        String value = split[1].split(";")[0];
        return value.trim();
    }
}
