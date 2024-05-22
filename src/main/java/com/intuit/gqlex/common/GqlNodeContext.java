package com.intuit.gqlex.common;

import graphql.language.Node;

import java.util.Stack;

public class GqlNodeContext/* implements Cloneable*/{

    private final Node parentNode;

    private GqlNode node;

    private int level;
    private Stack<GqlNode> nodeStack;

    private SearchContext searchContext;

    public SearchContext getSearchContext() {
        return searchContext;
    }

    /*@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }*/

    public GqlNodeContext(Node node, Node parentNode, DocumentElementType documentElementType, Stack<GqlNode> nodeStack, int level) {
        this.nodeStack = nodeStack;
        this.level = level;
        this.node = new GqlNode(node,documentElementType);
        this.parentNode = parentNode;

        searchContext = new SearchContext();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setNodeStack(Stack<GqlNode> nodeStack) {
        this.nodeStack = nodeStack;
    }

    public int getLevel() {
        return level;
    }

    public Stack<GqlNode> getNodeStack() {
        return nodeStack;
    }

    public void setNode(GqlNode node) {
        this.node = node;
    }

    public GqlNodeContext(Node node, Node parentNode, DocumentElementType type) {
        this(node,parentNode,type, null, 0);
    }

    public Node getNode() {
        return this.node.getNode();
    }

    public DocumentElementType getType() {
        return this.node.getType();
    }

    public Node getParentNode() {
        return parentNode;
    }

    @Override
    public String toString() {
        return "GqlNodeContext{" +
                "parentNode=" + parentNode +
                ", node=" + node +
                ", level=" + level +
                ", nodeStack=" + nodeStack +
                ", searchContext=" + searchContext +
                '}';
    }

    public String toShortString() {
        return "GqlNodeContext{" +
                "{ node=" + node + "}" +
                "{ level=" + level + "}" +
                "{ searchContext=" + searchContext + "}"+
                '}';
    }
}
