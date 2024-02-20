package com.intuit.library.eXtendGql.traversal;

import com.intuit.library.common.GqlNode;
import com.intuit.library.common.DocumentElementType;
import com.intuit.library.eXtendGql.TuneableSearchData;
import graphql.com.google.common.base.Preconditions;
import graphql.language.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Stack;

public class GqlTraversal {

    private final GqlTraversalObservable gqlTraversalObservable;
    Stack<GqlNode> nodeStack = null;
    private int level = -1; // init index is 0 (like a vector)
    private TuneableSearchData tuneableSearchData;

    public GqlTraversal() {
        gqlTraversalObservable = new GqlTraversalObservable();
    }

    public static Document parseDocument(String queryValue) {
        graphql.parser.Parser parser = new graphql.parser.Parser();
        return parser.parseDocument(queryValue);
    }

    public GqlTraversalObservable getGqlTraversalObservable() {
        return gqlTraversalObservable;
    }

    public void traverse(File file) throws IOException {

        String content = new String(Files.readAllBytes(file.toPath()));

        traverse(content);
    }

    public void traverse(String queryString, TuneableSearchData tuneableSearchData) {
        Preconditions.checkNotNull(queryString);

        Document graphQL = parseDocument(queryString);

        traverse(graphQL, tuneableSearchData);
    }
    public void traverse(Node queryNode, TuneableSearchData tuneableSearchData) {

        this.tuneableSearchData = tuneableSearchData;

        Preconditions.checkNotNull(queryNode);

        Document graphQL = (Document) queryNode;

        nodeStack = new Stack<GqlNode>();

        try {
            nodeStack.push(new GqlNode(graphQL, DocumentElementType.DOCUMENT));

            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(graphQL, null,
                    new Context(DocumentElementType.DOCUMENT, level, nodeStack));

            if (!isNotified) {
                // no meaning to run browsing when no one observe to the graphql traversal
                return;
            }

            if (graphQL.getDefinitions() == null) {
                return;
            }

            //level++;
            traverseDefinitions(graphQL);
        } finally {
            //level--;
            nodeStack.pop();
            gqlTraversalObservable.notifyObserversNodeExit(graphQL, null,
                    new Context(DocumentElementType.DOCUMENT, level, nodeStack));
        }
    }

    public void traverse(String queryCode) {
        traverse(queryCode, null);
    }

    private void traverseDefinitions(Document graphQL) {
        List<Definition> definitions = graphQL.getDefinitions();
        boolean isDefExist = false;

        isDefExist = definitions != null && definitions.size() > 0;
        if (isDefExist) {
            try {
                boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(null, null,
                        new Context(DocumentElementType.DEFINITIONS, level, nodeStack));
                if (!isNotified) {
                    // no meaning to run browsing when no one observe to the graphql traversal
                    return;
                }

                level++;
                for (Definition definition : definitions) {
                    traverseDefinition(definition, graphQL);
                }
            } finally {
                level--;
                gqlTraversalObservable.notifyObserversNodeExit(null, null,
                        new Context(DocumentElementType.DEFINITIONS, level, nodeStack));
            }
        }
    }

    private void traverseDefinition(Definition definition, Node parentNode) {

        try {
            boolean isFragment = false;
            boolean isQuery = false;
            boolean isMutation = false;

            if( definition instanceof FragmentDefinition){
              isFragment = true;
            }else if( definition instanceof OperationDefinition){
                if( ((OperationDefinition) definition).getOperation().name().equalsIgnoreCase("query") ){
                    isQuery = true;
                }else{
                    isMutation = true;
                }
            }

            nodeStack.push(new GqlNode(definition,
                    isFragment ?
                            DocumentElementType.FRAGMENT_DEFINITION :
                            (isQuery ? DocumentElementType.OPERATION_DEFINITION : DocumentElementType.MUTATION_DEFINITION)));

            if (definition instanceof OperationDefinition) {
                traverseOperationDefinition((OperationDefinition) definition, parentNode);
            } else{

                boolean isBrowse = true;

                if( this.tuneableSearchData != null ){
                    isBrowse = this.tuneableSearchData.isContainsFragment();
                }

                if (isTraverseByTuneableParams(isBrowse) && isFragment) {
                    traverseFragmentDefinition((FragmentDefinition) definition, parentNode);
                }
            }
        } finally {
            nodeStack.pop();
        }

    }

    private void traverseFragmentDefinition(FragmentDefinition fragmentDefinition, Node parentNode) {

        try {
            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(fragmentDefinition, parentNode,
                    new Context(DocumentElementType.FRAGMENT_DEFINITION, level, nodeStack));
            if (!isNotified) {
                // no meaning
                return;
            }

            boolean isBrowse = true;

            if( this.tuneableSearchData != null ){
                isBrowse = this.tuneableSearchData.isContainsDirectives();
            }

            if (isTraverseByTuneableParams(isBrowse))
                traverseDirectives(fragmentDefinition.getDirectives(), fragmentDefinition);

            traverseSelectionSet(fragmentDefinition.getSelectionSet(), fragmentDefinition);

        } finally {
            gqlTraversalObservable.notifyObserversNodeExit(fragmentDefinition, parentNode,
                    new Context(DocumentElementType.FRAGMENT_DEFINITION, level, nodeStack));
        }
    }

