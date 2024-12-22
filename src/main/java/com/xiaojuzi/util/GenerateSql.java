package com.xiaojuzi.util;

import cn.hutool.core.util.ObjectUtil;

import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojuzi.util.StreamUtil.distinctByKey;


/**
 * 生成insert语句工具
 *
 * @param <T>
 * @date 2023/05/22
 */
public class GenerateSql<T> {

    public String createInsert(T entity, String tableName) {

//        String sql = "insert into " + tableName;
        String sql = "replace into " + tableName;
        // 列
        StringBuilder column = new StringBuilder();
        // 列值
        StringBuilder cValues = new StringBuilder();
        List<Map<String, Object>> list = getFieldsInfo(entity);
        for (Map<String, Object> map : list) {
            column.append(ChangeTools.humpToLine(map.get("f_name").toString())).append(",");
            cValues.append("?,");
        }
        sql += "(" + column.substring(0, column.length() - 1).toUpperCase() + ") values ("
                + cValues.substring(0, cValues.length() - 1) + ")";
        return sql;
    }

    /**
     * 根据属性名获取属性值
     */
    protected static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            // log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 类名(obj_name)获取属性类型(f_type)，属性名(f_name)，属性值(f_value)的map组成的list
     */
    @SuppressWarnings("unused")
    public static List<Map<String, Object>> getFieldsInfo(Object o) {
        String objName = o.getClass().getSimpleName();
        List<Field> fields = getFiledContainSuperClass(o);
        String[] fieldNames = new String[fields.size()];
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> infoMap;

        for (Field field : fields) {
            infoMap = new HashMap<>(16);
            infoMap.put("obj_name", objName);
            infoMap.put("f_type", field.getType().toString());
            infoMap.put("f_name", field.getName());
            infoMap.put("f_value", getFieldValueByName(field.getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    public static List<Field> getFiledContainSuperClass(Object obj) {
        if (ObjectUtil.isNull(obj)) {
            return null;
        }
        Class o = obj.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (o != null) {
            fieldList.addAll(Arrays.asList(o.getDeclaredFields()));
            o = o.getSuperclass();
        }
        return fieldList.stream().filter(distinctByKey(Field::getName)).filter(GenerateSql::isNonTransient).collect(Collectors.toList());
    }

    public List<Object> getFiledsObject(Object o) {
        List<Field> fields = getFiledContainSuperClass(o);
        List<Object> list = new ArrayList<>();
        for (Field field : fields) {
            list.add(getFieldValueByName(field.getName(), o));
        }
        return list;
    }

    public static boolean isNonTransient(Field field){
        Annotation[] annotations = field.getDeclaredAnnotations();
        return Arrays.stream(annotations).noneMatch(e -> e instanceof Transient);
    }

    public static void main(String[] args) {

    }

}
