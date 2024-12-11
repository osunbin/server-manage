package com.bin.sm.internal.topk;

import com.bin.sm.util.HashUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class HeavyKeeper {

    private static final int LOOKUP_TABLE = 256;


    private int k;
    private int width;
    private int depth;
    private double decay;
    private List<Double> lookupTable;
    private int minCount;
    private List<List<Bucket>> buckets;
    private MinHeap minHeap;
    private Queue<Item> expelled;
    private long total;
    private Random random = new Random();

    public HeavyKeeper(int k, int width, int depth, double decay, int min) {
        this.buckets = new ArrayList<>(depth);
        for (int i = 0; i < depth; i++) {
            List<Bucket> buckets = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                buckets.add(new Bucket());
            }
            this.buckets.add(i, buckets);

        }
        this.k = k;
        this.width = width;
        this.depth = depth;
        this.decay = decay;
        this.lookupTable = new ArrayList<>(LOOKUP_TABLE);
        this.minHeap = new MinHeap(k);
        this.expelled = new ArrayDeque<>(32);
        this.minCount = min;

        for (int i = 0; i < LOOKUP_TABLE; i++) {
            lookupTable.add(i, Math.pow(decay, i));
        }

    }


    public Queue<Item> expelled() {
        return expelled;
    }

    public List<Item> list() {
        return minHeap.sorted();
    }


    public Result add(String key, int incr) {
        int itemFingerprint = HashUtil.hash(key);
        int maxCount = 0;
        List<List<Bucket>> bucketListList = this.buckets;
        for (int i = 0; i < bucketListList.size(); i++) {


            List<Bucket> bucketList = bucketListList.get(i);
            for (int j = 0; j < bucketList.size(); j++) {
                int bucketNumber = HashUtil.hash(i, key) % width;
                Bucket bucket = bucketList.get(bucketNumber);
                int count = bucket.count;
                int fingerprint = bucket.fingerprint;
                if (count == 0) {
                    bucket.fingerprint = itemFingerprint;
                    bucket.count = incr;
                    maxCount = Math.max(maxCount, incr);
                } else if (fingerprint == itemFingerprint) {
                    bucket.count += incr;
                    maxCount = Math.max(maxCount, bucket.count);
                } else {
                    for (int localIncr = incr; localIncr > 0; localIncr--) {
                        double decay = 0.0;
                        int curCount = bucket.count;
                        if (curCount < LOOKUP_TABLE) {
                            decay = lookupTable.get(curCount);
                        } else {
                            decay = lookupTable.get(LOOKUP_TABLE - 1);
                        }
                        if (random.nextDouble() < decay) {
                            if (--bucket.count == 0) {
                                bucket.fingerprint = itemFingerprint;
                                bucket.count = localIncr;
                                break;
                            }

                        }

                    }
                }

            }
        }

        total += incr;
        if (maxCount < minCount) return new Result("",false);
        int minHeapCount = minHeap.min();
        if (minHeap.size() == k && maxCount < minHeapCount)
            return new Result("",false);

        Item item = minHeap.find(key);
        if (item != null) {
            minHeap.fix(item);
            return new Result("",true);
        }

        String exp = "";
        Item expelled = minHeap.add(new Item(key, maxCount));
        if (expelled != null) {
            expell(expelled);
            exp = expelled.key();
        }

        return new Result(exp,true);
    }


    private void expell(Item item) {
        expelled.add(item);
    }


    public void fading() {
        List<List<Bucket>> bucketLists = this.buckets;
        for (List<Bucket> bucketList : bucketLists) {
            for (Bucket bucket : bucketList) {
                bucket.setCount(bucket.getCount() >> 1);
            }
        }
        PriorityQueue<Item> items = minHeap.nodes();

        for (Item item : items) {
            item.setCount(item.count() >> 1);
        }

        total = total >> 1;
    }

    public static class Result{
        String key;
        boolean hotkey;

        public Result(String key, boolean hotkey) {
            this.key = key;
            this.hotkey = hotkey;
        }

        public String getKey() {
            return key;
        }

        public boolean isHotkey() {
            return hotkey;
        }
    }



    class Bucket {
        int fingerprint;
        int count = 0;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

}