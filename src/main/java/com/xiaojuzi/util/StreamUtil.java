package com.xiaojuzi.util;

import cn.hutool.core.util.ReflectUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author lbb
 * @date 2023/8/17 16:56
 * @description
 */
public class StreamUtil {

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> boolean distinctById(T t) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        String id = Optional.ofNullable((String) (ReflectUtil.invoke(t.getClass(), "getId"))).orElse("");
        return seen.putIfAbsent(id, Boolean.TRUE) == null;
    }
}
