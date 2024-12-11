package com.bin.sm.internal.topk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class MinHeap {

    private int k;
    private PriorityQueue<Item> heap;


    public MinHeap(int k) {
        this.k = k;
        heap=new PriorityQueue<>(((o1, o2) -> o2.count() - o1.count()));
    }


    public Item add(Item val) {

        if (k > heap.size()) {
            heap.offer(val);
            return null;
        }

        Item head = heap.peek();
        if (head == null) return null;

        int min = head.count();
        if (val.count() > min) {
            Item expelled = heap.poll();
            heap.offer(val);
            return expelled;
        }
        return null;
    }


    public void fix(Item targetNode) {
        heap.remove(targetNode);
        heap.add(targetNode);
    }


    public int min() {
        if (heap.size() == 0) return 0;
        return heap.poll().count();
    }


    public Item find(String key) {
        for (Item node : heap) {
            if (Objects.equals(node.key(), key)) {
                return node;
            }
        }

        return null;
    }


    public List<Item> sorted() {
        PriorityQueue<Item> nodes = this.heap;


        List<Item> nodeList = new ArrayList<>(nodes);
        Collections.sort(nodeList, (Comparator.comparingInt(o -> o.count())));

        return nodeList;
    }


    public int size() {
        return heap.size();
    }

    public PriorityQueue<Item> nodes() {
        return heap;
    }
}
