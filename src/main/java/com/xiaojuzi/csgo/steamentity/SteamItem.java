
package com.xiaojuzi.csgo.steamentity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@ToString
@Entity
@Table(name = "steam_item")
public class SteamItem {
    @Id
    private String name;

    private String hash_name;

    private int sell_listings;

    private int sell_price;

    private String sell_price_text;

    private String app_icon;

    private String app_name;

    @Transient
    private AssetDescription asset_description;

    private String sale_price_text;
}
