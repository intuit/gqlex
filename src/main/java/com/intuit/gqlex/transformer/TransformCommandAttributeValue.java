package com.intuit.gqlex.transformer;

public class TransformCommandAttributeValue {
    private Object value;
    private String classValueType;

    public TransformCommandAttributeValue(Object value, String classValueType) {
        this.value = value;
        this.classValueType = classValueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getClassValueType() {
        return classValueType;
    }

    public void setClassValueType(String classValueType) {
        this.classValueType = classValueType;
    }
}
