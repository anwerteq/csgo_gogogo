package com.chenerzhu.crawler.proxy.util.bufflogin;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.chenerzhu.crawler.proxy.ProxyPoolApplication;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.buff.BuffUserData;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Component
public class BuffAutoLoginUtil {

    @Autowired
    RestTemplate restTemplate;

    public static String login(String username, String password) {
        String url = "https://buff.163.com/";
        // 设置 ChromeDriver 的路径
//        String projectPath = System.getProperty("user.dir") + "/src/main/resources/";
        String edgeDriverPath = getResource();
        log.info("edgeDriverPath:{}", edgeDriverPath);
        System.setProperty("webdriver.edge.driver", edgeDriverPath);
        WebDriver driver = new EdgeDriver();
        String cookie = "";
        try {
            // 创建 ChromeDriver 实例
            driver.get(url);
            WebDriverWait waitemail = new WebDriverWait(driver, 30000);
            WebElement element = waitemail.until(ExpectedConditions
                    .presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div[3]/ul/li/a")));
            element.click();
            SleepUtil.sleep(4 * 1000);
            WebDriverWait agreeEleWait = new WebDriverWait(driver, 30000);
            WebElement agreeEle = agreeEleWait.until(ExpectedConditions
                    .presenceOfElementLocated(By.cssSelector("#agree-checkbox > span > i")));
            agreeEle.click();
            try {
                WebDriverWait iframeEleWait = new WebDriverWait(driver, 30000);
                WebElement iframeEle = iframeEleWait.until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("/html/body/div[10]/div/div[3]/div[1]/iframe")));
                driver.switchTo().frame(iframeEle);
            } catch (Exception e) {
                WebDriverWait iframeEle1Wait = new WebDriverWait(driver, 30000);
                WebElement iframeEle1 = iframeEle1Wait.until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("/html/body/div[10]/div/div[3]/div[1]/iframe")));
                driver.switchTo().frame(iframeEle1);
            }
            WebDriverWait linkWait = new WebDriverWait(driver, 30000);
            WebElement linkEle = linkWait.until(ExpectedConditions
                    .presenceOfElementLocated(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[1]/a")));
            linkEle.click();
            WebDriverWait phoneElWait = new WebDriverWait(driver, 30000);
            WebElement phoneEl = phoneElWait.until(ExpectedConditions
                    .presenceOfElementLocated(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[2]/div[1]/input")));
            phoneEl.click();
            WebElement passwordEl = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[4]/div[2]/input[2]"));
            WebElement login = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[7]/a"));
            phoneEl.sendKeys(username);//手机号
            passwordEl.sendKeys(password);//密码
            login.click();
            Thread.sleep(2900);
            WebDriver.Options manage = driver.manage();
            cookie = String.valueOf(manage.getCookieNamed("session"));
            if (StrUtil.isEmpty(cookie)) {
                log.error(username + "获取cookie失败，请检查密码是否正确");
            }
            // 等待登录完成，可以根据页面元素的变化或者跳转来判断登录是否成功
            // 关闭浏览器
//            driver.quit();
        } catch (Exception e) {
            log.error("buff获取cookie失败，失败信息为：", e);
            e.printStackTrace();
            return "";
        } finally {
            //没有获取到cookie关闭浏览器
            if ("null".equals(cookie) || StrUtil.isEmpty(cookie)) {
                driver.quit();
            }
        }
        return cookie;
    }

    /**
     * 获取resource的路径
     *
     * @return
     */
    public static String getResource() {
        // 获取资源的URL
        URL resourceUrl = ProxyPoolApplication.class.getClassLoader().getResource("edgedriver_win64/msedgedriver.exe");
        // 获取资源的绝对路径
        String resourcePath = resourceUrl.getPath().replaceFirst("file:/", "");
        log.debug("jarPath11111111:{}", resourcePath);
        resourcePath = resourcePath.split("/proxy-pool-2.0.jar")[0];
        log.debug("jarPath11111122:{}", resourcePath);
        String uri = "/edgedriver_win64/msedgedriver.exe";
        if (!resourcePath.contains(uri)) {
            resourcePath = resourcePath + uri;
        }
        return resourcePath;
    }

    public static void main(String[] args) {
        System.out.println(getResource());
    }

    public String getCookie(BuffUserData buffUserData) {
        String url = "https://buff.163.com/account/api/user/info";
        String sessionId = buffUserData.getCookie();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "session=" + sessionId);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
        headers.set("Referer", "https://buff.163.com");
        HttpEntity<MultiValueMap<String, String>> entity1 = new HttpEntity(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (!"OK".equals(jsonObject.getString("code"))) {
            log.error("session：{}登录失败");
            return null;
        }
        String nickname = jsonObject.getString("nickname");
        buffUserData.setAcount(nickname);
        String steamid = jsonObject.getString("steamid");
        buffUserData.setSteamId(steamid);
        List<String> cookies = responseEntity.getHeaders().get("set-cookie");
        StringJoiner sj = new StringJoiner(";");
        for (String cookie : cookies) {
            sj.add(cookie);
        }
        buffUserData.setCookie(sj.toString());

        System.out.println("123123");
        return "";
    }


    /**
     * 或者buff cookie对应的steamId
     *
     * @param cookie
     * @return
     */
    public String getSteamId(String cookie) {

        String url = "https://buff.163.com/user-center/profile";
        CookiesConfig.buffCookies.set(cookie);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, BuffConfig.getBuffHttpEntity(), String.class);
        String body = responseEntity.getBody();
        String body1 = body.split("\"steamid\": ")[1];
        String steamId = body1.split("}, \"")[0];
        return steamId;
    }
}
