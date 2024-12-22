package com.xiaojuzi.common;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏类别
 */
@Configuration
@Slf4j
public class GameCommet {

    public static Map<String, String> gameMap = new HashMap() {{
        put("csgo", "730");
        put("dota2", "570");
    }};

    private static String auto_sale;

    public static Boolean check(String game) {
        auto_sale = game;
        return gameMap.containsKey(game);
    }


    /**
     * 获取游戏类别
     *
     * @return
     */
    public static String getGame() {
        if (gameMap.containsKey(auto_sale)) {
            return auto_sale;
        }
        return "csgo";
    }


    /**
     * 获取游戏类别
     *
     * @return
     */
    public static String getGameId() {
        String value = gameMap.getOrDefault(auto_sale, "");
        if (StrUtil.isNotEmpty(value)) {
            return value;
        }
        return "730";
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
        } else if ("dota2".equals(game)) {
            if (sell_num < 300) {
                log.info("dota2游戏,饰品在售小于300，跳过");
                return 0;
            }
            return 5;
        }
        return 0;
    }

    /**
     * 获取条件金额
     *
     * @return
     */
    public static int getPriceWhere() {
        String game = getGame();
        if ("csgo".equals(game)) {
            return 30;
        } else if ("dota2".equals(game)) {
            return 50;
        }
        return 30;
    }
}
