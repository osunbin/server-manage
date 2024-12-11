package com.bin.sm.loadbalance;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class P2cNode {



    // 衰减系数  “成本”的平均寿命，在Tau*ln(2)之后达到半衰期
    private static final long tau = TimeUnit.MILLISECONDS.toNanos(600);

    //惩罚值  如果没有收集统计数据，我们会在端点上添加一个很大的滞后惩罚
    // private static final long penalty = 250_000_000_000L;
    private static final long penalty = TimeUnit.MICROSECONDS.toNanos(100);
    // client统计数据
    protected final AtomicLong lag = new AtomicLong(0); //加权移动平均算法计算出的请求延迟度
    protected final AtomicLong success = new AtomicLong(1000); // 加权移动平均算法计算出的请求成功率（只记录grpc内部错误，比如context deadline）
    protected final AtomicLong inflight = new AtomicLong(1); // 当前客户端正在发送并等待response的请求数（pending request）
    protected final AtomicDouble svrCPU = new AtomicDouble(0.5); //对应服务端的CPU使用率

    protected final AtomicLong[] inflights = new AtomicLong[200];

    {
        for (int i = 0; i < inflights.length; i++) {
            inflights[i] = new AtomicLong();
        }

    }

    // 最近一次resp时间戳
    protected final AtomicLong stamp = new AtomicLong();

    protected final AtomicLong reqs = new AtomicLong(0);
    // 最近被pick的时间戳，利用该值可以统计被选中后，一次请求的耗时
    protected final AtomicLong lastPick = new AtomicLong(0);

    private final String address;
    protected int weight;
    protected long  startTime;
    protected long warmUpTime = TimeUnit.MINUTES.toSeconds(30);
    protected AtomicReference<CachedWeight> cachedWeight = new AtomicReference<>();

    private AtomicLong picked = new AtomicLong(0);


    public P2cNode(String address) {
        this.address = address;
        this.weight = 1;
    }

    public P2cNode(String address,int weight) {
        this.weight = weight;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public long health() {
        return success.get();
    }

    boolean valid() {
        return health() > 500 && svrCPU.get() < 0.9;
    }

    public double load() {
        long now = System.nanoTime();
        long avgLag = lag.get();
        long predict = predict(avgLag, now);

        if (avgLag == 0) {
            // penalty是节点刚启动时没有数据时的惩罚值
            return penalty * inflight.get();
        }
        if (predict > avgLag) {
            avgLag = predict;
        }
        // 增加5ms以消除不同区域之间的延迟差距
        avgLag += TimeUnit.MILLISECONDS.toNanos(5);
        avgLag = (long) (Math.sqrt((double) avgLag));
        double cpu = svrCPU.get();
        double load = cpu * avgLag * (inflight.get() + 1);
        return load;
    }


    public long predict(long avgLag, long now) {
        long total = 0;
        int totalNum = 0;
        int slowNum = 0;
        for (int i = 0; i < inflights.length; i++) {
            long start = inflights[i].get();
            if (start != 0) {
                totalNum++;
                long lag = now - start;
                if (lag > avgLag) { // 请求耗时
                    slowNum++;
                    total += lag;
                }
            }
        }
        long predict = 0;
        if (slowNum >= (totalNum / 2 + 1)) {
            predict = total / (long) slowNum;
        }
        return predict;
    }


    public P2cCallBack pick() {
        long start = System.nanoTime();
        lastPick.set(start);
        inflight.incrementAndGet();
        long reqTotal = reqs.incrementAndGet();
        int slot = (int) (reqTotal % 200);

        boolean swapped = inflights[slot].compareAndSet(0, start);

        return (cpu, error) -> {
            if (swapped) {
                inflights[slot].compareAndExchange(start, 0);
            }
            inflight.decrementAndGet();
            long now = System.nanoTime();
            long lastStamp = stamp.getAndSet(now);
            long td = now - lastStamp;
            if (td < 0) {
                td = 0;
            }

            double w = Math.exp((double) -td / (double) tau);

            long currLag = now - start;
            if (currLag < 0) {
                currLag = 0;
            }
            long oldLag = lag.get();
            if (oldLag == 0) {
                w = 0.0;
            }

            currLag = (int) ((double) oldLag * w + (double) currLag * (1.0 - w));
            lag.set(currLag);

            int currSuccess = error ? 0 : 1000;

            long oldSuc = success.get();

            //计算指数加权移动平均成功率
            currSuccess = (int) ((double) oldSuc * w + (double) currSuccess * (1.0 - w));
            this.success.set(currSuccess); //更新

            if (cpu > 0) {
                this.svrCPU.set(cpu);
            }
        };
    }

    public double weight() {
        long now = System.nanoTime();
        double score = 0.0;
        CachedWeight cached = cachedWeight.get();
        if (cached == null || (now - cached.updateAt >
                TimeUnit.MILLISECONDS.toNanos(5))) {
            long health = health();
            double load = load();

            score = load / ((double) health * getWeight());
            CachedWeight newCache = new CachedWeight();
            newCache.updateAt = now;
            newCache.value = score;
            cachedWeight.set(newCache);
        } else {
            score = cached.value;
        }

        return score;
    }


    public long pickElapsed() {
        return System.nanoTime() - lastPick.get();
    }

    private double getWeight() {
        if (startTime <= 0) {
            return weight;
        }
        long currSeconds = System.currentTimeMillis() / 1000;
        int duration = (int) (currSeconds - startTime);
        return  (weight * ((double) duration / warmUpTime));
    }


    public boolean picked() {
       return picked.compareAndSet(0,1);
    }


    public void resetPicked() {
        picked.set(0);
    }

    final class CachedWeight {
        double value;
        long updateAt;
    }
}
