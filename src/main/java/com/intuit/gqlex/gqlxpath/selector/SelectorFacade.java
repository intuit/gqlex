package com.intuit.gqlex.gqlxpath.selector;

import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.syntax.SyntaxPath;
import com.intuit.gqlex.traversal.GqlTraversal;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectorFacade {

    //private SearchObserver gqlBrowserObserver = null;
    private GqlTraversal traversal = null;

    public GqlNodeContext selectNext(String graphQuery, String searchSyntax) {
       return selectNext(graphQuery, searchSyntax, null);
    }
    public GqlNodeContext selectNext(String graphQuery, SyntaxPath syntaxPath) {
        return selectNext(graphQuery,syntaxPath,null);
    }

    public List<GqlNodeContext> selectMany(String graphQuery, SyntaxPath syntaxPath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkArgument(syntaxPath != null
                && syntaxPath.getPathElements() != null
                && syntaxPath.getPathElements().size() > 0);

        return selectMany(graphQuery, syntaxPath.toString());
    }

    public List<GqlNodeContext> selectMany(Node graphQuery, String searchSyntax) {

        Preconditions.checkNotNull(graphQuery);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchSyntax));

        traversal = new GqlTraversal();
        SearchNodesObserver gqlBrowserObserver = new SearchNodesObserver(searchSyntax);

        traversal.getGqlTraversalObservable().addObserver(gqlBrowserObserver);
        traversal.traverse(graphQuery, gqlBrowserObserver.getTuneableSearchData());

        return gqlBrowserObserver.getSearchNodeList();
    }

    public List<GqlNodeContext> selectMany(String graphQuery, String searchSyntax) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchSyntax));

        traversal = new GqlTraversal();
        SearchNodesObserver gqlBrowserObserver = new SearchNodesObserver(searchSyntax);

        traversal.getGqlTraversalObservable().addObserver(gqlBrowserObserver);
        traversal.traverse(graphQuery, gqlBrowserObserver.getTuneableSearchData());

        List<GqlNodeContext> searchNodeList = gqlBrowserObserver.getSearchNodeList();
        SearchPathBuilder searchPathBuilder = gqlBrowserObserver.getSearchPathBuilder();
        if( searchPathBuilder.isSelectByRange() ){
            SelectionRange selectionRange = searchPathBuilder.getSelectionRange();

            List<GqlNodeContext> searchByRangeNodeList = new ArrayList<>();
            int start = selectionRange.getRangeStart();
            int end = searchNodeList.size();
            if( ! selectionRange.isRangeEndAll() ){
                end = selectionRange.getRangeEnd();
            }
            if( end >= searchNodeList.size()){
                end= searchNodeList.size()-1;
            }
            /*if( start == end){
                end++;
            }*/

            for (int i = start; i <= end; i++) {
                searchByRangeNodeList.add(searchNodeList.get(i));
            }

            return searchByRangeNodeList;
        }else {
            return searchNodeList;
        }
    }

    public GqlNodeContext selectNext(Node graphNode, String searchPath, UUID transactionId) {
        Preconditions.checkNotNull(graphNode);
        Preconditions.checkNotNull(graphNode);

        traversal = new GqlTraversal();

        SearchNodesObserver gqlBrowserObserver = new SearchNodesObserver(searchPath,
                transactionId);

        traversal.getGqlTraversalObservable().addObserver(gqlBrowserObserver);
        traversal.traverse(graphNode, gqlBrowserObserver.getTuneableSearchData());

        return gqlBrowserObserver.getSearchNodeList() != null &&
                gqlBrowserObserver.getSearchNodeList().size() > 0 ?
                gqlBrowserObserver.getSearchNodeList().get(0) : null;
    }

    public GqlNodeContext selectNext(Node graphNode, SyntaxPath syntaxPath, UUID transactionId) {
        Preconditions.checkNotNull(graphNode);
        Preconditions.checkArgument(syntaxPath != null
                && syntaxPath.getPathElements() != null
                && syntaxPath.getPathElements().size() > 0);

        return selectNext(graphNode, syntaxPath.toString(),transactionId);
    }

    public GqlNodeContext selectNext(String graphQuery, SyntaxPath syntaxPath, UUID transactionId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkArgument(syntaxPath != null
                && syntaxPath.getPathElements() != null
                && syntaxPath.getPathElements().size() > 0);

        return selectNext(graphQuery, syntaxPath.toString(),transactionId);
    }

    public GqlNodeContext selectNext(String graphQuery, String searchSyntax, UUID transactionId) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchSyntax));

        traversal = new GqlTraversal();

        SearchNodesObserver gqlBrowserObserver = new SearchNodesObserver(searchSyntax.toString(),
                transactionId);

        traversal.getGqlTraversalObservable().addObserver(gqlBrowserObserver);
        traversal.traverse(graphQuery, gqlBrowserObserver.getTuneableSearchData());

        return gqlBrowserObserver.getSearchNodeList() != null &&
                gqlBrowserObserver.getSearchNodeList().size() > 0 ?
                gqlBrowserObserver.getSearchNodeList().get(0) : null;
    }

    public GqlNodeContext selectSingle(String graphQuery, String searchSyntax) {
        List<GqlNodeContext> nodeContexts = selectMany(graphQuery, searchSyntax);
        if( nodeContexts == null || nodeContexts.isEmpty()){
            return null;
        }

        return nodeContexts.get(0);
    }
    @Deprecated
    public GqlNodeContext selectSingle(Node graphQuery, String searchSyntax) {

        List<GqlNodeContext> nodeContexts = selectMany(graphQuery, searchSyntax);
        if( nodeContexts == null || nodeContexts.isEmpty()){
            return null;
        }

        return nodeContexts.get(0);

        // ---> old code - work as well, but this new class replace it

        /*Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchSyntax));

        traversal = new GqlTraversal();
        gqlBrowserObserver = new SearchObserver(searchSyntax);

        traversal.getGqlTraversalObservable().addObserver(gqlBrowserObserver);
        traversal.traverse(graphQuery, gqlBrowserObserver.getTuneableSearchData());

        return gqlBrowserObserver.getSearchNode();*/
    }

    public GqlNodeContext selectSingle(String graphQuery, SyntaxPath syntaxPath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(graphQuery));
        Preconditions.checkNotNull(syntaxPath);

        List<GqlNodeContext> nodeContexts = selectMany(graphQuery, syntaxPath.toString());
        if( nodeContexts == null || nodeContexts.isEmpty()){
            return null;
        }

        return nodeContexts.get(0);

    }
    public GqlNodeContext selectSingle(Node graphQuery, SyntaxPath syntaxPath) {
        Preconditions.checkNotNull(graphQuery);
        Preconditions.checkArgument(syntaxPath != null
                && syntaxPath.getPathElements() != null
                && syntaxPath.getPathElements().size() > 0);

        return selectSingle(graphQuery, syntaxPath.toString());
    }



    /*public SelectionDebugData getSelectionDebugData() {

        *//*if (gqlBrowserObserver != null) {
            return gqlBrowserObserver.getSelectionDebugData();
        }*//*

        return null;
    }*/



}
