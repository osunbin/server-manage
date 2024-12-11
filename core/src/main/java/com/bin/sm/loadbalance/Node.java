package com.bin.sm.loadbalance;

import java.util.concurrent.atomic.AtomicLong;

public class Node {
 
    //惩罚值
    private static final long penalty = 250_000_000_000L; //单位：纳秒（250s）
    //衰减系数
    private static final long tau = 600_000_000L; //单位：纳秒（600ms）
 
    protected final String host;
    protected final int weight; //权重
 
    // client统计数据
    protected final AtomicLong lag = new AtomicLong(); //加权移动平均算法计算出的请求延迟度
    protected final AtomicLong success = new AtomicLong(1000); // 加权移动平均算法计算出的请求成功率（只记录grpc内部错误，比如context deadline）
    protected final AtomicLong inflight = new AtomicLong(1); // 当前客户端正在发送并等待response的请求数（pending request）
    protected final AtomicLong svrCPU = new AtomicLong(500); //对应服务端的CPU使用率
 
    // 最近一次resp时间戳
    protected final AtomicLong stamp = new AtomicLong();
    // 最近被pick的时间戳，利用该值可以统计被选中后，一次请求的耗时
    protected final AtomicLong pick = new AtomicLong();
 
    public Node(String host, int weight) {
        this.host = host;
        this.weight = weight;
    }
 
    boolean valid() {
        return health() > 500 && svrCPU.get() < 900;
    }
 
    long health() {
        return success.get(); //成功率
    }
 
    long load() {
        long lag = (long) (Math.sqrt((double) this.lag.get()) + 1);
        long load = this.svrCPU.get() * lag * this.inflight.get(); //根据cpu使用率、延迟率、拥塞度计算出负载率
        if (load == 0) {
            // penalty是初始化没有数据时的惩罚值，默认为1e9 * 250
            load = penalty;
        }
        return load;
    }
 
    //被pick后，完成请求后触发逻辑
    public void responseTrigger(long pickTime, long cpu, boolean error) {
        this.inflight.decrementAndGet();
        long now = System.nanoTime();
        long stamp = this.stamp.getAndSet(now);
        long td = now - stamp; //计算距离上次response的时间差，节点本身闲置越久，这个值越大
        if (td < 0) {
            td = 0;
        }
        //实时计算β值，利用衰减函数计算，公式为：β = e^(-t/k)，相比前文给出的衰减公式这里是按照k值的反比计算的，即k值和β值成正比
        double w = Math.exp((double) -td / (double) tau);
 
        long lag = now - pickTime; //实际耗时
        if (lag < 0) {
            lag = 0;
        }
        long oldLag = this.lag.get();
        if (oldLag == 0) {
            w = 0;
        }
 
        //计算指数加权移动平均响应时间
        lag = (int) ((double) oldLag * w + (double) lag * (1.0 - w));
        this.lag.set(lag); //更新
 
        int success = error ? 0 : 1000;
        //计算指数加权移动平均成功率
        success = (int) ((double) this.success.get() * w + (double) success * (1.0 - w));
        this.success.set(success); //更新
 
        //更新本次请求服务端返回的cpu使用率
        if (cpu > 0) {
            this.svrCPU.set(cpu);
        }
    }
}