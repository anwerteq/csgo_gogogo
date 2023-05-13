package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenerzhu
 * @create 2018-09-08 23:25
 * https://www.proxynova.com/proxy-server-list/
 **/
@Slf4j
public class ProxynovaCrawlerJob extends AbstractCrawler {
    public ProxynovaCrawlerJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    protected Map<String, String> headerMap = new HashMap<String, String>() {{
        put("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
        put("sec-ch-ua-mobile", "?0");
        put("sec-ch-ua-platform", "\"Windows\"");
        put("Upgrade-Insecure-Requests", "1");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        put("Sec-Fetch-Site", "same-origin");
        put("Sec-Fetch-Mode", "navigate");
        put("Sec-Fetch-User", "?1");
        put("Sec-Fetch-Dest", "document");
    }};




    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByTag("tbody")
                .get(0).getElementsByTag("tr");
        Element element;
        ProxyIp proxyIp;
        for (int i = 0; i < elements.size(); i++) {
            try {
                element = elements.get(i);
                proxyIp = new ProxyIp();
                String ip = getIp(element);
                if ("".equals(ip)) {
                    continue;
                }
                proxyIp.setIp(ip);
                proxyIp.setPort(Integer.parseInt(element.child(1).text()));
                proxyIp.setLocation(element.child(5).text());
                proxyIp.setCountry(element.child(5).text().split("-")[0]);
                proxyIp.setAnonymity(element.child(6).text());
                proxyIp.setType("unKnow");
                proxyIp.setAvailable(true);
                proxyIp.setCreateTime(new Date());
                proxyIp.setLastValidateTime(new Date());
                proxyIp.setValidateCount(0);
                proxyIpQueue.offer(proxyIp);
            } catch (Exception e) {
                log.error("proxynovaCrawlerJob error:{0}",e);
            }
        }
    }

    private String getIp(Element element) throws ScriptException {
        String ip = "";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Pattern pattern = Pattern.compile("\\(.*?\\);<");
        Matcher matcher = null;
        matcher = pattern.matcher(element.child(0).html());
        if (matcher.find()) {
            String ipScript = matcher.group(0).substring(1, matcher.group(0).length() - 1);
            ip = (String) engine.eval(ipScript.replaceAll("\\);", ""));
        }
        return ip;
    }

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

        ProxynovaCrawlerJob proxyListCrawlerJob =  new ProxynovaCrawlerJob(proxyIpQueue, "https://www.proxynova.com/proxy-server-list/");

        proxyListCrawlerJob.run();
    }

}
