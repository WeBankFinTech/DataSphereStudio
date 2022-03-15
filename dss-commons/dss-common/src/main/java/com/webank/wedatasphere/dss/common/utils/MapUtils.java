package com.webank.wedatasphere.dss.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-03-12
 * @since 0.5.0
 */
public class MapUtils {

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <K, V> Map<K, V> newMap(K key, V value) {
        Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    public static <K, V> Map<K, V> newMap(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    public static <K, V> MapBuilder<K, V> newMapBuilder() {
        return new MapBuilder<K, V>();
    }

    public static Map<String, Object> newCommonMap(String key, Object value) {
        return newMap(key, value);
    }

    public static Map<String, Object> newCommonMap(String key1, Object value1, String key2, Object value2) {
        return newMap(key1, value1, key2, value2);
    }

    public static MapBuilder<String, Object> newCommonMapBuilder() {
        return new MapBuilder<String, Object>();
    }

    public static class MapBuilder<K, V> {

        private Map<K, V> map = new HashMap<>();

        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }
    }
}
