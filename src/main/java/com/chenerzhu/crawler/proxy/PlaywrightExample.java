package com.chenerzhu.crawler.proxy;

import com.microsoft.playwright.*;
import com.microsoft.playwright.*;

import javax.servlet.http.Cookie;
import java.util.concurrent.TimeUnit;
public class PlaywrightExample {
    public static void main(String[] args) {
        String url = "https://buff.163.com/";
        // 初始化 Playwright
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            // 打开 URL
            page.navigate(url);

            // 等待并点击元素
            page.waitForSelector("xpath=/html/body/div[1]/div/div[3]/ul/li/a");
            page.click("xpath=/html/body/div[1]/div/div[3]/ul/li/a");
            //十天免登
            Locator locator = page.locator("xpath=/html/body/div[9]/div/div[3]/div[2]/div");
            locator.click();

            // 切换到 iframe
            page.waitForSelector("xpath=/html/body/div[9]/div/div[3]/div[1]/iframe");


            //账号密码iframe
            FrameLocator frameLocator = page.frameLocator("xpath=/html/body/div[9]/div/div[3]/div[1]/iframe");
            // 获取 iframe 内部的元素
            //切换账号密码登录页面
            Locator elementZhangHaoLogin = frameLocator.locator("xpath=/html/body/div[2]/div[2]/div[2]/form/div/div[1]/a");
            elementZhangHaoLogin.click();
            //协议
            Locator xieyi = frameLocator.locator("xpath=/html/body/div[2]/div[2]/div[2]/form/div/div[7]/label/span/input");
            xieyi.click();

            //账号密码
            Locator zhangHao = frameLocator.locator("xpath=/html/body/div[2]/div[2]/div[2]/form/div/div[2]/div[1]/input");
            zhangHao.fill("15347971344");

            Locator miMa = frameLocator.locator("xpath=/html/body/div[2]/div[2]/div[2]/form/div/div[4]/div[2]/input[2]");
            miMa.fill("QingLiu98!");

//            Locator loginBut = frameLocator.locator("xpath=/html/body/div[2]/div[2]/div[2]/form/div/div[7]/a");
            Locator loginBut = frameLocator.locator("[id='submitBtn']");
            loginBut.click();


            // 等待并获取 cookie
            page.waitForTimeout(2900);
            String cookie = page.context().cookies().stream()
                    .filter(c -> "session".equals(c.name)) // 直接访问字段
                    .map(c -> c.value) // 直接访问字段
                    .findFirst()
                    .orElse(null);


            if (cookie == null) {
                System.out.println("获取 cookie 失败，请检查密码是否正确");
            } else {
                System.out.println("获取的 cookie: " + cookie);
            }

            // 关闭浏览器
            browser.close();
        }
    }
}
