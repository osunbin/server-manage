package com.bin.sm.internal.window;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface ReduceBucket {


    double reduce(List<Bucket> buckets);



     static double sum(List<Bucket> buckets) {
       return buckets.stream().mapToDouble(b -> Arrays.stream(b.points()).sum()).sum();
    }


     static double avg(List<Bucket> buckets) {
       return sum(buckets) / buckets.size();
    }

     static double min(List<Bucket> buckets) {
        Optional<Double> min = buckets.stream().
                map(b -> Arrays.stream(b.points()).min().getAsDouble())
                .min(Comparator.comparing(Double::doubleValue));
       return min.get();
    }

     static double max(List<Bucket> buckets) {
        Optional<Double> max = buckets.stream().
                map(b -> Arrays.stream(b.points()).max().getAsDouble())
                .max(Comparator.comparing(Double::doubleValue));
        return max.get();
    }



}
