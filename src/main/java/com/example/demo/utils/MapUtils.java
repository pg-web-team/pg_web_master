package com.example.demo.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtils {

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map,
        boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }
}
