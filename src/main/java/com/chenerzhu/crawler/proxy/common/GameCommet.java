package com.chenerzhu.crawler.proxy.common;


import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏类别
 */
@Configuration
public class GameCommet {

    public static List<String> gameList = new ArrayList() {{
        add("csgo");
        add("data2");
    }};
    private static String auto_sale;

    public static Boolean check(String game) {
        auto_sale = game;
        return game.contains(game);
    }


    /**
     * 获取游戏类别
     *
     * @return
     */
    public static String getGame() {
        if (gameList.contains(auto_sale)) {
            return auto_sale;
        }
        return "csgo";
    }

    public static int getCsgoSum(int sell_num) {
        int quantity = 0;
        if (sell_num < 300) {
            return 0;
        } else if (sell_num < 400) {
            quantity = 8;
        } else if (sell_num < 500) {
            quantity = 10;
        } else if (sell_num < 600) {
            quantity = 12;
        } else if (sell_num < 700) {
            quantity = 15;
        } else if (sell_num < 800) {
            quantity = 17;
        } else if (sell_num < 900) {
            quantity = 19;
        } else if (sell_num < 1000) {
            quantity = 22;
        } else {
            quantity = 25;
        }
        return quantity;
    }

    public static int getQuantity(int sell_num) {
        String game = getGame();
        if ("csgo".equals(game)) {
            return getCsgoSum(sell_num);
        } else if ("data2".equals(game)) {
            return 5;
        }
        return 0;
    }
}
