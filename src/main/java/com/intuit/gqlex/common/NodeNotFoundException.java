package com.intuit.gqlex.common;

public class NodeNotFoundException extends RuntimeException {
    private final GqlNodeContext nodeContext;
    private final String msg;

    public NodeNotFoundException(GqlNodeContext nodeContext, String msg) {
        this.nodeContext = nodeContext;
        this.msg = msg;
    }

    public GqlNodeContext getNodeContext() {
        return nodeContext;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "NodeNotFoundException{" +
                "nodeContext=" + nodeContext +
                ", msg='" + msg + '\'' +
                '}';
    }
}
