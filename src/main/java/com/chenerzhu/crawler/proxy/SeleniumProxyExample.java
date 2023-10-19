package com.chenerzhu.crawler.proxy;

import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.logging.Level;

public class SeleniumProxyExample {
    public static void main(String[] args) {
        // 设置代理服务器
        String proxyAddress = "127.0.0.1";
        int proxyPort = 7890;
        // 创建EdgeOptions对象，并设置代理
        EdgeOptions options = new EdgeOptions();
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress + ":" + proxyPort);
        proxy.setSslProxy(proxyAddress + ":" + proxyPort);
//        options.setProxy(proxy);


        // 初始化EdgeDriver，并传入EdgeOptions对象
        String edgeDriverPath = BuffAutoLoginUtil.getResource();
        System.setProperty("webdriver.edge.driver",edgeDriverPath);
        WebDriver driver = new EdgeDriver(options);
        // 最小化浏览器窗口
        Dimension minSize = new Dimension(0, 0); // 设置为 0x0 大小
        Point minPosition = new Point(-2000, 0); // 设置到屏幕外
        driver.manage().window().setSize(minSize);
        driver.manage().window().setPosition(minPosition);

        driver.get("https://store.steampowered.com/join");
        // 使用JavaScript执行点击事件
        driver.findElement(By.cssSelector("#i_agree_check")).click();
        // 等待 iframe 元素加载
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[title='reCAPTCHA']")));
        String src = iframe.getAttribute("src");


        //获取captchagid数据
        WebElement element = driver.findElement(By.id("captchagid"));
        String value = element.getAttribute("value");


        //  #label_agree  captcha_entry_recaptcha

//        // 创建JavascriptExecutor对象
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        // 执行JavaScript代码
//        js.executeScript("alert('Hello, Selenium!');");

        SleepUtil.sleep(5000);
        // 执行你的自动化测试代码
        // ...

        // 关闭浏览器
        driver.quit();
    }
}
