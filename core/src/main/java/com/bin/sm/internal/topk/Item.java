package com.bin.sm.internal.topk;

public class Item {

   private String key;
    private  int count;

    public Item(){}

    public Item(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public String key() {
        return key;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }
}