package com.chenerzhu.crawler.proxy.util.bufflogin;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.buff.BuffConfig;
import com.chenerzhu.crawler.proxy.config.CookiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BuffAutoLoginUtil {

    @Autowired
    RestTemplate restTemplate;

    public static String login(String username, String password) {
        String url = "https://buff.163.com/";
        // 设置 ChromeDriver 的路径
        String projectPath = System.getProperty("user.dir");
        String edgeDriverPath = projectPath + "/src/main/resources/edgedriver_win64/msedgedriver.exe";
//        String edgeDriverPath = "resources/edgedriver_win64/msedgedriver.exe";
        log.info("Edge支持版本为：117.0.2045.60，请将Edge浏览器升级为最新版本，或使用最新脚本");
        System.setProperty("webdriver.edge.driver", edgeDriverPath);
        WebDriver driver = new EdgeDriver();
        String cookie = "";
        try {
            // 创建 ChromeDriver 实例

            driver.get(url);

            WebElement element = driver.findElement(By.cssSelector(".nav_entries>ul>li"));
            element.click();
            Thread.sleep(4000);
            WebDriver driver1 = driver;
            driver1.findElement(By.cssSelector("#agree-checkbox > span > i")).click();
            try {
                driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[9]/div/div[3]/div[1]/iframe")));
            } catch (Exception e) {
                driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[10]/div/div[3]/div[1]/iframe")));
            }
            driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[1]/a")).click();
            Thread.sleep(1000);
            WebElement phoneEl = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/form/div/div[2]/div[1]/input"));
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
