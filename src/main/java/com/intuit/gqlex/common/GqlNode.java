package com.intuit.gqlex.common;

import graphql.language.Node;

public class GqlNode {
    private final Node node;
    private final DocumentElementType type;

    public GqlNode(Node node, DocumentElementType type) {
        this.node = node;
        this.type = type;
    }

    public Node getNode() {
        return node;
    }

    public DocumentElementType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GqlNode{" +
                "node=" + node +
                ", type=" + type +
                '}';
    }
}
