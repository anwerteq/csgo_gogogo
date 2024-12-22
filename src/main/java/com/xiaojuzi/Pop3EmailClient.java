package com.xiaojuzi;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class Pop3EmailClient {
    public static void main(String[] args) {
        // 邮箱账号和密码
        String username = "admin@qingliu.love";
        String password = "123456789";

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
                // 处理邮件内容
                String subject = message.getSubject();
                String from = message.getFrom()[0].toString();
                System.out.println("Subject: " + subject);
                System.out.println("From: " + from);
                System.out.println("Text: " + getTextFromMessage(message));
            }

            // 关闭连接
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
