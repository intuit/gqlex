package com.intuit.gqlex.traversal;

public class StringBuilderElem {
    private String name;
    private int depth;

    public StringBuilderElem(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "StringBuilderElem{" +
                "name='" + name + '\'' +
                ", depth=" + depth +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepth() {
        return depth;
    }

    /*public void setDepth(int depth) {
        this.depth = depth;
    }*/
}
