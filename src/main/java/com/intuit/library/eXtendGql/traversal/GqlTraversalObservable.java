package com.intuit.library.eXtendGql.traversal;

import com.intuit.library.common.GqlNode;
import graphql.com.google.common.base.Preconditions;
import graphql.language.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GqlTraversalObservable {

    private List<TraversalObserver> traversalObservers;

    public boolean notifyObserversNodeEntry(Node node, Node parentNode, Context context) {
        if (traversalObservers == null) {
            return false;
        }

        Preconditions.checkNotNull(context.getNodeStack());

        context.setNodeStack((Stack<GqlNode>) context.getNodeStack().clone());

        List<TraversalObserver> toRemoveList = null;
        for (TraversalObserver traversalObserver : traversalObservers) {
            ObserverAction observerAction = new ObserverAction();

            traversalObserver.updateNodeEntry(node, parentNode, context, observerAction);
            if (observerAction.isUnregisterObserverPostBack()) {
                if (toRemoveList == null) {
                    toRemoveList = new ArrayList<>();
                }

                toRemoveList.add(traversalObserver);
            }

        }

        if (toRemoveList != null) {
            for (TraversalObserver traversalObserver : toRemoveList) {
                removeObserver(traversalObserver);
            }
        }

        return true;
    }


    public boolean notifyObserversNodeExit(Node node, Node parentNode, Context context) {
        if (traversalObservers == null) {
            return false;
        }

        for (TraversalObserver traversalObserver : traversalObservers) {
            ObserverAction observerAction = new ObserverAction();

            traversalObserver.updateNodeExit(node, parentNode, context, observerAction);

            if (observerAction.isUnregisterObserverPostBack()) {
                removeObserver(traversalObserver);
            }
        }

        return true;
    }

    /*public ImmutableList<TraversalObserver> getBrowserObservers() {
        return (ImmutableList<TraversalObserver>) traversalObservers;
    }*/

    public void addObserver(TraversalObserver traversalObserver) {

        if (traversalObservers == null) {
            traversalObservers = new ArrayList<>();
        }

        traversalObservers.add(traversalObserver);
    }

    public void removeObserver(TraversalObserver traversalObserver) {
        if (traversalObservers == null) {
            return;
        }

        if (traversalObserver == null) {
            return;
        }

        traversalObservers.remove(traversalObserver);
    }
}
