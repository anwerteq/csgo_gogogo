/**
  * Copyright 2023 ab173.com 
  */
package com.xiaojuzi.st.csgo.service.LowPaintwearEntity;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-10-28 0:0:22
 *
 * @author ab173.com (info@ab173.com)
 * @website http://www.ab173.com/json/
 */
@Data
public class Info {

    private String fraudwarnings;
    private String icon_url;
    private String inspect_mobile_size;
    private String inspect_mobile_url;
    private String inspect_preview_size;
    private String inspect_preview_url;
    private String inspect_size;
    private int inspect_state;
    private String inspect_url;
    private String original_icon_url;
    private int paintindex;
    private int paintseed;
    private List<Object> stickers;
    private List<Object> tournament_tags;
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

    public void setInspect_mobile_size(String inspect_mobile_size) {
         this.inspect_mobile_size = inspect_mobile_size;
     }
     public String getInspect_mobile_size() {
         return inspect_mobile_size;
     }

    public void setInspect_mobile_url(String inspect_mobile_url) {
         this.inspect_mobile_url = inspect_mobile_url;
     }
     public String getInspect_mobile_url() {
         return inspect_mobile_url;
     }

    public void setInspect_preview_size(String inspect_preview_size) {
         this.inspect_preview_size = inspect_preview_size;
     }
     public String getInspect_preview_size() {
         return inspect_preview_size;
     }

    public void setInspect_preview_url(String inspect_preview_url) {
         this.inspect_preview_url = inspect_preview_url;
     }
     public String getInspect_preview_url() {
         return inspect_preview_url;
     }

    public void setInspect_size(String inspect_size) {
         this.inspect_size = inspect_size;
     }
     public String getInspect_size() {
         return inspect_size;
     }

    public void setInspect_state(int inspect_state) {
         this.inspect_state = inspect_state;
     }
     public int getInspect_state() {
         return inspect_state;
     }

    public void setInspect_url(String inspect_url) {
         this.inspect_url = inspect_url;
     }
     public String getInspect_url() {
         return inspect_url;
     }

    public void setOriginal_icon_url(String original_icon_url) {
         this.original_icon_url = original_icon_url;
     }
     public String getOriginal_icon_url() {
         return original_icon_url;
     }

    public void setPaintindex(int paintindex) {
         this.paintindex = paintindex;
     }
     public int getPaintindex() {
         return paintindex;
     }

    public void setPaintseed(int paintseed) {
         this.paintseed = paintseed;
     }
     public int getPaintseed() {
         return paintseed;
     }

}