package com.bin.sm.circuitbreak.sre;

import com.bin.sm.circuitbreak.CircuitBreaker;
import com.bin.sm.circuitbreak.support.CircuitBreakerStatusEvent;
import com.bin.sm.circuitbreak.CircuitBreakerType;
import com.bin.sm.circuitbreak.support.DefaultCircuitBreakerStatusEvent;
import com.bin.sm.context.NodeInstance;
import com.bin.sm.internal.window.RollingCounter;
import com.bin.sm.internal.window.Bucket;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;


public class Sre implements CircuitBreaker {


    private final AtomicReference<Status> status = new AtomicReference<>(Status.CLOSED);

    private RollingCounter stat;

    /**
     * 减小k将使自适应节流行为更加激进，增大k将使适应性节流行为不那么激进。
     *  [1.5, 2]
     */
    private double k;
    private long request;


   private CircuitBreakerStatusEvent circuitBreakerStatus = DefaultCircuitBreakerStatusEvent.circuitBreakerStatusEvent;;

    public Sre(Config config) {
        int bucket = config.bucket; // 10
        long window = config.window; // 10 * 1000
        long bucketDuration = window / bucket;
        this.stat = new RollingCounter(bucket, bucketDuration);
        this.request = config.request;
        this.k = config.k;
    }

    public NodeInstance nodeInstance() {
        return null;
    }

    public CircuitBreakerType circuitBreakerType(){
        return CircuitBreakerType.SRE;
    }


    // mark request is success
    public void markSuccess() {
        stat.add(1);
    }

    /**
     * mark request is failed
     * 当客户端在本地拒绝请求时，继续添加计数器让
     * 下降率更高
     */
    public void markFailed() {
        stat.add(0);
    }

    @Override
    public void markTimeout() {
        stat.add(0);
    }

    private boolean trueOnProba(double proba) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        return current.nextGaussian() < proba;
    }


    private long[] summary() {
        long[] collect = new long[2];
        stat.reduce(lists -> {
            long total = 0;
            long success = 0;
            for (Bucket bucket : lists) {
                total += bucket.count();
                success += Math.round(Arrays.stream(bucket.points()).sum());
            }
            collect[0] = success;
            collect[1] = total;
            return 0;
        });
        return collect;
    }

    /**
     * 上报 状态 StateOpen StateClosed
     */
    public boolean allow() {
        long[] summary = summary();
        long accepts = summary[0]; // 成功
        long total = summary[1];

        double requests = k * (double) accepts;

        if (total < request || (double) total < requests) {
            // open TO closed
            if (status.compareAndSet(Status.OPEN, Status.CLOSED)) {
                circuitBreakerStatus.openToClose(this);
            }
            return true;
        }
         // closed To Open, total x accepts x  l
        if (status.compareAndSet(Status.CLOSED, Status.OPEN)) {
            circuitBreakerStatus.closeToOpen(this,String.format("total %s accepts %s k %s",total,accepts,k));
        }
        // success=60 (100 - 90)/(100+1)
        double dr = Math.max(0, ((double) total - requests) / (double) (total + 1));

        if (dr <= 0) {
            return true;
        }
        // 随机产生0.0-1.0之间的随机数与上面计算出来的熔断概率相比较
        // 如果随机数比熔断概率小则进行熔断
        boolean drop = trueOnProba(dr);
        if (drop) {
            return false;
        }
        return true;
    }


    public static class Config {
        public  double k = 1.2;
        public  long request = 80;
        public int bucket = 10;
        public  long window = 10 * 1000; // 3s
    }
}