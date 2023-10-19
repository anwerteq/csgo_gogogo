package com.chenerzhu.crawler.proxy;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class MailExample {
    public static void main(String[] args) throws Exception {
        // 创建信任管理器，用于信任服务器证书
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                // 信任所有服务器证书
            }
        }};

        // 获取 SSL 上下文
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, null);

        // 创建自定义的 SSLSocketFactory
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // 创建自定义的信任管理器，用于主机名验证
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                // 始终信任主机名
                return true;
            }
        };

        // 设置自定义的 SSLSocketFactory 和主机名验证器到 IMAP 属性
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.socketFactory", sslSocketFactory.getClass().getName());
        props.setProperty("mail.imap.ssl.socketFactory.hostnameVerifier", hostnameVerifier.getClass().getName());
        props.put("mail.store.protocol", "imaps");
//        props.put("mail.imaps.host", host);
//        props.put("mail.imaps.port", port);
        // 创建邮件会话
        Session session = Session.getInstance(props);

        // 使用创建的邮件会话连接到服务器
        Store store = session.getStore("imap");
        store.connect("45.144.136.173:993", "admin@qingliu.love", "123456789");



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
        // 其他操作...
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
