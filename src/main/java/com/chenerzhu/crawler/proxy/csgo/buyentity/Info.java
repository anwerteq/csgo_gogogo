/**
 * Copyright 2023 json.cn
 */
package com.chenerzhu.crawler.proxy.csgo.buyentity;

import java.util.List;

/**
 * Auto-generated: 2023-05-24 18:40:29
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Info {

    private String fraudwarnings;
    private String icon_url;
    private List<String> stickers;
    private List<Tournament_tags> tournament_tags;
    public void setFraudwarnings(String fraudwarnings) {
         this.fraudwarnings = fraudwarnings;
     }
     public String getFraudwarnings() {
         return fraudwarnings;
     }

    public void setIcon_url(String icon_url) {
         this.icon_url = icon_url;
     }
     public String getIcon_url() {
         return icon_url;
     }

    public void setStickers(List<String> stickers) {
         this.stickers = stickers;
     }
     public List<String> getStickers() {
         return stickers;
     }

    public void setTournament_tags(List<Tournament_tags> tournament_tags) {
         this.tournament_tags = tournament_tags;
     }
     public List<Tournament_tags> getTournament_tags() {
         return tournament_tags;
     }

}
