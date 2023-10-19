package com.chenerzhu.crawler.proxy.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取邮件中的url
 */
public class Pop3EmailClientUtil {


    public static void registerUrl() {
        getMessage("admin@qingliu.love", "123456789");
    }

    public static void getMessage(String username, String password) {
        // 邮箱账号和密码
        String steamRegisterUrl = getSteamRegisterUrl(username, password);
        //发送注册链接
        if (StrUtil.isNotEmpty(steamRegisterUrl)) {
            String reponse = HttpClientUtils.sendGet(steamRegisterUrl, new HashMap<>());
            System.out.println("123123");
        }

    }

    public static String getSteamRegisterUrl(String username, String password) {

        String steamRegisterUrl = "";
        // 邮箱服务器的 POP3 配置
        String pop3Host = "45.144.136.173";
        int pop3Port = 995;
        try {
            // 设置 JavaMail 属性
            Properties props = new Properties();
            props.put("mail.store.protocol", "pop3s");
            props.put("mail.pop3s.host", pop3Host);
            props.put("mail.pop3s.port", pop3Port);
            props.put("mail.pop3s.ssl.enable", "false");
            props.put("mail.pop3s.ssl.trust", "*"); // 忽略 SSL 证书验证
            // 创建 Session 对象
            Session session = Session.getDefaultInstance(props);

            // 连接到邮箱服务器
            Store store = session.getStore("pop3s");
            store.connect(pop3Host, pop3Port, username, password);

            // 打开收件箱
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // 获取收件箱中的邮件
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {

                Date sentDate = message.getSentDate();
                Date expirationDate = DateTime.from(DateUtil.offsetMinute(new Date(), -3).toInstant());
                if (sentDate.compareTo(expirationDate) > 0) {
                    String textFromMessage = getTextFromMessage(message);
                    System.out.println("Text: " + textFromMessage);
                    steamRegisterUrl = getSteamRegisterUrl(textFromMessage).trim();
                    break;
                }
//                // 处理邮件内容
//                String subject = message.getSubject();
//                String from = message.getFrom()[0].toString();
//                System.out.println("Subject: " + subject);
//                System.out.println("From: " + from);
            }
            // 关闭连接
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steamRegisterUrl;
    }

    /**
     * 获取steam的注册url
     *
     * @param text
     * @return
     */
    public static String getSteamRegisterUrl(String text) {
        // 使用正则表达式匹配URL
        String regex = "(https?://[\\w.-]+(?:/[\\w\\s./?%&=-]*)?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        // 提取匹配到的URL
        if (matcher.find()) {
            String extractedURL = matcher.group(1);
            System.out.println("提取到的URL: " + extractedURL);
            return extractedURL;
        } else {
            System.out.println("未能在邮件正文中找到URL。");
        }
        return "";
    }

    // 从邮件中获取文本内容
    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(multipart);
        } else {
            return "";
        }
    }

    // 解析多部分邮件内容
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        int count = mimeMultipart.getCount();
        if (count == 0) {
            return "";
        }
        boolean textFound = false;
        StringBuilder textContent = new StringBuilder();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                textContent.append(bodyPart.getContent());
                textFound = true;
                break; // 只获取第一个文本部分
            } else if (bodyPart.isMimeType("multipart/*")) {
                textContent.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        if (!textFound) {
            return "";
        }
        return textContent.toString();
    }
}
