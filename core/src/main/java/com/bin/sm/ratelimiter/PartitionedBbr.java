package com.bin.sm.ratelimiter;

import com.bin.sm.internal.collection.CopyOnWriteMap;

import java.util.Map;

public class PartitionedBbr {
    private Bbr main;
    private Map<String,Bbr> partitions = new CopyOnWriteMap<>();


}
