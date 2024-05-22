package com.intuit.gqlex.common;

import com.intuit.gqlex.gqlxpath.selector.SearchPathElement;

import java.util.LinkedList;

public class SearchContext {
    private LinkedList<SearchPathElement> pathElements;

    /*public LinkedList<SearchPathElement> getPathElements() {
        return pathElements;
    }*/

    public void setSearchPaths(LinkedList<SearchPathElement> pathElements) {

        this.pathElements = pathElements;
    }

    @Override
    public String toString() {
        return "SearchContext{" +
                "pathElements=" + pathElements +
                '}';
    }
}
