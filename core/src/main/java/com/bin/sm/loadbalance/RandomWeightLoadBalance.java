package com.bin.sm.loadbalance;

import com.bin.sm.context.NodeAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWeightLoadBalance {



    public static <N extends NodeAdapter> N doSelect(List<N> nodeAdapters) {
        int length = nodeAdapters.size();
        // Every invoker has the same weight?
        boolean sameWeight = true;
        // the maxWeight of every invoker, the minWeight = 0 or the maxWeight of the last invoker
        int[] weights = new int[length];
        // The sum of weights
        int totalWeight = 0;
        for (int i = 0; i < length; i++) {
            int weight = nodeAdapters.get(i).getWeight();
            // Sum
            totalWeight += weight;
            // save for later use
            weights[i] = totalWeight;
            if (sameWeight && totalWeight != weight * (i + 1)) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on
            // totalWeight.
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return an invoker based on the random value.
            if (length <= 4) {
                for (int i = 0; i < length; i++) {
                    if (offset < weights[i]) {
                        return nodeAdapters.get(i);
                    }
                }
            } else {
                int i = Arrays.binarySearch(weights, offset);
                if (i < 0) {
                    i = -i - 1;
                } else {
                    while (weights[i + 1] == offset) {
                        i++;
                    }
                    i++;
                }
                return nodeAdapters.get(i);
            }
        }
        return nodeAdapters.get(ThreadLocalRandom.current().nextInt(length));
    }
}
