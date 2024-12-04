package com.xiaojuzi.st.steam.service.csgoFloat;

import lombok.Data;

import java.util.List;

/**
 * steam市场磨损的信息
 */
@Data
public class FloatBulk {

    String linkKey;
    private int origin;
    private int quality;
    private int rarity;
    private String a;
    private String d;
    private int paintseed;
    private int defindex;
    private int paintindex;
    private List<String> stickers;
    private String floatid;
    private int low_rank;
    private int high_rank;
    private int killeatervalue;
    private double floatvalue;
    private String m;
    private String s;
}
