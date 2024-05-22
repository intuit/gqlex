package com.intuit.library.jsonConverter.model;

public class GqlValidation {
    private boolean isChainQuery;
    private boolean isVoidQuery;

    public GqlValidation() {
    }

    public boolean isChainQuery() {
        return isChainQuery;
    }

    public void setChainQuery(boolean chainQuery) {
        isChainQuery = chainQuery;
    }

    public boolean isVoidQuery() {
        return isVoidQuery;
    }

    public void setVoidQuery(boolean voidQuery) {
        isVoidQuery = voidQuery;
    }
}