    private void traverseDirectives(List<Directive> directives, Node parentNode) {


        boolean isDirectiveExist = directives != null && directives.size() > 0;
        if (isDirectiveExist) {
            try {
                boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(null, null,
                        new Context(DocumentElementType.DIRECTIVES, level, nodeStack));

                if (!isNotified) {
                    // no meaning 
                    return;
                }
                for (Directive directive : directives) {
                    traverseDirective(directive, parentNode);
                }
            } finally {
                gqlTraversalObservable.notifyObserversNodeExit(null, null,
                        new Context(DocumentElementType.DIRECTIVES, level, nodeStack));
            }

        }
    }

    private void traverseOperationDefinition(OperationDefinition operationDefinition, Node parentNode) {
        OperationDefinition.Operation operation = operationDefinition.getOperation();

        DocumentElementType operationDefinition1 = operation.name().equalsIgnoreCase("mutation") ? DocumentElementType.MUTATION_DEFINITION : DocumentElementType.OPERATION_DEFINITION;

        try {
            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(operationDefinition, parentNode,
                    new Context(operationDefinition1, level, nodeStack));

            if (!isNotified) {
                return;
            }

            traverseSelectionSet(operationDefinition.getSelectionSet(), operationDefinition);

            boolean isBrowse = true;

            if( this.tuneableSearchData != null ){
                isBrowse = this.tuneableSearchData.isContainsVariables();
            }

            if (isTraverseByTuneableParams(isBrowse))
                traverseVariableDefinitions(operationDefinition.getVariableDefinitions(), operationDefinition);

            //level++;  --> ignore document -> this is level 0
        } finally {
            //level--;
            gqlTraversalObservable.notifyObserversNodeExit(operationDefinition, parentNode, new Context(operationDefinition1, level, nodeStack));
        }
    }

    private void traverseVariableDefinitions(List<VariableDefinition> variableDefinitions, Node parentNode) {

        boolean isVarsExist = variableDefinitions != null && variableDefinitions.size() > 0;
        if (isVarsExist) {
            try {
                boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(null, null, new Context(DocumentElementType.VARIABLE_DEFINITIONS, level, nodeStack));
                if (!isNotified) {
                    return;
                }

                for (VariableDefinition variableDefinition : variableDefinitions) {
                    traverseVariableDefinition(variableDefinition, parentNode);
                }
            } finally {
                gqlTraversalObservable.notifyObserversNodeExit(null, null, new Context(DocumentElementType.VARIABLE_DEFINITIONS, level, nodeStack));
            }
        }
    }

    private void traverseVariableDefinition(VariableDefinition variableDefinition, Node parentNode) {
        try {
            level++;

            nodeStack.push(new GqlNode(variableDefinition, DocumentElementType.VARIABLE_DEFINITION));
            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(variableDefinition,
                    parentNode, new Context(DocumentElementType.VARIABLE_DEFINITION, level, nodeStack));

            if (!isNotified) {
                return;
            }

            boolean isBrowse = true;

            if( this.tuneableSearchData != null ){
                isBrowse = this.tuneableSearchData.isContainsDirectives();
            }

            if (isTraverseByTuneableParams(isBrowse))
                traverseDirectives(variableDefinition.getDirectives(), variableDefinition);
        } finally {
            level--;
            nodeStack.pop();
            gqlTraversalObservable.notifyObserversNodeExit(variableDefinition, parentNode, new Context(DocumentElementType.VARIABLE_DEFINITION, level, nodeStack));
        }
    }

    private void traverseSelectionSet(SelectionSet selectionSet, Node parentNode) {
        if (selectionSet != null) {

            boolean isSelectionSetExist = selectionSet.getSelections() != null && selectionSet.getSelections().size() > 0;
            if (isSelectionSetExist) {
                try {
                    boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(selectionSet, parentNode,
                            new Context(DocumentElementType.SELECTION_SET, level, nodeStack));
                    if (!isNotified) {
                        return;
                    }
                    nodeStack.push(new GqlNode(selectionSet, DocumentElementType.SELECTION_SET));
                    for (Selection selection : selectionSet.getSelections()) {

                        try {


                            boolean isInlineFragment = selection instanceof InlineFragment;
                            boolean isFragmentSpread = selection instanceof FragmentSpread;

                            if( isInlineFragment){
                                nodeStack.push(new GqlNode(selection, DocumentElementType.INLINE_FRAGMENT));
                            } else if (isFragmentSpread) {
                                nodeStack.push(new GqlNode(selection, DocumentElementType.FRAGMENT_SPREAD));
                            }


                            if (selection instanceof Field) {
                                nodeStack.push(new GqlNode(selection, DocumentElementType.FIELD));
                                traverseField((Field) selection, selectionSet);
                            } else {

                                boolean isBrowse = true;

                                if( this.tuneableSearchData != null ){
                                    isBrowse = this.tuneableSearchData.isContainsFragment();
                                }

                                if (isTraverseByTuneableParams(isBrowse)) {
                                    if (isInlineFragment) {
                                        traverseInlineFragment((InlineFragment) selection, selectionSet);
                                    } else if (isFragmentSpread) {
                                        traverseFragmentSpread((FragmentSpread) selection, selectionSet);
                                    }
                                }
                            }
                        }finally {
                            nodeStack.pop(); // selection
                        }
                    }

                    nodeStack.pop(); // selection set
                } finally {
                    gqlTraversalObservable.notifyObserversNodeExit(selectionSet, parentNode,
                            new Context(DocumentElementType.SELECTION_SET, level, nodeStack));
                }

            }
        }
    }

