package com.bin.sm.loadbalance;

import com.bin.sm.core.Governance;
import com.bin.sm.core.GovernanceContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class P2c {


    //闲置时间的最大容忍值
    private static final long forceGap = TimeUnit.SECONDS.toNanos(3);

    private Map<String,P2cNode> p2cNodes = new HashMap<String,P2cNode>();

    public static final P2c p2c = new P2c();

    public void chooseNode(String address) {
        P2cNode node = p2cNodes.get(address);
        if (node != null) {
            GovernanceContext.setP2cLoadBalance(node);
        }
    }

    public String loadBalancer(List<String> addresses) {

        List<P2cNode> list = addresses.stream().map(address -> p2cNodes.get(address)).toList();
        P2cNode node = pick(list);
        String address = node.getAddress();
        GovernanceContext.setP2cLoadBalance(node);
        return address;
    }


    private P2cNode pick(List<P2cNode> nodes) {

        if (nodes == null || nodes.size() <= 0) {
            throw new IllegalArgumentException("no node!");
        }
        if (nodes.size() == 1) {
            return nodes.getFirst();
        }
        P2cNode pc, upc;

        P2cNode[] randomPair = prePick(nodes);
        P2cNode nodeA = randomPair[0];
        P2cNode nodeB = randomPair[1];
        if (nodeB.weight() > nodeA.weight()) {
            pc = nodeB;
            upc = nodeA;
        } else {
            pc = nodeA;
            upc = nodeB;
        }
        // 如果在forceGap期间从未选择过故障节点，则强制选择一次。利用强制机会触发成功率和延迟的更新
        if (upc.pickElapsed() > forceGap && upc.picked()) {
            pc = upc;
            upc.resetPicked();
        }
        return pc;
    }


    private P2cNode[] prePick(List<P2cNode> nodes) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        P2cNode[] randomPair = new P2cNode[2];
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
