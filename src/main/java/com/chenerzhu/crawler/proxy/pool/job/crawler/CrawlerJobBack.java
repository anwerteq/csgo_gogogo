package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.config.ProxyConfig;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import com.chenerzhu.crawler.proxy.pool.thread.ThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author chenerzhu
 * @create 2018-09-02 20:16
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class CrawlerJobBack implements Runnable {
    private volatile static ExecutorService executorService= Executors.newFixedThreadPool(5,new ThreadFactory("crawlerJob-consumer"));

    private ISchedulerJobExecutor schedulerJobExecutor=new SchedulerJobExecutor(30,"crawlerJob-producer");

    @Autowired
    private IProxyIpService proxyIpService;

    @Autowired
    private ProxyConfig proxyConfig;

    @Override
    public void run() {
        try{
            ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

            if(null == proxyConfig){
                return;
            }


            // 地址优化
            List<String> addrList = proxyConfig.getAddrList();
            for (String addr : addrList) {

                Integer delay = Integer.parseInt(proxyConfig.getDelayTime());
                // 如果为0 则开启随机数
                if(null == delay || delay == 0){
                    delay = RandomUtils.nextInt(20,100);
                }
                log.info("初始化代理任务 - 地址：{}  时效：{}",addr,delay);
                schedulerJobExecutor.execute(new GatherproxyCrawlerJob(proxyIpQueue, addr), delay, 100, TimeUnit.SECONDS);
            }


            //生产者
           /* //schedulerJobExecutor.execute(new XicidailiCrawlerJob(proxyIpQueue, "http://www.xicidaili.com/nn"), 0, 100, TimeUnit.SECONDS);

            //schedulerJobExecutor.execute(new Data5uCrawlerJob(proxyIpQueue, "http://www.data5u.com/free/index.shtml"), 10, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new FreeProxyListCrawlerJob(proxyIpQueue, "https://free-proxy-list.net"), 20, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new MyProxyCrawlerJob(proxyIpQueue, "https://www.my-proxy.com/free-proxy-list.html"), 30, 100, TimeUnit.SECONDS);

            //schedulerJobExecutor.execute(new SpysOneCrawlerJob(proxyIpQueue, "http://spys.one/en/free-proxy-list/"), 40, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new ProxynovaCrawlerJob(proxyIpQueue, "https://www.proxynova.com/proxy-server-list/"), 50, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new Proxy4FreeCrawlerJob(proxyIpQueue, "https://www.proxy4free.com/list/webproxy1.html"), 60, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new GatherproxyCrawlerJob(proxyIpQueue, "http://www.gatherproxy.com/"), 70, 100, TimeUnit.SECONDS);*/

            //消费者
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true && !Thread.currentThread().isInterrupted()) {
                            try {
                                log.info("代理ip定时任务 - 当前大小:{}", proxyIpQueue.size());
                                ProxyIp proxyIp = proxyIpQueue.poll();
                                if (proxyIp != null) {
                                    log.debug("获得代理ip:{}", proxyIp.toString());
                                    if (proxyIpService.findByIpEqualsAndPortEqualsAndTypeEquals(proxyIp.getIp(), proxyIp.getPort(), proxyIp.getType()) == null) {
                                        proxyIpService.save(proxyIp);
                                    } else {
                                        log.debug("代理ip存在:{}", proxyIp.toString());
                                    }
                                }else{
                                    TimeUnit.SECONDS.sleep(3);
                                }
                            } catch (Exception e) {
                                log.error("获取代理ip失败！ error:{}",e.getMessage());
                                //e.printStackTrace();
                                try {
                                    TimeUnit.SECONDS.sleep(3);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            log.error("crawler error:{}",e);
            executorService.shutdown();
            schedulerJobExecutor.shutdown();
        }finally {

        }
    }
}