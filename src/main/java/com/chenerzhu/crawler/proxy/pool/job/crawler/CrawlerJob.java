package com.chenerzhu.crawler.proxy.pool.job.crawler;

import com.chenerzhu.crawler.proxy.pool.entity.ProxyApi;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyConfig;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.job.execute.ISchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.job.execute.impl.SchedulerJobExecutor;
import com.chenerzhu.crawler.proxy.pool.service.IPWhiteListService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyApiService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyConfigService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import com.chenerzhu.crawler.proxy.pool.thread.ThreadFactory;

/**
 * @author chenerzhu
 * @create 2018-09-02 20:16
 **/
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class CrawlerJob implements Runnable {
    private volatile static ExecutorService executorService= Executors.newFixedThreadPool(5,new ThreadFactory("crawlerJob-consumer"));

    private ISchedulerJobExecutor schedulerJobExecutor=new SchedulerJobExecutor(30,"crawlerJob-producer");

    @Autowired
    private IProxyIpService proxyIpService;

    @Autowired
    private IProxyApiService proxyApiService;

    @Autowired
    private IProxyConfigService proxyConfigService;

    @Autowired
    private IPWhiteListService whiteListService;

    @Override
    public void run() {
        try{
            ConcurrentLinkedQueue<ProxyIp> proxyIpQueue = new ConcurrentLinkedQueue<>();

            // 获得配置信息
            ProxyConfig proxyConfig = proxyConfigService.getConfig();

            //生产者

            // 私有化 txt api
            if(null != proxyConfig){
                List<ProxyApi> addrList = proxyApiService.findAll();
                int delay = proxyConfig.getDelayTime();
                for (ProxyApi api : addrList) {
                    if(null == api){
                        continue;
                    }

                    // 如果为0 则为随机数
                    if(delay == 0){
                        delay = RandomUtils.nextInt(10, 30);
                    }
                    // 处理 txt 类型接口
                    if("txt".equals(api.getType())){
                        schedulerJobExecutor.execute(new PrivateTXTJob(proxyIpQueue, api.getIpApi()), delay, 10, TimeUnit.SECONDS);
                    }
                }
            }


//            schedulerJobExecutor.execute(new Data5uCrawlerJob(proxyIpQueue, "http://www.data5u.com/"), 0, 120, TimeUnit.SECONDS);


            schedulerJobExecutor.execute(new KuaidailiCrawlerJob(proxyIpQueue, "https://www.kuaidaili.com/free/inha/#/",5), 10, 20, TimeUnit.SECONDS);
            schedulerJobExecutor.execute(new KuaidailiCrawlerJob(proxyIpQueue, "http://www.kuaidaili.com/free/intr/#/", 5), 10, 20, TimeUnit.SECONDS);


            schedulerJobExecutor.execute(new IP366CrawlerJob(proxyIpQueue, "http://www.ip3366.net/?stype=1&page=#", 5), 30, 50, TimeUnit.SECONDS);

            // Not available in China mainland
            schedulerJobExecutor.execute(new FreeProxyListCrawlerJob(proxyIpQueue, "https://free-proxy-list.net/"), 1000, 200, TimeUnit.SECONDS);
//            schedulerJobExecutor.execute(new SpysOneCrawlerJob(proxyIpQueue, "http://spys.one/en/free-proxy-list/"), 40, 100, TimeUnit.SECONDS);

            schedulerJobExecutor.execute(new ProxynovaCrawlerJob(proxyIpQueue, "https://www.proxynova.com/proxy-server-list/"), 30, 50, TimeUnit.SECONDS);
//            schedulerJobExecutor.execute(new GatherproxyCrawlerJob(proxyIpQueue, "http://www.gatherproxy.com/"), 70, 100, TimeUnit.SECONDS);

            // Slow in China mainland
            schedulerJobExecutor.execute(new MyProxyCrawlerJob(proxyIpQueue, "https://www.my-proxy.com/free-proxy-list.html"), 30, 50, TimeUnit.SECONDS);


            //消费者
            for (int i = 0; i < 5; i++) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (true && !Thread.currentThread().isInterrupted()) {
                            try {
                                log.info("the proxyIpQueue current  size:{}", proxyIpQueue.size());
                                ProxyIp proxyIp = proxyIpQueue.poll();
                                synchronized (this) {
                                    if (proxyIp != null) {
                                        log.debug("get proxy ip:{}", proxyIp.toString());
                                        if (proxyIpService.findByIpEqualsAndPortEqualsAndTypeEquals(proxyIp.getIp(), proxyIp.getPort(), proxyIp.getType()) == null) {
                                            proxyIpService.save(proxyIp);
                                        } else {
                                            log.debug("the proxy ip exist:{}", proxyIp.toString());
                                        }
                                    }else{
                                        TimeUnit.SECONDS.sleep(3);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("get the proxy ip  failed! error:{}",e.getMessage());
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
