package com.intuit.gqlex.gxpath.selector;

import graphql.com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class SelectionAttributeMap {

    private Map<String, String> attributes = null;

    public void add(String attributeKey, String attributeValue) {
        Preconditions.checkArgument(attributeKey != null);
        Preconditions.checkArgument(attributeValue != null);
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        attributes.put(attributeKey, attributeValue);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "SelectionAttributeMap{" +
                "attributes=" + attributes +
                '}';
    }
}
