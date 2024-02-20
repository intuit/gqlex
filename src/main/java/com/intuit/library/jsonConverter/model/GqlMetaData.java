package com.intuit.library.jsonConverter.model;

public class GqlMetaData {

    private String operationType;
    private String OperationName;
    private String method;
    private int numberOfToken;
    private int numberOfWhiteSpace;
    private int maxDepth;

    public void setNumberOfToken(int numberOfToken) {
        this.numberOfToken = numberOfToken;
    }

    public int getNumberOfToken() {
        return numberOfToken;
    }

    public void setNumberOfWhiteSpace(int numberOfWhiteSpace) {

        this.numberOfWhiteSpace = numberOfWhiteSpace;
    }

    public int getNumberOfWhiteSpace() {
        return numberOfWhiteSpace;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationName() {
        return OperationName;
    }

    public void setOperationName(String operationName) {
        OperationName = operationName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "GqlMetaData{" +
                "operationType='" + this.getOperationType() + '\'' +
                ", OperationName='" + this.getOperationName() + '\'' +
                ", method='" + this.getMethod() + '\'' +
                ", numberOfToken=" + this.getNumberOfToken() +
                ", numberOfWhiteSpace=" + this.getNumberOfWhiteSpace() +
                ", maxDepth=" + this.getMaxDepth() +
                '}';
    }
}
