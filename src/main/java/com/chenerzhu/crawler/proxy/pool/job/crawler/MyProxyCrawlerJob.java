package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import com.chenerzhu.crawler.proxy.pool.job.crawler.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-08 16:35
 * https://www.my-proxy.com/free-proxy-list.html
 **/
@Slf4j
public class MyProxyCrawlerJob extends AbstractCrawler {

    protected Map<String, String> headerMap = new HashMap<String, String>() {{

        put("sec-ch-ua","\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
        put("sec-ch-ua-mobile","?0");
        put("sec-ch-ua-platform","\"Windows\"");
        put("Upgrade-Insecure-Requests","1");
        put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        put("Sec-Fetch-Site","same-origin");
        put("Sec-Fetch-Mode","navigate");
        put("Sec-Fetch-User","?1");
        put("Sec-Fetch-Dest","document");


    }};

    public MyProxyCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    @Override
    public void parsePage(WebPage webPage) {
        String[] elements = webPage.getDocument().getElementsByClass("list")
                .html().split("<br>");
        ProxyIp proxyIp;
        String element;
        for (int i = 0; i < 43; i++) {
            try {
                //185.120.37.186:55143#AL
                element = elements[i];
                String ipPort = element.split("#")[0];
                String ip = ipPort.split(":")[0];
                String port = ipPort.split(":")[1];
                String country = element.split("#")[1];
                proxyIp = new ProxyIp();
                proxyIp.setIp(ip);
                proxyIp.setPort(Integer.parseInt(port));
                proxyIp.setType("http");
                proxyIp.setCountry(country);
                proxyIp.setLocation(country);
                proxyIp.setCreateTime(new Date());
                proxyIp.setAvailable(true);
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("myProxyCrawlerJob error:{0}",e);
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

        MyProxyCrawlerJob myProxyCrawlerJob = new MyProxyCrawlerJob(proxyIpQueue, "https://www.my-proxy.com/free-proxy-list.html");

        myProxyCrawlerJob.run();
    }
}
