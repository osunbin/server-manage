package com.bin.sm.context;

import com.bin.sm.loadbalance.P2cCallBack;
import com.bin.sm.loadbalance.P2cNode;

public class RpcContext {


    private P2cNode p2cNode;

    private NodeInstance  nodeInstance;

    private String appName;

    private String funcName;


    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setP2cNode(P2cNode p2cNode) {
        this.p2cNode = p2cNode;
    }

    public P2cNode getP2cNode() {
        return p2cNode;
    }


    public void clear(){

    }
}
