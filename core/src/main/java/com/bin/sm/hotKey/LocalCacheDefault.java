package com.bin.sm.hotKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalCacheDefault implements LocalCache {

    private final LRUCache<String, Item> cache;
    private final Long startTime;

    public LocalCacheDefault(int cap) {
        cache = new LRUCache<>(cap);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void add(String key, Object value, long ttl) {
        long currentTime = System.currentTimeMillis();
        long expirationTime = ttl + (currentTime - startTime);
        Item item = new Item(expirationTime, value);
        cache.put(key, item);
    }

    @Override
    public Object get(String key) {
        Item item = cache.get(key);
        if (item != null) {
            long currentTime = System.currentTimeMillis();
            if (item.getTtl() > (currentTime - startTime)) {
                return item.getValue();
            }
            cache.remove(key);
        }
        return null;
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public List<Object> list() {
        // 仅用作测试，不推荐正式环境使用
        Set<Map.Entry<String, Item>> entries = cache.entrySet();
        return new ArrayList<>(entries);
    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        public final int cap;

        public LRUCache(int cap) {
            super(cap, 0.75f, true);
            this.cap = cap;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > cap;
        }

    }




    static class Item {
        private Long ttl;
        private Object value;

        public Item(){}
        public Item(long expirationTime, Object value) {
            this.ttl = expirationTime;
            this.value = value;
        }

        public Long getTtl() {
            return ttl;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
