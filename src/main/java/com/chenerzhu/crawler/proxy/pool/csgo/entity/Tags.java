/**
 * Copyright 2023 bejson.com
 */
package com.chenerzhu.crawler.proxy.pool.csgo.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.*;

/**
 * Auto-generated: 2023-05-14 0:18:12
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Tags {



    private Tag category;
    private Tag quality;
    private Tag rarity;
    private Tag type;
    private Tag weapon;
    private Tag exterior;


    public List<Tag> getTagList(){
        ArrayList<Tag> tags = new ArrayList<>();
        if (!Objects.isNull(getQuality())){
            tags.add(getQuality());
        }
        if (!Objects.isNull(getRarity())){
            tags.add(getRarity());
        } if (!Objects.isNull(getType())){
            tags.add(getType());
        } if (!Objects.isNull(getWeapon())){
            tags.add(getWeapon());
        } if (!Objects.isNull(getExterior())){
            tags.add(getExterior());
        }
        return tags;
    }

    public static void main(String[] args) {
        String str = "{\n" +
                "\t\"tags\": {\n" +
                "\t\t\"category\": {\n" +
                "\t\t\t\"category\": \"category\",\n" +
                "\t\t\t\"id\": 642,\n" +
                "\t\t\t\"internal_name\": \"weapon_sawedoff\",\n" +
                "\t\t\t\"localized_name\": \"\\u622a\\u77ed\\u9730\\u5f39\\u67aa\"\n" +
                "\t\t},\n" +
                "\t\t\"category_group\": {\n" +
                "\t\t\t\"category\": \"category_group\",\n" +
                "\t\t\t\"id\": 641,\n" +
                "\t\t\t\"internal_name\": \"shotgun\",\n" +
                "\t\t\t\"localized_name\": \"\\u9730\\u5f39\\u67aa\"\n" +
                "\t\t},\n" +
                "\t\t\"custom\": {\n" +
                "\t\t\t\"category\": \"custom\",\n" +
                "\t\t\t\"id\": 11596,\n" +
                "\t\t\t\"internal_name\": \"purple\",\n" +
                "\t\t\t\"localized_name\": \"purple\"\n" +
                "\t\t},\n" +
                "\t\t\"exterior\": {\n" +
                "\t\t\t\"category\": \"exterior\",\n" +
                "\t\t\t\"id\": 512,\n" +
                "\t\t\t\"internal_name\": \"wearcategory2\",\n" +
                "\t\t\t\"localized_name\": \"\\u4e45\\u7ecf\\u6c99\\u573a\"\n" +
                "\t\t},\n" +
                "\t\t\"itemset\": {\n" +
                "\t\t\t\"category\": \"itemset\",\n" +
                "\t\t\t\"id\": 17345,\n" +
                "\t\t\t\"internal_name\": \"set_community_31\",\n" +
                "\t\t\t\"localized_name\": \"set_community_31\"\n" +
                "\t\t},\n" +
                "\t\t\"quality\": {\n" +
                "\t\t\t\"category\": \"quality\",\n" +
                "\t\t\t\"id\": 518,\n" +
                "\t\t\t\"internal_name\": \"strange\",\n" +
                "\t\t\t\"localized_name\": \"StatTrak\\u2122\"\n" +
                "\t\t},\n" +
                "\t\t\"rarity\": {\n" +
                "\t\t\t\"category\": \"rarity\",\n" +
                "\t\t\t\"id\": 591,\n" +
                "\t\t\t\"internal_name\": \"legendary_weapon\",\n" +
                "\t\t\t\"localized_name\": \"\\u4fdd\\u5bc6\"\n" +
                "\t\t},\n" +
                "\t\t\"type\": {\n" +
                "\t\t\t\"category\": \"type\",\n" +
                "\t\t\t\"id\": 579,\n" +
                "\t\t\t\"internal_name\": \"csgo_type_shotgun\",\n" +
                "\t\t\t\"localized_name\": \"\\u9730\\u5f39\\u67aa\"\n" +
                "\t\t},\n" +
                "\t\t\"weapon\": {\n" +
                "\t\t\t\"category\": \"weapon\",\n" +
                "\t\t\t\"id\": 549,\n" +
                "\t\t\t\"internal_name\": \"weapon_sawedoff\",\n" +
                "\t\t\t\"localized_name\": \"\\u622a\\u77ed\\u9730\\u5f39\\u67aa\"\n" +
                "\t\t},\n" +
                "\t\t\"weaponcase\": {\n" +
                "\t\t\t\"category\": \"weaponcase\",\n" +
                "\t\t\t\"id\": 17346,\n" +
                "\t\t\t\"internal_name\": \"Recoil Case\",\n" +
                "\t\t\t\"localized_name\": \"recoil case\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        HashMap<String, JSONObject> tagsHash = JSON.parseObject(str, HashMap.class);
        JSONObject tags = tagsHash.get("tags");
        for (Map.Entry<String, Object> entry : tags.entrySet()) {
            String tagStr = JSONObject.toJSONString(entry.getValue());
            Tag tag = JSONObject.parseObject(tagStr, Tag.class);
            System.out.println("12312");
        }



        System.out.println("123123");
    }
}
