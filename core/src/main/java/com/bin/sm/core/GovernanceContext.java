package com.bin.sm.core;

import com.bin.sm.context.RpcContext;
import com.bin.sm.loadbalance.P2c;
import com.bin.sm.loadbalance.P2cNode;

public class GovernanceContext {

    private static final ThreadLocal<Boolean> RandomWeight = new ThreadLocal<>();

    private static final ThreadLocal<RpcContext> rpcContext = new ThreadLocal<>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };


    public static void setRandomWeight() {
        RandomWeight.set(true);
    }

    public static boolean isRandomWeight() {
        Boolean b = RandomWeight.get();
        if (b != null) {
            RandomWeight.remove();
            return b;
        }
        return false;
    }



    public static void setP2cLoadBalance(P2cNode node) {
        rpcContext.get().setP2cNode(node);
    }

    public static void isP2cLoadBalance(String address) {
        if (rpcContext.get().getP2cNode() == null) {
            P2c.p2c.chooseNode(address);
        }
    }

    public static RpcContext getRpcContext() {
        return rpcContext.get();
    }

}
