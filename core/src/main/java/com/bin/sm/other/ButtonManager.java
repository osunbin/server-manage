package com.bin.sm.other;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ButtonManager {

    Map<String, AtomicBoolean> buttons = new ConcurrentHashMap<>();


    public void init(Map<String, Boolean> buttonMap) {
        buttonMap.forEach((k, v) -> {
            buttons.put(k, new AtomicBoolean(v));
        });
    }

    public void change(String key, boolean value) {
        AtomicBoolean atomicBoolean = buttons.get(key);
        if (atomicBoolean != null) atomicBoolean.set(value);
    }

    public boolean get(String key) {
        AtomicBoolean atomicBoolean = buttons.get(key);
        if (atomicBoolean != null) return atomicBoolean.get();

        buttons.computeIfAbsent(key,
                k -> new AtomicBoolean(false));

        return false;
    }


}
