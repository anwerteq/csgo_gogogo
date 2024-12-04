package com.xiaojuzi.st;

import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

public class FileUtil {


    public static void main1(String[] args) {
        String content = "Hello, World! \r\n"; // 要追加到文件的字符串内容

        try (FileWriter fileWriter = new FileWriter("steam.txt", true)) {
            fileWriter.write(content); // 将字符串追加到文件
            System.out.println("字符串已成功追加到文件。");
        } catch (IOException e) {
            System.out.println("写入文件时出现错误：" + e.getMessage());
        }
    }

    public static void writeNumber(String value) {
        String content =value + "\r\n"; // 要追加到文件的字符串内容

        try (FileWriter fileWriter = new FileWriter("steam.txt", true)) {
            fileWriter.write(content); // 将字符串追加到文件
            System.out.println("字符串已成功追加到文件。");
        } catch (IOException e) {
            System.out.println("写入文件时出现错误：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String str = "INSERT INTO `ewomail`.`i_users` (`id`, `domain_id`, `password`, `email`, `maildir`, `uname`, `tel`, `active`, `limits`, `limitg`, `ctime`) VALUES (#id, 1, '25f9e794323b453885f5181f1b624d0b', 'admin#id@qingliu.love', '/ewomail/mail/vmail/qingliu.love/a/d/m/admin.20231018', 'zml', '15347986532', 1, 0, 0, '2023-10-18 12:17:56');\n";
        StringJoiner stringJoiner = new StringJoiner(";");


        for (int i = 6; i < 100; i++) {
            String str1 = str.replaceAll("#id",String.valueOf(i));
            stringJoiner.add(str1);
        }
        System.out.println(stringJoiner.toString());

    }
}
