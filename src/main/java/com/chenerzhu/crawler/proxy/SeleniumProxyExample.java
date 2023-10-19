package com.chenerzhu.crawler.proxy;

import cn.hutool.core.util.StrUtil;
import com.chenerzhu.crawler.proxy.steam.util.SleepUtil;
import com.chenerzhu.crawler.proxy.util.HttpClientUtils;
import com.chenerzhu.crawler.proxy.util.Pop3EmailClientUtil;
import com.chenerzhu.crawler.proxy.util.Recaptchav2Util;
import com.chenerzhu.crawler.proxy.util.bufflogin.BuffAutoLoginUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
        System.setProperty("webdriver.edge.driver", edgeDriverPath);
        WebDriver driver = new EdgeDriver(options);
//        // 最小化浏览器窗口
//        Dimension minSize = new Dimension(0, 0); // 设置为 0x0 大小
//        Point minPosition = new Point(-2000, 0); // 设置到屏幕外
//        driver.manage().window().setSize(minSize);
//        driver.manage().window().setPosition(minPosition);
        String email = "admin@qingliu.love";
        driver.get("https://store.steampowered.com/join");

        WebDriverWait waitemail = new WebDriverWait(driver, 10);
        WebElement emailEle = waitemail.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div/input")));
        emailEle.sendKeys(email);
        WebElement email2 = driver.findElement(By.xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div/input"));
        email2.sendKeys(email);
        // 使用JavaScript执行点击事件
        driver.findElement(By.cssSelector("#i_agree_check")).click();
        // 等待 iframe 元素加载
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[title='reCAPTCHA']")));
        String reloadsStr = iframe.getAttribute("src");
        //验证码唯一值
        String k = reloadsStr.split("k=")[1].split("&")[0];

        //获取captchagid数据
        WebElement captchagidEle = driver.findElement(By.id("captchagid"));
        String captchagid = captchagidEle.getAttribute("value");


// //
////        WebElement commit = driver.findElement(By.id("createAccountButton"));
//        // /html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[6]/div[2]/button/span
//        WebElement commit = driver.findElement(By
//                .xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[6]/div[2]/button/span"));
//        commit.click();


        // 查找id为"error_display"的元素
        WebElement errorElement = driver.findElement(By.id("error_display"));

        // 获取元素的文本内容
        String errorText = errorElement.getText();
        if (StrUtil.isNotEmpty(errorText)) {
            System.out.println("此次注册认为是机器人");
            return;
        }
        WebElement init_idEle = driver.findElement(By.id("init_id"));
        String init_id = init_idEle.getAttribute("value");
        System.out.println("123123");
        Set<Cookie> cookies = driver.manage().getCookies();

        String captcha_text = Recaptchav2Util.checkRecaptchav2(k);

//        String script = "document.querySelector('input[name=\"g-recaptcha-response\"]').value = '"+captcha_text+"';";
//        ((JavascriptExecutor) driver).executeScript(script);
//
//        //完成按钮
//        WebElement complateEle = driver.findElement(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[4]/div[1]/input"));
//        complateEle.click();
        String cookie = "";
        for (Cookie cookieTe : cookies) {
            cookie = cookie + cookieTe.toString();
        }
        ajaxverifyemail(email, captchagid,captcha_text,init_id,cookie);
        Pop3EmailClientUtil.registerUrl();

        //第二个页面 创建账号
        WebDriverWait waiteName = new WebDriverWait(driver, 10);
        //账号名称
        WebElement nameEle = waiteName.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div[1]/input")));

        WebElement passwordEle = driver.findElement(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div[1]/input"));
        WebElement passwordEle2 = driver.findElement(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[4]/div[1]/input"));
        String name = randomString();
        System.out.println("注册的账号为：" + name);
        String password = randomString();
        System.out.println("注册的密码为：" + password);
        nameEle.sendKeys(name);
        passwordEle.sendKeys(password);
        passwordEle2.sendKeys(password);


        // 账号 ： /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div[1]/input
        // 密码   /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div[1]/input
        // 密码2   /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[4]/div[1]/input
        // 完成    /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[5]/div/button

        SleepUtil.sleep(20000);
        // 执行你的自动化测试代码
        // ...

        // 关闭浏览器
        driver.quit();
    }

    /**
     * 获取十位的随机数
     *
     * @return
     */
    public static String randomString() {
        Random random = new Random();

        // 生成随机字母
        char randomLetter = (char) (random.nextInt(26) + 'A');

        // 生成随机数字部分
        long randomNumber = random.nextLong() % 10000000000L;
        if (randomNumber < 0) {
            randomNumber += 10000000000L;
        }

        // 将字母和数字部分组合成最终的随机数
        StringBuilder randomValue = new StringBuilder();
        randomValue.append(randomLetter);
        randomValue.append(randomNumber);

        System.out.println("Random Number: " + randomValue.toString());
        return randomValue.toString();
    }

    public static String ajaxverifyemail(String email, String captchagid, String captcha_text, String init_id, String cookie) {
        String url = "https://store.steampowered.com/join/ajaxverifyemail";
        Map<String, String> headerMap = new HashMap() {{
            put("X-Requested-With", "XMLHttpRequest");
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.57");

            put("Host", "store.steampowered.com");
            put("Origin", "https://store.steampowered.com");
            put("Referer", "https://store.steampowered.com/join");
            put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            put("Cookie", cookie);
        }};
        Map<String, String> paramerMap = new HashMap() {{
            put("email", email);
            put("captchagid", captchagid);
            put("captcha_text", captcha_text);
            put("elang", "6");
            put("init_id", String.valueOf(init_id));
        }};
        String registerRes = HttpClientUtils.sendPostForm(url, "", headerMap, paramerMap);


        return "";
    }
}
