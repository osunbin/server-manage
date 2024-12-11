package com.bin.sm.internal.window;


import java.util.ArrayList;
import java.util.List;


public class Window{
    private Bucket[] buckets;
    private int size;


    public Window(int windowSize) {
        size = windowSize;
        buckets = new Bucket[size];
        for (int i = 0; i < size; i++) {
            buckets[i] = new Bucket();
        }

        for (int offset = 0; offset < size; offset++) {
            int nextOffset = offset + 1;
            if (nextOffset == size)
                nextOffset = 0;
            buckets[offset].setNext(buckets[nextOffset]);
        }
    }


    public void resetWindow() {
        for (Bucket bucket : buckets)
            bucket.reset();

    }

    private void resetBuckets(int offset) {
        buckets[offset % size].reset();
    }

    public void resetBuckets(int offset, int count) {
        for (int i = 0; i < count; i++)
            resetBuckets(offset + i);
    }


    public void append(int offset, double val) {
        buckets[offset % size].append(val);
    }

    public void add(int offset, double val) {
        offset %= size;
        if (buckets[offset].count() == 0) {
            buckets[offset].append(val);
        } else {
            buckets[offset].add(0, val);
        }
    }

    public Bucket bucket(int offset) {
        return buckets[offset%size];
    }

    public int size() {
        return size;
    }


    public List<Bucket> iterator(int offset,int count) {
        List<Bucket> list = new ArrayList<>();
        for (int i = offset%size ;i < count;i++) {
            list.add(buckets[i]);
        }
        return list;
    }

}
