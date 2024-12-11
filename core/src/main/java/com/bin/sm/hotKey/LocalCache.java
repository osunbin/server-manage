package com.bin.sm.hotKey;

import java.util.List;

public interface LocalCache {

    void add(String key, Object value, long ttl);

    Object get(String key);

    void remove(String key);

    List<Object> list();
}
