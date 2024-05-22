package com.intuit.gqlex.traversal;

public class ObserverAction {
    private boolean unregisterObserverPostBack = false;

    public boolean isUnregisterObserverPostBack() {
        return unregisterObserverPostBack;
    }

    public void setUnregisterObserverPostBack(boolean unregisterObserverPostBack) {
        this.unregisterObserverPostBack = unregisterObserverPostBack;
    }
}
