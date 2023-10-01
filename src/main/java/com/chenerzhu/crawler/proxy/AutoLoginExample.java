package com.chenerzhu.crawler.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class AutoLoginExample {
    public static void main(String[] args) {
        String url = "https://buff.163.com/";
        String username = "15347971344";
        String password = "QingLiu98!";
        // 设置 ChromeDriver 的路径
        String projectPath = System.getProperty("user.dir");
        String edgeDriverPath = projectPath + "/src/main/resources/edgedriver_win64/msedgedriver.exe";
        System.setProperty("webdriver.edge.driver", edgeDriverPath);

        try {
            // 使用Desktop类打开默认浏览器
//            Desktop.getDesktop().browse(new URI(url));

            // 等待一段时间，确保网页加载完成
            Thread.sleep(5000);

            // 创建 ChromeDriver 实例
            WebDriver driver = new EdgeDriver();
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement navElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".nav_entries>ul>li")));


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
            String cookie = String.valueOf(manage.getCookieNamed("session"));
            if (StrUtil.isEmpty(cookie)) {
                log.info(username + "获取cookie失败，请检查密码是否正确");
            }
            // 等待登录完成，可以根据页面元素的变化或者跳转来判断登录是否成功

            // 关闭浏览器
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
