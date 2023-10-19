package com.chenerzhu.crawler.proxy;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class FetchEmails {
    public static void main(String[] args) {
        // 邮箱账户信息
        String username = "admin@qingliu.love";
        String password = "123456789";

        // 邮箱服务器信息
        String host = "imap.qingliu.love";
        int port = 993;

        try {


            // 创建一个信任管理器，用于信任所有证书
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 创建一个自定义的 SSL 上下文
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
// 获取自定义的 SSLSocketFactory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
// 设置自定义的 SSLSocketFactory 到邮件会话属性
            // 创建会话属性
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.host", host);
            properties.put("mail.imaps.port", port);
            properties.setProperty("mail.imap.ssl.socketFactory", sslSocketFactory.getClass().getName());

            // 创建会话对象
            Session session = Session.getInstance(properties);

            // 连接到邮箱服务器
            Store store = session.getStore();
            store.connect(username, password);

            // 打开收件箱
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // 获取收件箱中的邮件
            Message[] messages = inbox.getMessages();

            // 遍历每封邮件并打印邮件内容
            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + getTextFromMessage(message));
                System.out.println("--------------------------------------");
            }

            // 关闭邮箱相关资源
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
