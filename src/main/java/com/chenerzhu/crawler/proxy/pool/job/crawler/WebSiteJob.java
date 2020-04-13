package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author vincent
 * @create 2019-11-11
 * http://www.ip3366.net/?stype=1&page=1
 **/
@Slf4j
public class WebSiteJob extends AbstractCrawler {
    public WebSiteJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    public WebSiteJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl, int pageCount) {
        super(proxyIpQueue, pageUrl, pageCount);
    }

    @Override
    public void parsePage(WebPage webPage) {
        Elements elements = webPage.getDocument().getElementsByTag("div");
        log.info("website:{}",super.pageUrl);
    }

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

        WebSiteJob webSiteJob = new WebSiteJob(proxyIpQueue, "https://www.arcinbj.com");

        webSiteJob.run();
    }


}