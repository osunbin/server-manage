package com.bin.sm.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 羊群效应->在分布式系统中，当某个应用节点出现问题时，
 * 其他节点会自动转移流量，导致流量过度集中到少数节点上，最终导致节点崩溃。
 * 指标->
 * 节点最近500ms内cpu使用率
 * 当前客户端发送并等待response的请求数(拥塞度)
 * 接口延迟(指数加权移动平均值)
 * 请求成功率(指数加权移动平均值)
 * 节点权重(定值)
 * <p>
 * 流程->
 * 算法通过随机选择两个node选择优胜者来避免羊群效应，并通过ewma尽量获取服务端的实时状态。
 * 服务端： 服务端获取最近500ms内的CPU使用率（需要将cgroup设置的限制考虑进去，并除于CPU核心数），
 * 并将CPU使用率乘与1000后塞入每次rpc请求中的的Trailer中夹带返回： cpu_usage uint64 encoded with string cpu_usage : 1000
 * 客户端： 主要参数：
 * weight:人工配置
 * server_cpu：通过每次请求中服务端塞在trailer中的cpu_usage拿到服务端最近500ms内的cpu使用率(rpc埋点方式获取)
 * inflight：当前客户端正在发送并等待response的请求数（pending request）
 * latency: 加权移动平均算法计算出的接口延迟   sum(time)/sum(count) 总耗时/总请求次数
 * client_success:加权移动平均算法计算出的请求成功率（只记录rpc内部错误）
 */
public class P2CLoadBalancer {


    //闲置时间的最大容忍值
    private static final long forceGap = 3000_000_000L; //单位：纳秒（3s）


    private final List<Node> nodes; //保存了参与lb的节点集合

    public P2CLoadBalancer(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Node pick() { //外界给入start，值为当前时间，resp后应给recycle传同样的值



        Node pc, upc;
        if (nodes == null || nodes.size() <= 0) {
            throw new IllegalArgumentException("no node!");
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        Node[] randomPair = prePick();

        /**
         * 这里根据各自当前指标，计算出谁更合适被pick
         * 计算方式：
         *        nodeA.load                           nodeB.load
         * ----------------------------   :   ----------------------------
         * nodeA.health * nodeA.weight        nodeB.health * nodeB.weight
         *
         * health和weight都是提权用的，而load是降权用的，所以用load除以heal和weight的乘积，计算出的值越大，越不容易被pick
         *
         */
        if (randomPair[0].load() * randomPair[1].health() * randomPair[1].weight >
                randomPair[1].load() * randomPair[0].health() * randomPair[0].weight) {
            pc = randomPair[1];
            upc = randomPair[0];
        } else {
            pc = randomPair[0];
            upc = randomPair[1];
        }
        long start = System.nanoTime();
        // 如果落选的节点，在forceGap期间内没有被选中一次，那么强制选中一次，利用强制的机会，来触发成功率、延迟的衰减
        long pick = upc.pick.get();
        if ((start - pick) > forceGap && upc.pick.compareAndSet(pick, start)) {
            pc = upc; //强制选中
        }

        // 节点未发生切换才更新pick时间
        if (pc != upc) {
            pc.pick.set(start);
        }
        pc.inflight.incrementAndGet();

        return pc;
    }

    //pick出去后，等来了response后，应触发该方法
    public void recycle(Node node, long pickTime, long cpu, boolean error) {
        node.responseTrigger(pickTime, cpu, error);
    }

    // 随机选择俩节点
    public Node[] prePick() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Node[] randomPair = new Node[2];
        for (int i = 0; i < 3; i++) {
            int a = random.nextInt(nodes.size());
            int b = random.nextInt(nodes.size() - 1);
            if (b >= a) {
                b += 1; //防止随机出的节点相同
            }
            randomPair[0] = nodes.get(a);
            randomPair[1] = nodes.get(b);
            if (randomPair[0].valid() || randomPair[1].valid()) {
                break;
            }
        }

        return randomPair;
    }
}