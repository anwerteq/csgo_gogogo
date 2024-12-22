package com.xiaojuzi.buff.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojuzi.csgo.entity.Tag;
import com.xiaojuzi.csgo.entity.Tags;
import com.xiaojuzi.csgo.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TagService {
    @Autowired
    TagRepository tagRepository;



    public void saveTags(Tags tags, long item_id) {
        String tagsStr = JSON.toJSONString(tags);
        HashMap<String, JSONObject> tagsHash = JSON.parseObject(tagsStr, HashMap.class);
        tagsHash.entrySet().parallelStream().forEach(entry -> {
            String tagStr = JSONObject.toJSONString(entry.getValue());
            Tag tag = JSONObject.parseObject(tagStr, Tag.class);
            tag.setItem_id(item_id);
            try {
                tagRepository.save(tag);

            } catch (Exception e) {
                System.out.println("12312");
                throw e;
            }
        });
    }
}
