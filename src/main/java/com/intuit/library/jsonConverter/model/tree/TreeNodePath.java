package com.intuit.library.jsonConverter.model.tree;

import com.intuit.library.jsonConverter.model.FieldNameDescriptor;
import com.intuit.library.jsonConverter.model.Gqlable;
import graphql.com.google.common.base.Strings;

import java.util.Arrays;

public class TreeNodePath {
    private  Gqlable node;
    private Class type;
    private FieldNameDescriptor[] path;

    @Override
    public String toString() {
        return "TreeNodePath{" +
                "node=" + node +
                ", type=" + type +
                ", path=" + Arrays.toString(path) +
                ", canonicalPath='" + canonicalPath + '\'' +
                '}';
    }

    public Gqlable getNode() {
        return node;
    }

    public void setNode(Gqlable node) {
        this.node = node;
    }

    public FieldNameDescriptor[] getPath() {
        return path;
    }

    private String canonicalPath  = null;
    public String getCanonicalPath(){
        if(path == null || path.length == 0){
            return null;
        }
        if( Strings.isNullOrEmpty(canonicalPath)) {
            StringBuilder builder = new StringBuilder();

            for (FieldNameDescriptor fieldNameDescriptor : getPath()) {
                builder.append(fieldNameDescriptor.getConcatenatedName());
                builder.append(".");
            }

            builder.append(this.getNode().getFieldNameDescriptor().getConcatenatedName());

            canonicalPath = builder.toString();
        }

        return canonicalPath;
    }

    public void setPath(FieldNameDescriptor[] path) {
        this.path = path;
        canonicalPath = null;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
