package com.xiaojuzi.st.buff.enumtype;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * buff查询分类分组
 */


public enum CategoryGroupEnum {

    hands("hands","手套"),
    rifle("rifle","步枪"),
    pistol("pistol","手枪"),
    smg("smg","微型冲风枪"),
    shotgun("shotgun","散弹枪"),
    machinegun("machinegun","机枪");

    private  String type;
    private  String memo;


     CategoryGroupEnum(String type,String memo){
        this.type = type;
        this.memo = memo;
    }


    public static List<String> getTypes(){
        return Arrays.stream(values()).map(CategoryGroupEnum::getType).collect(Collectors.toList());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
