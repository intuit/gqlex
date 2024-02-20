package com.intuit.library.eXtendGql.traversal;

import com.intuit.library.common.GqlNode;
import com.intuit.library.common.DocumentElementType;

import java.util.Stack;

public class Context {
    private final int level;
    private Stack<GqlNode> nodeStack;

    private final DocumentElementType documentElementType;

    public Context(DocumentElementType documentElementType, int level, Stack<GqlNode> nodeStack) {
        this.documentElementType = documentElementType;

        this.level = level;
        this.nodeStack = nodeStack;
    }

    public void setNodeStack(Stack<GqlNode> nodeStack) {
        this.nodeStack = nodeStack;
    }

    public Stack<GqlNode> getNodeStack() {
        return nodeStack;
    }

    public DocumentElementType getDocumentElementType() {
        return documentElementType;
    }

    /*public void setDocumentElementType(DocumentElementType documentElementType) {
        this.documentElementType = documentElementType;
    }*/

    public int getLevel() {
        return level;
    }

   /* public void setLevel(int level) {
        this.level = level;
    }*/

    @Override
    public String toString() {
        return "Context{" +
                "level=" + level +
                ", documentElementType=" + documentElementType +
                '}';
    }
}