    private void traverseDirective(Directive directive, Node parentNode) {
        try {
            level++;
            nodeStack.push(new GqlNode(directive, DocumentElementType.DIRECTIVE));

            boolean isNotified =
                    gqlTraversalObservable.notifyObserversNodeEntry(directive, parentNode,
                            new Context(DocumentElementType.DIRECTIVE, level, nodeStack));

            if (!isNotified) {
                return;
            }

            boolean isBrowse = true;

            if( this.tuneableSearchData != null ){
                isBrowse = this.tuneableSearchData.isContainsArgument();
            }

            if (isTraverseByTuneableParams(isBrowse))
                traverseArguments(directive.getArguments(), directive);

        } finally {
            level--;
            nodeStack.pop();
            gqlTraversalObservable.notifyObserversNodeExit(directive, parentNode,
                    new Context(DocumentElementType.DIRECTIVE, level, nodeStack));

        }

    }

    private boolean isTraverseByTuneableParams(boolean tuneableSearchData) {
        boolean isBrowse = true;
        if (this.tuneableSearchData != null) {
            isBrowse = tuneableSearchData;
        }
        return isBrowse;
    }

    private void traverseArguments(List<Argument> arguments, Node parentNode) {
        boolean isArgsExist = arguments != null && arguments.size() > 0;
        if (isArgsExist) {
            try {
                level++;

                boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(null,
                        null, new Context(DocumentElementType.ARGUMENTS, level, nodeStack));

                if (!isNotified) {
                    return;
                }

                for (Argument argument : arguments) {
                    try {
                        nodeStack.push(new GqlNode(argument, DocumentElementType.ARGUMENT));

                        isNotified = gqlTraversalObservable.notifyObserversNodeEntry(argument, parentNode,
                                new Context(DocumentElementType.ARGUMENT, level, nodeStack));
                        if (!isNotified) {
                            return;
                        }
                    } finally {
                        nodeStack.pop();
                    }
                }
            } finally {
                level--;
                gqlTraversalObservable.notifyObserversNodeExit(null, null,
                        new Context(DocumentElementType.ARGUMENTS, level, nodeStack));
            }

        }
    }

    private void traverseFragmentSpread(FragmentSpread fragmentSpread, Node parentNode) {
        try {
            level++;

            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(fragmentSpread, parentNode,
                    new Context(DocumentElementType.FRAGMENT_SPREAD, level, nodeStack));

            if (!isNotified) {
                return;
            }

            traverseDirectives(fragmentSpread.getDirectives(), fragmentSpread);
        } finally {
            level--;
            gqlTraversalObservable.notifyObserversNodeExit(fragmentSpread, parentNode,
                    new Context(DocumentElementType.FRAGMENT_SPREAD, level, nodeStack));
        }


    }

    private void traverseInlineFragment(InlineFragment inlineFragment, Node parentNode) {
        try {
            level++;

            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(inlineFragment,
                    parentNode, new Context(DocumentElementType.INLINE_FRAGMENT, level, nodeStack));
            if (!isNotified) {
                return;
            }

            traverseDirectives(inlineFragment.getDirectives(), inlineFragment);

            traverseSelectionSet(inlineFragment.getSelectionSet(), inlineFragment);
        } finally {
            level--;
            gqlTraversalObservable.notifyObserversNodeExit(inlineFragment, parentNode,
                    new Context(DocumentElementType.INLINE_FRAGMENT, level, nodeStack));
        }

    }

    private void traverseField(Field field, Node parentNode) {
        try {

            level++;

            boolean isNotified = gqlTraversalObservable.notifyObserversNodeEntry(field, parentNode,
                    new Context(DocumentElementType.FIELD, level, nodeStack));
            if (!isNotified) {
                return;
            }

            traverseSelectionSet(field.getSelectionSet(), field);

            traverseArguments(field.getArguments(), field);

            traverseDirectives(field.getDirectives(), field);
        } finally {
            level--;
            gqlTraversalObservable.notifyObserversNodeExit(field, parentNode,
                    new Context(DocumentElementType.FIELD, level, nodeStack));
        }


    }


}
