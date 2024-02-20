/*
package com.intuit.library.eXtendGql.selector;

import com.intuit.library.common.GqlNodeContext;
import com.intuit.library.eXtendGql.TuneableSearchData;
import com.intuit.library.eXtendGql.traversal.TraversalObserver;
import com.intuit.library.eXtendGql.traversal.Context;
import com.intuit.library.eXtendGql.traversal.ObserverAction;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SearchObserver implements TraversalObserver {
    private final SearchPathBuilder searchPathBuilder;
    GqlNodeContext searchNode = null;
    private int currentLocationInSearchPath = 0;
    private SelectionDebugData selectionDebugData;

    public SearchObserver(String searchPathStr) {
        this(new SearchPathBuilder(searchPathStr));
    }

    public SearchObserver(SearchPathBuilder searchPathBuilder) {
        this.searchPathBuilder = searchPathBuilder;
    }

    public SearchPathBuilder getSearchPathBuilder() {
        return searchPathBuilder;
    }

    public SelectionDebugData getSelectionDebugData() {
        return selectionDebugData;
    }

    @Override
    public void updateNodeEntry(Node node, Node parentNode, Context context, ObserverAction observerAction) {

        if (node == null) {
            return;
        }
        Map<String, String> contextAttributeMap = new HashMap<>();
        switch (context.getDocumentElementType()) {

            case DIRECTIVE:
                //contextAttributeMap.put(SearchPathElement.DOC_ELEM_TYPE,DocumentElementType.DIRECTIVE.getShortName() );
                contextAttributeMap.put(SearchPathElement.NAME, ((Directive) node).getName());
                break;
            case FIELD:
                Field field = (Field) node;
                contextAttributeMap.put(SearchPathElement.NAME, field.getName());
                String alias = field.getAlias();
                if (!Strings.isNullOrEmpty(alias)) contextAttributeMap.put(SearchPathElement.ALIAS, alias);
                break;
            case OPERATION_DEFINITION:
                String baseName1 = ((OperationDefinition) node).getOperation().name(); // query name
                contextAttributeMap.put(SearchPathElement.NAME, baseName1);
                String name1 = ((OperationDefinition) node).getName();
                if (name1 != null)
                    contextAttributeMap.put(SearchPathElement.OPERATION_NAME, name1);
                else
                    contextAttributeMap.put(SearchPathElement.NAME, baseName1);
                break;
            case MUTATION_DEFINITION:
                String baseName2 = ((OperationDefinition) node).getOperation().name(); //mutation name
                contextAttributeMap.put(SearchPathElement.NAME, baseName2);
                String name2 = ((OperationDefinition) node).getName();
                if (name2 != null)
                    contextAttributeMap.put(SearchPathElement.OPERATION_NAME, name2);
                else
                    contextAttributeMap.put(SearchPathElement.NAME, baseName2);
                break;
            case INLINE_FRAGMENT:
                contextAttributeMap.put(SearchPathElement.NAME, ((InlineFragment) node).getTypeCondition().getName());
                break;
            case FRAGMENT_DEFINITION:
                contextAttributeMap.put(SearchPathElement.NAME, ((FragmentDefinition) node).getName());
                break;
            case FRAGMENT_SPREAD:
                contextAttributeMap.put(SearchPathElement.NAME, ((FragmentSpread) node).getName());
                break;
            case VARIABLE_DEFINITION:
                contextAttributeMap.put(SearchPathElement.NAME, ((VariableDefinition) node).getName());
                break;
            case ARGUMENT:
                Argument argNode = (Argument) node;
                // Value value = argNode.getValue();
                //contextAttributeMap.put(NAME, argNode.getName() );
                contextAttributeMap.put(SearchPathElement.NAME, argNode.getName());

                break;
            default:
                // do nothing
                return;
        }

        SearchPathElementList searchPathElements = this.searchPathBuilder.getPathElements();

        if( context.getLevel() > currentLocationInSearchPath){
            selectionDebugData.addMessage(MessageFormat.format("[\n\tPlease check you path correctness, \n" +
                            "\tThere is no correlation between search path elements and traversal context, " +
                            "\tReason: context.level {0} bigger than currentLocationDuringTraversal {1}" +
                            "\n\tsearchPathElements : \n{2}\n\tCurrent context : {3}]",
                    context.getLevel(),
                    currentLocationInSearchPath,
                    searchPathElements.toString(1),
                    contextAttributeMap));
            return;
        }
        //if current searchPath is equal to the name of the founded node and equal to the node type, by default its field,  and level equal to

        SearchPathElement searchPathElement = searchPathElements.get(currentLocationInSearchPath);

        //      boolean isIncrement = true;

        boolean isEqualVerified = true;

        if (selectionDebugData == null) selectionDebugData = new SelectionDebugData();

        SelectionAttributeMap selectionAttributeMap = searchPathElement.getSelectionAttributeMap();

        Preconditions.checkNotNull(selectionAttributeMap);
        Map<String, String> searchAttributes = selectionAttributeMap.getAttributes();
        Preconditions.checkNotNull(searchAttributes);

        String searchDocType = searchAttributes.get(SearchPathElement.DOC_ELEM_TYPE);
        if (context.getDocumentElementType().getShortName().equals(searchDocType)) {
            for (Map.Entry<String, String> searchAttrbuteEntry : searchAttributes.entrySet()) {

                if (searchAttrbuteEntry.getKey().equals(SearchPathElement.DOC_ELEM_TYPE)) {
                    continue;
                }
                isEqualVerified &= searchAttrbuteEntry.getValue().equalsIgnoreCase(contextAttributeMap.get(searchAttrbuteEntry.getKey()));

                String msg = MessageFormat.format("Verify by searchValue element by key: {0}, value: {1}",
                        searchAttrbuteEntry.getKey(), searchAttrbuteEntry.getValue());
                if (!isEqualVerified) {
                    msg = MessageFormat.format("Failed to {0}", msg);
                }

                selectionDebugData.addMessage(MessageFormat.format("Attribute are equal, " +
                                "Current Position in search path: {0}, Message : {1}",
                        currentLocationInSearchPath, msg));
            }

        } else {
            isEqualVerified = false;

            selectionDebugData.addMessage(MessageFormat.format(
                    "Context and search value does not have " +
                            "the same DocumentElementType: searchPathElement={0}, context={1}",
                    searchDocType, context.getDocumentElementType()));
        }

        if (isEqualVerified) {
            currentLocationInSearchPath++;
        }


        // check leveling



        /// check if currentLocationInSearchPath exceeded to find the entire path
        if (isEqualVerified && currentLocationInSearchPath == searchPathElements.size()) {
            observerAction.setUnregisterObserverPostBack(true);
            searchNode = new GqlNodeContext(node,
                    parentNode,
                    context.getDocumentElementType(),
                    context.getNodeStack(),
                    context.getLevel());
            searchNode.setNodeStack(context.getNodeStack());
            searchNode.setLevel(context.getLevel());

            searchNode.getSearchContext().setSearchPaths(this.searchPathBuilder.getPathElements());
        }
    }

    @Override
    public void updateNodeExit(Node node, Node parentNode, Context context, ObserverAction observerAction) {
    }

    public TuneableSearchData getTuneableSearchData() {
        return searchPathBuilder.getTuneableSearchData();
    }

    public GqlNodeContext getSearchNode() {

        return searchNode;
    }
}
*/
