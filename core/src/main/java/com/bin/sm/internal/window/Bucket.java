package com.bin.sm.internal.window;


public class Bucket {
    private double[] points = new double[1];
    private long count = 0;
    private Bucket next;



    public Bucket() {

    }

    public void append(double val) {
        points[0] = val;
        count++;
    }

    public void add(int offset,double val) {
        points[0] += val;
        count++;
    }



    public void reset() {
        points[0] = 0;
        count = 0;
    }

    public Bucket next() {
        return next;
    }

    public void setNext(Bucket bucket) {
        next = bucket;
    }


    public long count() {
        return count;
    }

    public double[] points() {
        return points;
    }
}
