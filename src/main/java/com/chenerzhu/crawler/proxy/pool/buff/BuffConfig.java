package com.chenerzhu.crawler.proxy.pool.buff;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * buff配置类
 */
public class BuffConfig {

   public static String buffCookie = "_ntes_nnid=d9c42eaaee06546264904dce6ec2e618,1666248870670; _ntes_nuid=d9c42eaaee06546264904dce6ec2e618; __bid_n=184ddc62be224e2b824207; FPTOKEN=uy78EK2vFv2hziG1KX096cYrjbZuafzi6bDDytRNfnFArd5i+wqpdIOtJfhh+jjcQLpEEgLiwEmaxCeuVoqPZmN30wQfin89xYCpI6Bzj+G6ksg+CEWonmX1HPWt2H1eefaXTOBeX4MZ72DuWgYRFqEnuV3Gn2yrAuSZrRUJEabMTCm+VpWXuaV1Wgy25HYsssOW83ZvyijT0zKOv0H9ogQMOUU9KgFnaszby+LD+5oVFtCue4AFFIEPAPyPAaX0Z5FG5rLZJFR2DTuEJ265U4omGkx0I/FCH9hgDt48yrCx4RqpTZGMn7Fa3lavStNpMg1Jqzx4CLHHJxhrGhnSSGBdpTwBRND6dXeyBmNxLsk6quJqYDVyDIJUcaenhWrWc2Qb5gcovmFeRmez/9zlyQ==|avZCqYu3/fhWFXjSqhEypRvMOncZqeOLUIyHDUos96g=|10|33d785e611a48a89009352dd7deb5a6c; timing_user_id=time_7Ix5JzrCca; P_INFO=15989173318|1684131704|1|netease_buff|00&99|null&null&null#shh&null#10#0|&0||15989173318; Device-Id=eNsnpKlEE3KrEa39TVdI; hb_MA-BFF5-63705950A31C_source=www.toolchest.cn; Locale-Supported=zh-Hans; game=csgo; session=1-iuN--N6z1CGWt8vr26-X0yPx8upbF1K8ukskO5R4N7ms2030407391; csrf_token=ImJkYTU3OWFlY2MzMGEzMjQwOThkNmNhNjliNTIzOTgxZWRkMjAyZDYi.F04ClQ.SPsHNU0P-FOTEkamaYdqu04HXIk";

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
//            put("Cookie", "_ntes_nnid=d9c42eaaee06546264904dce6ec2e618,1666248870670; _ntes_nuid=d9c42eaaee06546264904dce6ec2e618; __bid_n=184ddc62be224e2b824207; FPTOKEN=uy78EK2vFv2hziG1KX096cYrjbZuafzi6bDDytRNfnFArd5i+wqpdIOtJfhh+jjcQLpEEgLiwEmaxCeuVoqPZmN30wQfin89xYCpI6Bzj+G6ksg+CEWonmX1HPWt2H1eefaXTOBeX4MZ72DuWgYRFqEnuV3Gn2yrAuSZrRUJEabMTCm+VpWXuaV1Wgy25HYsssOW83ZvyijT0zKOv0H9ogQMOUU9KgFnaszby+LD+5oVFtCue4AFFIEPAPyPAaX0Z5FG5rLZJFR2DTuEJ265U4omGkx0I/FCH9hgDt48yrCx4RqpTZGMn7Fa3lavStNpMg1Jqzx4CLHHJxhrGhnSSGBdpTwBRND6dXeyBmNxLsk6quJqYDVyDIJUcaenhWrWc2Qb5gcovmFeRmez/9zlyQ==|avZCqYu3/fhWFXjSqhEypRvMOncZqeOLUIyHDUos96g=|10|33d785e611a48a89009352dd7deb5a6c; Device-Id=dClYQRNmcSAs5uUWiiRJ; timing_user_id=time_7Ix5JzrCca; Locale-Supported=zh-Hans; game=csgo; steam_info_to_bind=; NTES_YD_SESS=Y3W6DesW535sbxMzmdG2pLTLLFy94Il_wxLdVH9OAA56Xu9MX1ahwnlrxFKNykvj_2hlaO8LePxSbcWO.gx2hWOXC.l5tkQF55VgiR398doQ6y78QM_LC9xZyS_el_epoYaM4IKEAw4s4uvc1XjREef1vnWTbAU5GXHgBFWdtq1GuANXexwo8HqbBcDQuhrz13sMxHDCrVhaJr4lqNUcYciMeN0CY35g2GCdCPERTVsGy; S_INFO=1684131704|0|0&60##|15989173318; P_INFO=15989173318|1684131704|1|netease_buff|00&99|null&null&null#shh&null#10#0|&0||15989173318; remember_me=U1103827335|VekMfEHVSUzj0WydiCEOm3670Luyu4Mb; session=1-xqoGj7ecAA5hzi3EvNHvrgmvZwgpSJvkjRhUGM99HTNk2030407391; csrf_token=IjUwNmMzMDUyMjgwZjhlODI5OTJmZmMwOWVjZTQ2MGY3NDk4YzYzYTIi.F0NhDg.8KKnlpj1tnyjfTdgJ_jyM3OX_Q8");
//            put("Cookie", "_clck=1306jcq|2|fbs|0|1236; nie_video_speed=257.6; _clsk=17ot5x9|1684643502912|1|1|t.clarity.ms/collect; csrf_token=ImEwZTUyMzM2NjMwNjNmYWJjNzg2MzI4OTNkOWRhOTFmNDhmY2ZlOTEi.F0tbvg.ifwHA3oD5JqXAqGNOlCCqDqXCbQ;Device-Id=wAzsoRK1TlGO7PRdSbtl;Locale-Supported=zh-Hans; game=csgo; session=1-WfiWDKomdnq5YqFXQ5OaffzeQqM6mSHuM4XyYLGNmZXn2030407391");
            put("Cookie", buffCookie);
        }
    };


    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity() {
        HttpHeaders headers1 = new HttpHeaders();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers1);
        syncCookie();
        return entity1;
    }

    public static HttpEntity<MultiValueMap<String, String>> getBuffHttpEntity(Map<String, Object> whereMap) {
        HttpHeaders headers1 = new HttpHeaders();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            headers1.set(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(whereMap, headers1);
        syncCookie();
        return entity1;
    }

    /**
     * 控制整个项目使用cookie的频率
     */
    public static void syncCookie() {
        synchronized ("12345678909876") {
            try {
                double random = Math.random() * 2000;
                int shleepTime = (int) (random) + 1100;
                Thread.sleep(shleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
