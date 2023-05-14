package com.chenerzhu.crawler.proxy.pool.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.chenerzhu.crawler.proxy.pool.csgo.util.HttpsSendUtil;
import com.chenerzhu.crawler.proxy.pool.entity.IPWhiteList;
import com.chenerzhu.crawler.proxy.pool.entity.ProxyIp;
import com.chenerzhu.crawler.proxy.pool.entity.Result;
import com.chenerzhu.crawler.proxy.pool.service.IPWhiteListService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpRedisService;
import com.chenerzhu.crawler.proxy.pool.service.IProxyIpService;
import com.chenerzhu.crawler.proxy.pool.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author chenerzhu
 * @create 2018-08-29 19:51
 **/
@Slf4j
@Controller
public class ProxyIpController extends BaseController {

    /**
     * Redis ip key
     */
    private static final String REDIS_IP_KEY = "ip_white_list";

    @Autowired
    private IProxyIpRedisService proxyIpRedisService;

    @Resource
    private IProxyIpService proxyIpService;

    @Autowired
    private IPWhiteListService whiteListService;

    @Autowired
    private HttpsSendUtil httpsSendUtil;

    @GetMapping("/")
    public String index(ModelMap modelMap) {
        List proxyIpList = proxyIpRedisService.findAllByPageRt(0, 20);
        modelMap.put("proxyIpList", JSON.toJSON(proxyIpList));
        return "index";
    }

    /**
     * 更新白名单
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @GetMapping("/updateIpWhiteList")
    @ResponseBody
    public Result updateIpWhiteList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {

        Result result = new Result();

        // 更新白名单
        boolean flag = whiteListService.updateIpWhiteList();
        if (!flag) {
            result.setCode(500);
            result.setMessage("clean error !");
            return result;
        }

        result.setCode(200);
        result.setMessage("clean success !");
        return result;
    }

    @GetMapping("/proxyIpLow")
    @ResponseBody
    public Object getProxyIpLow(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        ProxyIp proxyIp = proxyIpRedisService.getOne();
        boolean available = proxyIpService.testIp(proxyIp.getIp(), proxyIp.getPort(), proxyIp.getType());
        while (!available) {
            proxyIp = proxyIpRedisService.getOne();
            available = proxyIpService.testIp(proxyIp.getIp(), proxyIp.getPort(), proxyIp.getType());
        }
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(Arrays.asList(proxyIp));
        return result;
    }

    @GetMapping("/proxyIp")
    @ResponseBody
    public Object getProxyIp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        ProxyIp proxyIp = proxyIpRedisService.getOneRt();
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(Arrays.asList(proxyIp));
        return result;
    }

    @GetMapping("/proxyAllByTXT")
    public void proxyAllByTXT(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        StringBuffer str = new StringBuffer();
        List<Serializable> allByPage = proxyIpRedisService.findAllByPageRt(0, -1);
        for (Serializable serializable : allByPage) {
            ProxyIp proxyIp = (ProxyIp) serializable;
            str.append(proxyIp.getIp() + ":" + proxyIp.getPort());
            str.append("\r\n");
        }
        PrintWriter out = response.getWriter();
        out.println(str.toString());
        out.close();
    }


    @PostMapping("/test")
    @ResponseBody
    public Object testIp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        String ip = request.getParameter("ip").trim();
        String port = request.getParameter("port").trim();
        boolean available = proxyIpService.testIp(ip, Integer.parseInt(port));
        Result result = new Result();
        result.setCode(200);
        result.setData(new ArrayList());
        result.setMessage(available == true ? "available" : "unavailable");
        return result;
    }

    @GetMapping("/test1")
    public String test1() {
        Map<String, String> map = new HashMap() {
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

            }
        };
//        ProxyIp oneRt = proxyIpRedisService.getOneRt();
        ProxyIp oneRt = new ProxyIp();
        String url = "https://buff.163.com/api/market/goods?game=csgo&page_num=1&use_suggestion=0&_=1683997415442";
//        String url = "http://buff.163.com/api/market/goods?game=csgo&page_num=1&use_suggestion=0&_=1684037703215";
        Object send = httpsSendUtil.send(oneRt.getIp(), oneRt.getPort(), url, map);

        return "test";
    }

    public static void main(String[] args) {
        ProxyIpController proxyIpController = new ProxyIpController();
        proxyIpController.test1();
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
