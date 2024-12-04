package com.xiaojuzi.st.steam.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
    public static void main(String[] args) {
        String string = "\t\tCreateItemHoverFromContainer( g_rgAssets, 'history_row_5831654360491315109_5831654360491315110_name', 730, '2', '33254416435', 0 );\r\n\t\tCreateItemHoverFromContainer( g_rgAssets, 'history_row_5831654360491315109_5831654360491315110_image', 730, '2', '33254416435', 0 );\r\n\t\t\tCreateItemHoverFromContainer( g_rgAssets, 'history_row_4325199651999390596_4325199651999390597_name', 730, '2', '33212622942', 0 );\r\n\t\tCreateItemHoverFromContainer( g_rgAssets, 'history_row_4325199651999390596_4325199651999390597_image', 730, '2', '33212622942', 0 );\r\n\t";

        // 使用正则表达式提取参数
        String pattern = "CreateItemHoverFromContainer\\(\\s*g_rgAssets, '([^']*)', (\\d+), '([^']*)', '([^']*)', (\\d+) \\);";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(string);

        // 提取的参数存储在列表中
        while (matcher.find()) {
            String name = matcher.group(1);
            String number1 = matcher.group(2);
            String number2 = matcher.group(3);
            String number3 = matcher.group(4);
            String number4 = matcher.group(5);
            System.out.println("Name: " + name + ", Number1: " + number1 + ", Number2: " + number2 + ", Number3: " + number3 + ", Number4: " + number4);
        }
    }
}
