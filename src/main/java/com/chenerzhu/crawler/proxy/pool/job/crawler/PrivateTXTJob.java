package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author parker
 * @create 2019-11-11
 * 独立 txt 接口
 **/
@Slf4j
public class PrivateTXTJob extends AbstractCrawler {
    public PrivateTXTJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl) {
        super(proxyIpQueue, pageUrl);
    }

    public PrivateTXTJob(ConcurrentLinkedQueue<ProxyIp> proxyIpQueue, String pageUrl, int pageCount) {
        super(proxyIpQueue, pageUrl, pageCount);
    }

    @Override
    public void parsePage(WebPage webPage) {
        String ips = webPage.getPage();
        String[] ipArray = ips.split("\r\n");
        //String[] ipArray = {"192.0.0.104:8888","192.0.0.1:80","192.0.0.169:80","192.0.0.104:8888","192.0.0.104:8080"};

        ProxyIp proxyIp;
        for (String s : ipArray) {
            if(!StringUtils.isEmpty(s)){
                String[] ipAndPort = s.split(":");
                if(ipAndPort.length == 2){
                    try {
                        proxyIp = new ProxyIp();
                        proxyIp.setIp(ipAndPort[0]);
                        proxyIp.setPort(Integer.parseInt(ipAndPort[1]));
                        proxyIp.setLocation("私有化API");
                        proxyIp.setType("HTTP");
                        proxyIp.setAvailable(true);
                        proxyIp.setCreateTime(new Date());
                        proxyIp.setLastValidateTime(new Date());
                        proxyIp.setValidateCount(0);
                        proxyIpQueue.offer(proxyIp);
                    } catch (Exception e) {
                        log.error("kuaidailiCrawlerJob error:{0}",e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

        PrivateTXTJob privateJob = new PrivateTXTJob(proxyIpQueue, "http://218.78.97.2:8181/IP.txt");

        privateJob.run();
    }


}