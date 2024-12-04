package com.xiaojuzi.st;

import com.xiaojuzi.st.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CsgoExchangeSelenium {
//    public static void main(String[] args) {
//        // 设置代理服务器
//        String proxyAddress = "127.0.0.1";
//        int proxyPort = 7890;
//        // 创建EdgeOptions对象，并设置代理
//        EdgeOptions options = new EdgeOptions();
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy(proxyAddress + ":" + proxyPort);
//        proxy.setSslProxy(proxyAddress + ":" + proxyPort);
////        options.setProxy(proxy);
//
//
//        // 初始化EdgeDriver，并传入EdgeOptions对象
//        String edgeDriverPath = BuffAutoLoginUtil.getResource();
////        System.setProperty("webdriver.edge.driver", edgeDriverPath);
//        System.setProperty("webdriver.edge.driver", "C:\\Users\\Administrator\\Desktop\\csgo-coding\\edgedriver_win64\\msedgedriver1.exe");
//        WebDriver driver = new EdgeDriver(options);
////        // 最小化浏览器窗口
////        Dimension minSize = new Dimension(0, 0); // 设置为 0x0 大小
////        Point minPosition = new Point(-2000, 0); // 设置到屏幕外
////        driver.manage().window().setSize(minSize);
////        driver.manage().window().setPosition(minPosition);
//
//        try{
//            String email = "admin4@qingliu.love";
//            driver.get("https://csgo.exchange/home#item/market");
//
//            WebDriverWait steamSsoLoginWait = new WebDriverWait(driver, 10);
//            WebElement steamSsoLoginELe = steamSsoLoginWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/ul/li[9]/a/img")));
//            steamSsoLoginELe.click();
//            //
//            WebDriverWait steamNameWait = new WebDriverWait(driver, 20);
//            WebElement steamNameWaitEle = steamNameWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[4]/div[1]/div[1]/div/div/div/div[2]/div/form/div[1]/input")));
//            steamNameWaitEle.sendKeys("L6913222025");
//            WebElement steamPassWaitEle = steamNameWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[4]/div[1]/div[1]/div/div/div/div[2]/div/form/div[2]/input")));
//            steamPassWaitEle.sendKeys("V2117135497");
//            WebElement steamLoginButonWaitEle = steamNameWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[4]/div[1]/div[1]/div/div/div/div[2]/div/form/div[4]/button")));
//            steamLoginButonWaitEle.click();
//            //
//
//            WebDriverWait steamSsoLoginButtonWait = new WebDriverWait(driver, 20);
//            WebElement steamSsoLoginButtonEle = steamSsoLoginButtonWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[4]/div/div[2]/div[2]/div/form/input[5]")));
//            steamSsoLoginButtonEle.click();
//
//            SleepUtil.sleep(2* 1000);
//
//            Set<Cookie> cookies1 = driver.manage().getCookies();
//
//            for (Cookie cookie : cookies1) {
//                String name = cookie.getName();
//                String value = cookie.getValue();
//                String domain = cookie.getDomain();
//                // 其他可用的方法：getPath(), getExpiry(), isSecure(), etc.
//                System.out.println("Cookie: " + name + "=" + value + "; Domain=" + domain);
//            }
//
//            WebDriverWait waitemail = new WebDriverWait(driver, 10);
//            WebElement emailEle = waitemail.until(ExpectedConditions
//                    .presenceOfElementLocated(By.xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div/input")));
//            emailEle.sendKeys(email);
//            WebElement email2 = driver.findElement(By.xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div/input"));
//            email2.sendKeys(email);
//            // 使用JavaScript执行点击事件
//            driver.findElement(By.cssSelector("#i_agree_check")).click();
//            // 等待 iframe 元素加载
//            WebDriverWait wait = new WebDriverWait(driver, 10);
//            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[title='reCAPTCHA']")));
//            // /html/body/div[2]/div[3]/div[1]/div/div/span/div[1]
//            String reloadsStr = iframe.getAttribute("src");
//            //验证码唯一值
//            String k = reloadsStr.split("k=")[1].split("&")[0];
//
//            //获取captchagid数据
//            WebElement captchagidEle = driver.findElement(By.id("captchagid"));
//            String captchagid = captchagidEle.getAttribute("value");
//
//
//            // 查找id为"error_display"的元素
//            WebElement errorElement = driver.findElement(By.id("error_display"));
//
//            // 获取元素的文本内容
//            String errorText = errorElement.getText();
//            if (StrUtil.isNotEmpty(errorText)) {
//                System.out.println("此次注册认为是机器人");
//                return;
//            }
//
//            // 校验页面
//            WebDriverWait graphicalEleWait = new WebDriverWait(driver, 10);
//            By iframeLocator = By.cssSelector("iframe[title='reCAPTCHA']");
//            WebElement graphicalEle = graphicalEleWait.until(ExpectedConditions.presenceOfElementLocated(iframeLocator));
//            // 切换到iframe
//            driver.switchTo().frame(graphicalEle);
//            // 人机验证按钮 验证标签
//            WebDriverWait labelEleWait = new WebDriverWait(driver, 10);
//            WebElement labelEle = labelEleWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div[1]/div/div/span/div[1]")));
//            labelEle.click();
//            driver.switchTo().defaultContent();
//
//
//            //获取验证iframe 查找包含特定标题的iframe
//            WebElement auditEleIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[title='reCAPTCHA 验证将于 2 分钟后过期']")));
//            // 切换到iframe
//            driver.switchTo().frame(auditEleIframe);
//            WebDriverWait auditEleWait = new WebDriverWait(driver, 10);
//            WebElement auditEle = auditEleWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/div[3]/div[2]/div[1]/div[1]/div[2]/button")));
//            auditEle.click();
//            //下载音频按钮
//            WebElement downdAuditEle = auditEleWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div/div/div[7]/a")));
//            String downUrl = downdAuditEle.getAttribute("href");
//            String base64 = ImageToBase64Converter.convertToBase64(downUrl);
//            String text = Recaptchav2Util.getText(base64);
//            //输入听的数据
//            WebElement inputField = driver.findElement(By.id("audio-response"));
//
//            // 向输入框发送文本
//            inputField.sendKeys(text);
//
//
//            //
//            WebElement recaptchaVerifyButtonELe = driver.findElement(By.id("recaptcha-verify-button"));
//            recaptchaVerifyButtonELe.click();
//
//            ///html/body/div/div/div[6]/input
//            driver.switchTo().defaultContent();
//
//            //完成按钮
//            WebElement continueEle = driver.findElement(By.xpath("/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[6]/div[2]/button/span"));
//            continueEle.click();
//
//            Pop3EmailClientUtil.registerUrl(email);
//
//            int count = 0;
//            while (count++ < 10){
//                SleepUtil.sleep(2000);
//                try {
//                    driver.findElement(By.className("title_text"));
//                    // 元素存在
//                    System.out.println("元素存在");
//                } catch (NoSuchElementException e) {
//                    // 元素不存在
//                    System.out.println("元素不存在");
//                    break;
//                }
//            }
//            //第二个页面 创建账号
//            WebDriverWait waiteName = new WebDriverWait(driver, 10);
//            //账号名称
//            WebElement nameEle = waiteName.until(ExpectedConditions
//                    .presenceOfElementLocated(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div[1]/input")));
//
//            WebElement passwordEle = driver.findElement(By.xpath("/html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div[1]/input"));
//            // /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[4]/div[1]/input
//            WebElement passwordEle2 = driver.findElement(By.id("reenter_password"));
//
//            String name = randomString();
//            System.out.println("注册的账号为：" + name);
//            String password = randomString();
//            System.out.println("注册的密码为：" + password);
//            nameEle.sendKeys(name);
//            passwordEle.sendKeys(password);
//            passwordEle2.sendKeys(password);
//            //点击完成
//            WebElement complateEle = driver.findElement(By.id("createAccountButton"));
//            complateEle.click();
//
//            SleepUtil.sleep(20000);
//            if (true){
//                return;
//            }
//            WebElement init_idEle = driver.findElement(By.id("init_id"));
//            String init_id = init_idEle.getAttribute("value");
//            System.out.println("123123");
//            Set<Cookie> cookies = driver.manage().getCookies();
//
////        String captcha_text = Recaptchav2Util.checkRecaptchav2(k);
//            String captcha_text = "";
//
//            //
//
////        // 设置g-recaptcha-response字段的值
////        String script = "document.querySelector(\"#g-recaptcha-response\").textContent  = '" + captcha_text + "';";
////        ((JavascriptExecutor) driver).executeScript(script);
//
//
//            // 查找包含特定标题的iframe
//            WebElement reCAPTCHAEle = driver.findElement(By.cssSelector("iframe[title='reCAPTCHA 验证将于 2 分钟后过期']"));
//
//            // 切换到iframe
//            driver.switchTo().frame(reCAPTCHAEle);
//
//            // 获取iframe中的文本内容
//            WebElement inputReCAPTCHAEle = driver.findElement(By.xpath("/html/body/input"));
//            //设置token
//            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
//                    inputReCAPTCHAEle, "value", captcha_text);
////        inputReCAPTCHAEle
////
//            // 切换回默认的上下文
//            driver.switchTo().defaultContent();
//
////        // 使用JavaScript设置元素值
////        String xpath = "/html/body/div[1]/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[5]/div/div[1]/div/div/textarea";
////        String script = String.format(
////                "var element = document.evaluate('%s', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
////                        + "element.value = '%s';",
////                xpath, captcha_text);
////
////        ((JavascriptExecutor) driver).executeScript(script);
//
//
//
////
//
//            String cookie = "";
//            for (Cookie cookieTe : cookies) {
//                cookie = cookie + cookieTe.toString();
//            }
//
//            ajaxverifyemail(email, captchagid, captcha_text, init_id, cookie);
//
//
//
//
//
//            // 账号 ： /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[2]/div[1]/input
//            // 密码   /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[3]/div[1]/input
//            // 密码2   /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[4]/div[1]/input
//            // 完成    /html/body/div/div[7]/div[6]/div/div[1]/div[2]/form/div/div/div[5]/div/button
//
//            SleepUtil.sleep(20000);
//            // 执行你的自动化测试代码
//            // ...
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }finally {
//            // 关闭浏览器
//            driver.quit();
//        }
//
//
//
//    }

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
