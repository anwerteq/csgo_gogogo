package com.xiaojuzi.st.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验磨损工具类
 *
 * @author muyun
 * @date 2023/10/28 16:06
 */
public class CheckWearUtil {
    /**
     * @param wearDouble
     * @return
     */
    public static String checkWear(String wearDouble) {
        String wearDoubleReplace = wearDouble.replace("0.", "");
        char[] wearDoubleChar = wearDoubleReplace.toCharArray();
        Integer wearDoubleCharIndex = null;
        for (int i = 0; i < wearDoubleChar.length; i++) {
            if (wearDoubleChar[i] != '0') {
                wearDoubleCharIndex = i;
                break;
            }
        }
        String wearStr = wearDoubleReplace.substring(wearDoubleCharIndex);
        char[] wearChar = wearStr.toCharArray();
        String equipmentWearDegree = "";
        if (wearStr.startsWith("6666") || wearStr.startsWith("8888")) {
            equipmentWearDegree = wearDouble;
        } else if (wearChar.length > 2 && wearChar[0] == wearChar[1] && wearChar[1] == wearChar[2]) {
            equipmentWearDegree = wearDouble;
        } else if (wearDoubleChar.length == 2 || wearDoubleChar.length == 3 || wearDoubleChar.length == 4) {
            equipmentWearDegree = wearDouble;
        } else if (wearStr.startsWith("520") || dateCheck(wearStr)) {
            equipmentWearDegree = wearDouble;
        }
        return equipmentWearDegree;
    }

    /**
     * 校验日期字符串
     *
     * @param date yyyyMMddxxxxxxx
     * @return
     */
    private static boolean dateCheck(String date) {
        String dateTime = date.substring(0, 8);
        String pat = "\\d{4}\\d{2}\\d{2}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(dateTime);
        return m.matches();
    }

    public static void main(String[] args) {
        String s = checkWear("0.1995021402835846");
        System.out.println("123123");
    }
}
