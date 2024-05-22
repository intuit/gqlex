package com.intuit.gqlex.gqlxpath.selector;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.common.GqlNode;
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.TuneableSearchData;
import com.intuit.gqlex.transformer.GeneratedComment;
import com.intuit.gqlex.traversal.Context;
import com.intuit.gqlex.traversal.ObserverAction;
import com.intuit.gqlex.traversal.TraversalObserver;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.Node;
import graphql.language.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SearchNodesObserver implements TraversalObserver {
    private UUID transactionId;
    private SearchPathBuilder searchPathBuilder;

    public SearchPathBuilder getSearchPathBuilder() {
        return searchPathBuilder;
    }

    private int currentLocationInSearchPath = 0;

    public SearchNodesObserver(String searchPathStr) {
        this(new SearchPathBuilder(searchPathStr));
    }

    public SearchNodesObserver(String searchPathStr, UUID transactionId
    ) {
        this(searchPathStr);

        this.transactionId = transactionId;
    }

    class ExpectedSearchAttribute {
        String name;

        String operationName;

        String alias;
        String type;

        private Boolean isOperation = false;
        private Boolean isField = false;
        private Boolean isArgument = false;
        private Boolean isQuery = false;
        private Boolean isMutation = false;
        private Boolean isDirective = false;
        private Boolean isFragement = false;
        private Boolean isInlineFragment = false;
        private Boolean isVariable = false;

        public ExpectedSearchAttribute(SearchPathBuilder searchPathBuilder) {
            Preconditions.checkNotNull(searchPathBuilder);

            SearchPathElementList searchPathElements = searchPathBuilder.getPathElements();
            Preconditions.checkState(searchPathElements != null && searchPathElements.size() > 0);

            SearchPathElement searchPathElement = searchPathElements.get(searchPathElements.size() - 1);

            name = searchPathElement.getName();
            operationName = searchPathElement.getOperationName();
            alias = searchPathElement.getAlias();
            type = searchPathElement.getType();

            switch (DocumentElementType.getByShortName(type)){

                /*case DOCUMENT:
                    break;*/
                case DIRECTIVE:
                    isDirective = true;
                    break;
                case FIELD:
                    isField = true;
                    break;
                case MUTATION_DEFINITION:
                    isMutation = true;
                    break;
                case OPERATION_DEFINITION:
                    isOperation = true;
                    break;
                case INLINE_FRAGMENT:
                    isInlineFragment  =true;
                    break;
                case FRAGMENT_DEFINITION:
                    isFragement = true;
                    break;
                /*case FRAGMENT_SPREAD:
                    break;*/
                case VARIABLE_DEFINITION:
                    isVariable = true;
                    break;
                case ARGUMENT:
                    isArgument = true;
                    break;
                /*case ARGUMENTS:
                    break;
                case SELECTION_SET:
                    break;
                case VARIABLE_DEFINITIONS:
                    break;
                case DIRECTIVES:
                    break;
                case DEFINITIONS:
                    break;
                case DEFINITION:
                    break;
                case SELECTION:
                    break;*/
            }
        }

        public String getOperationName() {
            return operationName;
        }

        public String getAlias() {
            return alias;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

       /* public Boolean getOperation() {
            return isOperation;
        }*/

        public Boolean getField() {
            return isField;
        }

        public Boolean getArgument() {
            return isArgument;
        }

        public Boolean getQuery() {
            return isQuery;
        }

        public Boolean getMutation() {
            return isMutation;
        }

        public Boolean getDirective() {
            return isDirective;
        }

        /*public Boolean getFragement() {
            return isFragement;
        }

        public Boolean getInlineFragment() {
            return isInlineFragment;
        }

        public Boolean getVariable() {
            return isVariable;
        }*/
    }

    private ExpectedSearchAttribute expectedSearchAttribute = null;
    public SearchNodesObserver(SearchPathBuilder searchPathBuilder) {
        this.searchPathBuilder = searchPathBuilder;
        expectedSearchAttribute = new ExpectedSearchAttribute(searchPathBuilder);
    }

    public List<GqlNodeContext> getSearchNodeList() {
        return searchNodesResultArray;
    }

    class NodeDescriptor{
        String actualName = null;
        String actualAlias = null;
        String actualOperationName = null;

        public String getActualName() {
            return actualName;
        }

        public void setActualName(String actualName) {
            this.actualName = actualName;
        }

        public String getActualAlias() {
            return actualAlias;
        }

        public void setActualAlias(String actualAlias) {
            this.actualAlias = actualAlias;
        }

        public String getActualOperationName() {
            return actualOperationName;
        }

        public void setActualOperationName(String actualOperationName) {
            this.actualOperationName = actualOperationName;
        }
    }
    private List<GqlNodeContext> searchNodesResultArray = new ArrayList<>();
    @Override
    public void updateNodeEntry(graphql.language.Node node,
                                graphql.language.Node parentNode,
                                Context context,
                                ObserverAction observerAction) {
        if (node == null) {
            return;
        }

        Map<String, String> contextAttributeMap = new HashMap<>();
        if (extractActualNodeContext(node, context, contextAttributeMap)) return;

        boolean isMatch = true;
        if(isEqual(contextAttributeMap)){

            Stack<GqlNode> actualNodeStack = context.getNodeStack();
            SearchPathElementList searchPathElements = this.searchPathBuilder.getPathElements();

            for (int i = searchPathElements.size()-1; i >=0; i--) {
                SearchPathElement expectedSearchPathElement = searchPathElements.get(i);

                GqlNode actualNode = getActualGqlNodeFromStack(actualNodeStack);;
                NodeDescriptor actualNodeDescriptor = extractNextActualNodeDescriptor(actualNode);

                SearchPathElement nextExpectedSearchPathElement = null;

                 /*
                if expected =  any
                    - need to skip all the actual stack until the actual task is equal to the next expected value.

                        - if the actual stack read and left no elements in the stack
                               ->  if isMatch found, take this node
                               else not found, continue , no match
                        - if the actual stack and
                                -> element found that equal to next-expected
                                        -> stop 'any' state
                                             -> keep compare values expected vs. actual as usual
                                -
                 */
                if( expectedSearchPathElement.isAnyValue()){

                    int nextExpectedIndex = i-1;

                    if( nextExpectedIndex >= 0 ) {

                        nextExpectedSearchPathElement = searchPathElements.get(nextExpectedIndex);

                        int tempNextExpectedStuckIndex = currentActualStackIndex;

                        boolean isStackPopEndedFoundNoMatchAgainstExpected = false;
                        while (!nextExpectedSearchPathElement.getName().equalsIgnoreCase(actualNodeDescriptor.getActualName()) ||
                                !nextExpectedSearchPathElement.getType().equalsIgnoreCase(actualNode.getType().getShortName())) {

                            if( tempNextExpectedStuckIndex == -1){
                                isStackPopEndedFoundNoMatchAgainstExpected  =true;
                                break;
                            }
                            Pair<GqlNode, Integer> nextGqlNodeFromStack = getNextGqlNodeFromStack(actualNodeStack, tempNextExpectedStuckIndex);

                            actualNode = nextGqlNodeFromStack.getLeft();
                            tempNextExpectedStuckIndex = nextGqlNodeFromStack.getRight();
                            actualNodeDescriptor = extractNextActualNodeDescriptor(actualNode);

                        }

                        if( isStackPopEndedFoundNoMatchAgainstExpected){
                            isMatch &= false;
                            break;
                        }

                        expectedSearchPathElement = nextExpectedSearchPathElement;
                        currentActualStackIndex = tempNextExpectedStuckIndex - 1;
                        i= nextExpectedIndex;
                    }else{
                        // no more elements in the expected search element
                        isMatch &= true;
                        // any set with no more elements in the stack
                        break;
                    }

                }

                if( actualNode.getType().equals(DocumentElementType.OPERATION_DEFINITION) || actualNode.getType().equals(DocumentElementType.MUTATION_DEFINITION)){
                    if ( expectedSearchPathElement.getType().equals(actualNode.getType().getShortName())) {

                        if(     actualNodeDescriptor.getActualOperationName() != null && expectedSearchPathElement.getOperationName() != null &&
                                expectedSearchPathElement.getOperationName().equalsIgnoreCase( actualNodeDescriptor.getActualOperationName()) &&
                                actualNodeDescriptor.getActualName() != null && expectedSearchPathElement.getName() != null && expectedSearchPathElement.getName().equalsIgnoreCase( actualNodeDescriptor.getActualName() )){
                            isMatch &= true;
                        }else if(     actualNodeDescriptor.getActualOperationName() != null &&
                                actualNodeDescriptor.getActualName() == null &&
                                expectedSearchPathElement.getOperationName() != null &&
                                expectedSearchPathElement.getOperationName().equalsIgnoreCase( actualNodeDescriptor.getActualOperationName())){
                            isMatch &= true;
                        }else if(     actualNodeDescriptor.getActualOperationName() == null &&
                                actualNodeDescriptor.getActualName() != null &&
                                expectedSearchPathElement.getName() != null &&
                                expectedSearchPathElement.getName().equalsIgnoreCase(actualNodeDescriptor.getActualName())

                        ){
                            isMatch &= true;
                        }else{
                            isMatch &= false;
                        }
                    } else {
                        isMatch &= false;
                    }
                }else{
                    if (expectedSearchPathElement.getName().equalsIgnoreCase(actualNodeDescriptor.getActualName()) &&
                            expectedSearchPathElement.getType().equalsIgnoreCase(actualNode.getType().getShortName())) {
                        isMatch &= true;
                    } else {
                        isMatch &= false;
                    }
                }

                if( ! isMatch){
                    break;
                }
            }

            currentActualStackIndex = -1;


                // found
            if( isMatch) {
              //  matchCount++;

                GqlNodeContext gqlNodeContext = new GqlNodeContext(node,
                        parentNode,
                        context.getDocumentElementType(),
                        context.getNodeStack(),
                        context.getLevel());
                gqlNodeContext.setNodeStack(context.getNodeStack());
                gqlNodeContext.setLevel(context.getLevel());

                gqlNodeContext.getSearchContext().setSearchPaths(this.searchPathBuilder.getPathElements());

                if( this.transactionId != null ){
                    searchNodesResultArray.add(gqlNodeContext);
                    observerAction.setUnregisterObserverPostBack(true);
                }else {
                    searchNodesResultArray.add(gqlNodeContext);
                }

                if( ! this.searchPathBuilder.isMultiSelect()){
                    observerAction.setUnregisterObserverPostBack(true);
                }
                /*if( iterateIndex != -1 ){

                    if( matchCount-1 == iterateIndex) {
                        searchNodesResultArray.add(gqlNodeContext);
                        observerAction.setUnregisterObserverPostBack(true);
                    }

                }else {
                    searchNodesResultArray.add(gqlNodeContext);
                }*/
            }
        }else{

        }
    }

    private NodeDescriptor extractNextActualNodeDescriptor(GqlNode actualNode) {
        NodeDescriptor nodeDescriptor = new NodeDescriptor();

        switch (actualNode.getType()) {

            case DOCUMENT:
                break;
            case DIRECTIVE:
                Directive directive = (Directive) actualNode.getNode();
                nodeDescriptor.setActualName( directive.getName() );
                break;
            case FIELD:
                Field field = (Field) actualNode.getNode();
                nodeDescriptor.setActualName( field.getName());
                nodeDescriptor.setActualAlias( field.getAlias() );
                break;
            case MUTATION_DEFINITION:
            case OPERATION_DEFINITION:
                OperationDefinition oper = (OperationDefinition) actualNode.getNode();
                nodeDescriptor.setActualOperationName( oper.getName() );
                nodeDescriptor.setActualName( oper.getOperation().name());
                break;
            case INLINE_FRAGMENT:
                InlineFragment inlineFragment = (InlineFragment) actualNode.getNode();
                nodeDescriptor.setActualName( inlineFragment.getTypeCondition().getName());
                break;
            case FRAGMENT_DEFINITION:
                FragmentDefinition fragmentDefinition = (FragmentDefinition) actualNode.getNode();
                nodeDescriptor.setActualName( fragmentDefinition.getName());
                break;
            /*case FRAGMENT_SPREAD:
                break;*/
            case VARIABLE_DEFINITION:
                VariableDefinition variableDefinition = (VariableDefinition) actualNode.getNode();
                nodeDescriptor.setActualName( variableDefinition.getName());
                break;
            case ARGUMENT:
                Argument argument = (Argument) actualNode.getNode();
                nodeDescriptor.setActualName( argument.getName());
                break;

        }

        return nodeDescriptor;
    }

    //private int matchCount = 0;
    private int currentActualStackIndex = -1;
    private GqlNode getActualGqlNodeFromStack(Stack<GqlNode> actualNodeStack) {
        Pair<GqlNode, Integer> nextGqlNodeFromStack = getNextGqlNodeFromStack(actualNodeStack, currentActualStackIndex);
        currentActualStackIndex = nextGqlNodeFromStack.getRight();
        return nextGqlNodeFromStack.getLeft();
    }

    private Pair<GqlNode,Integer> getNextGqlNodeFromStack(Stack<GqlNode> actualNodeStack, int externalIndex) {
        GqlNode node = null;
        if( externalIndex == -1){
            // set index size
            externalIndex = actualNodeStack.size()-1;
        }
        do{
            node= actualNodeStack.get(externalIndex);;
            externalIndex--;
        }while( node.getType().equals(DocumentElementType.SELECTION_SET));

        Pair<GqlNode,Integer> pairResult = new ImmutablePair<>(node, externalIndex);
        return pairResult;
    }

    private boolean isEqual(Map<String, String> contextAttributeMap) {

        if( this.transactionId != null ) {
            String transactionId = contextAttributeMap.get(SearchPathElement.TRANSACTION_ID);

            if (!Strings.isNullOrEmpty(transactionId) && transactionId.equals(this.transactionId.toString())) {
                return false;
            }
        }


        boolean isNameEqual = false;
        boolean isNameVerified = false;
        if( contextAttributeMap.get(SearchPathElement.NAME) != null && expectedSearchAttribute.getName() != null ){

       //     String s = contextAttributeMap.get(SearchPathElement.NAME);
            isNameVerified = true;
            isNameEqual = contextAttributeMap.get(SearchPathElement.NAME).equalsIgnoreCase(expectedSearchAttribute.getName());
        }


        boolean isOperationNameEqual = false;
        boolean isOperationNameVerified = false;
        if( contextAttributeMap.get(SearchPathElement.OPERATION_NAME) != null && expectedSearchAttribute.getOperationName()!= null ){
            isOperationNameVerified = true;
            isOperationNameEqual = contextAttributeMap.get(SearchPathElement.OPERATION_NAME).equalsIgnoreCase(expectedSearchAttribute.getOperationName());
        }


        boolean isAliasEqual = false;
        boolean isAliasVerified = false;
        if( contextAttributeMap.get(SearchPathElement.ALIAS) != null && expectedSearchAttribute.getAlias() != null ){
            isAliasVerified = true;
            isAliasEqual = contextAttributeMap.get(SearchPathElement.ALIAS).equalsIgnoreCase(expectedSearchAttribute.getAlias());
        }

        boolean isTypeEqual = false;
        boolean isTypeVerified = false;
        if( contextAttributeMap.get(SearchPathElement.DOC_ELEM_TYPE) != null && expectedSearchAttribute.getType() != null ){
            isTypeVerified = true;
            isTypeEqual = contextAttributeMap.get(SearchPathElement.DOC_ELEM_TYPE).equalsIgnoreCase(expectedSearchAttribute.getType());
        }

        int isNameEqualFuzzyResult = getKeyEqualFuzzyResult(isNameVerified,isNameEqual);
        int isAliasEqualFuzzyResult = getKeyEqualFuzzyResult(isAliasVerified,isAliasEqual);
        int isTypeEqualFuzzyResult = getKeyEqualFuzzyResult(isTypeVerified,isTypeEqual);
        int isOperationNameEqualFuzzyResult = getKeyEqualFuzzyResult(isOperationNameVerified,isOperationNameEqual);


        if( (isOperationNameEqualFuzzyResult == 1 || isOperationNameEqualFuzzyResult ==2 )&&
                (isNameEqualFuzzyResult == 1 || isNameEqualFuzzyResult == 2) &&
                (isAliasEqualFuzzyResult == 1 || isAliasEqualFuzzyResult == 2) &&
                (isTypeEqualFuzzyResult == 1 || isTypeEqualFuzzyResult == 2)) {
            return true;
        }

        return false;
    }

    private int getKeyEqualFuzzyResult(boolean isVerified, boolean isEqual){
        if( ! isVerified ){
            return 2;
        }
        return (isVerified && isEqual) ? 1:0;
    }

    private static boolean extractActualNodeContext(Node node, Context context, Map<String, String> contextAttributeMap) {

        contextAttributeMap.put(SearchPathElement.DOC_ELEM_TYPE, context.getDocumentElementType().getShortName());

        String transactionIdValue = GeneratedComment.getTransactionIdValue(node);

        if( !Strings.isNullOrEmpty(transactionIdValue) ){
            contextAttributeMap.put(SearchPathElement.TRANSACTION_ID, transactionIdValue);
        }
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
            case SELECTION_SET:
                return false;
            case ARGUMENT:
                Argument argNode = (Argument) node;
                // Value value = argNode.getValue();
                //contextAttributeMap.put(NAME, argNode.getName() );
                contextAttributeMap.put(SearchPathElement.NAME, argNode.getName());
                break;
            default:
                // do nothing
                return true;
        }
        return false;
    }

    @Override
    public void updateNodeExit(graphql.language.Node node, Node parentNode, Context context, ObserverAction observerAction) {
    }

    public TuneableSearchData getTuneableSearchData() {
        return searchPathBuilder.getTuneableSearchData();
    }

}
