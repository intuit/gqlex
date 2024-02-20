package com.intuit.library.eXtendGql.transformer;

import com.intuit.library.common.GqlNode;
import com.intuit.library.common.GqlNodeContext;
import com.intuit.library.common.NodeNotFoundException;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.*;

import java.util.*;

public class TransformUtils {

    public static final int DEFAULT_DUPLICATION_NUMBER = 1;
   // public static final String SELECTIONS = "selections";

    public static Node addChildren(GqlNodeContext nodeContext, Node newNodeToAdd){
        return addChildren(nodeContext, newNodeToAdd, null);
    }
    public static Node addChildren(GqlNodeContext nodeContext, Node newNodeToAdd, UUID transactionId) {
        Preconditions.checkArgument(nodeContext!=null);
        Preconditions.checkArgument(newNodeToAdd!=null);

        if( nodeContext.getNodeStack().isEmpty()){
            throw new IllegalArgumentException("No context set for node");
        }

        Stack<GqlNode> nodeStack = (Stack) nodeContext.getNodeStack().clone();


        Node nodeToChange = null;
        if( nodeContext.getNode() instanceof  SelectionSet) {
            /*nodeToChange = (SelectionSet) nodeContext.getNode();
            List<Selection> selectionList = new ArrayList<>(((SelectionSet)nodeToChange).getSelections());
            selectionList.add((Selection) newNodeToAdd);

            SelectionSet selectionSet = new SelectionSet(selectionList);
            nodeToChange = ((SelectionSet)nodeToChange).transform(a -> a.selections(selectionSet.getSelections()));*/

            throw new UnsupportedOperationException("Add Children: does not support of SelectionSet");

        }else if( nodeContext.getNode() instanceof  Selection) {

            if( nodeContext.getNode() instanceof  Field) {

                nodeToChange = nodeContext.getNode();

                SelectionSet selectionSet = ((Field) nodeToChange).getSelectionSet();

                List<Selection> selectionList = new ArrayList<>();
                if( selectionSet != null ){
                    selectionList.addAll(selectionSet.getSelections());
                }

                selectionList.add((Field)newNodeToAdd);
                selectionSet = new SelectionSet(selectionList);

                SelectionSet finalSelectionSet = selectionSet;
                nodeToChange = ((Field) nodeToChange).transform(a->a.selectionSet(finalSelectionSet));

                if(transactionId != null ) {
                    nodeToChange = GeneratedComment.addComment(nodeToChange, GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
                }

            }else if( nodeContext.getNode() instanceof  InlineFragment) {
                nodeToChange = (InlineFragment) nodeContext.getNode();

                SelectionSet selectionSet = ((InlineFragment) nodeToChange).getSelectionSet();

                List<Selection> selectionList = new ArrayList<>();
                if (selectionSet != null) {
                    selectionList.addAll(selectionSet.getSelections());
                }

                selectionList.add((Field) newNodeToAdd);
                selectionSet = new SelectionSet(selectionList);

                SelectionSet finalSelectionSet = selectionSet;
                nodeToChange = ((InlineFragment) nodeToChange).transform(a -> a.selectionSet(finalSelectionSet));

                if(transactionId != null ) {
                   nodeToChange = GeneratedComment.addComment(nodeToChange, GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
                }
            } else{
                throw new UnsupportedOperationException("Add Children: Support only selection of type field");
            }
        }
        //nodeContext.getNodeStack().pop();
        nodeStack.pop();
        GqlNode grandPaNode = nodeStack.pop();

        return bottomToUpCreateNodeTreeChilds(nodeStack,
                grandPaNode.getNode(),
                nodeToChange);
    }

    public static Node addSibling(GqlNodeContext nodeContext, Node newNodeToAdd){
        return addSibling(nodeContext, newNodeToAdd, null);
    }
    public static Node addSibling(GqlNodeContext nodeContext, Node newNodeToAdd, UUID transactionId) {

        SelectionSet nodeToChange = (SelectionSet) nodeContext.getParentNode();
        List<Selection> selectionList = new ArrayList<>(nodeToChange.getSelections());
        selectionList.add((Selection) newNodeToAdd);

        SelectionSet selectionSet = new SelectionSet(selectionList);
        nodeToChange = nodeToChange.transform(a -> a.selections(selectionSet.getSelections()));

        if(transactionId != null ) {
            nodeToChange = (SelectionSet) GeneratedComment.addComment(nodeToChange,
                    GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
        }

        nodeContext.getNodeStack().pop();
        nodeContext.getNodeStack().pop();
        GqlNode grandPaNode = nodeContext.getNodeStack().pop();

        return bottomToUpCreateNodeTreeChilds(nodeContext.getNodeStack(),
                grandPaNode.getNode(),
                nodeToChange);
    }

    private static Node bottomToUpCreateNodeTreeChilds(Stack<GqlNode> nodeStack,
                                                       Node parentNode,
                                                       Node childNode) {

        if (parentNode instanceof Selection) {

            if (parentNode instanceof Field) {
                Field parentNodeField = (Field) parentNode;

                Field finalParentNodeField = parentNodeField;

                if( childNode instanceof  Field) {
                    parentNode = parentNodeField.transform(a -> a.selectionSet(finalParentNodeField.getSelectionSet()
                            .transform(b -> b.selections(((SelectionSet) childNode).getSelections()))));
                }else if( childNode instanceof Directive){
                    List<Directive> directives = new ArrayList<>();
                    String direcName = ((Directive)childNode).getName();
                    String oldName = GeneratedComment.getOldName(childNode);
                    if( !Strings.isNullOrEmpty(oldName)){
                        direcName =oldName;
                    }
                    for (Directive directive : parentNodeField.getDirectives()) {
                        if (directive.getName().equalsIgnoreCase(direcName)) {
                            continue;
                        }
                        directives.add(directive);
                    }
                    directives.add((Directive) childNode);

                    parentNode = parentNodeField.transform(a -> a.directives(directives));
                }else if(childNode instanceof SelectionSet){
                    parentNode = parentNodeField.transform(a->a.selectionSet((SelectionSet) childNode));
                }

            } else if (parentNode instanceof InlineFragment) {
                InlineFragment parentNodeField = (InlineFragment) parentNode;

                InlineFragment finalParentNodeField = parentNodeField;

                parentNode = parentNodeField.transform(a -> a.selectionSet(finalParentNodeField.getSelectionSet()
                        .transform(b -> b.selections(((SelectionSet) childNode).getSelections()))));

            }else {
                throw new UnsupportedOperationException("Only selection of type field is supported");
            }
        } else if (parentNode instanceof SelectionSet) {
            SelectionSet parentNodeSelectionSet = (SelectionSet) parentNode;

            List<Selection> selections = new ArrayList<>(parentNodeSelectionSet.getSelections());
            int indexToReplace = -1;
            Class<? extends Node> childNodeClass = childNode.getClass();

            for (int i = 0; i < selections.size(); i++) {

                if( selections.get(i).getClass() != childNodeClass){
                    continue;
                }

                if( childNode instanceof InlineFragment) {
                    String name = ((InlineFragment) childNode).getTypeCondition().getName();
                    String valueFromComment = extractNameFromComments(childNode);
                    if( !Strings.isNullOrEmpty(valueFromComment )){
                        name = valueFromComment;
                    }

                    if (((InlineFragment) selections.get(i)).getTypeCondition().getName().equalsIgnoreCase(name)) {
                        indexToReplace = i;
                        break;
                    }
                }else if( childNode instanceof Field) {

                    String name = ((Field) childNode).getName();
                    String valueFromComment = extractNameFromComments(childNode);
                    if( !Strings.isNullOrEmpty(valueFromComment )){
                        name = valueFromComment;
                    }
                    if (((Field) selections.get(i)).getName().equalsIgnoreCase(name) ) {
                        indexToReplace = i;
                        break;
                    }
                }
            }
            selections.remove(indexToReplace);
            selections.add((Selection) childNode);

            parentNode = parentNodeSelectionSet.transform(a -> a.selections(selections));
        } else if (parentNode instanceof OperationDefinition) {

            OperationDefinition parentNodeOperationDefinition = (OperationDefinition) parentNode;

            parentNode = parentNodeOperationDefinition.transform(a -> a.selectionSet((SelectionSet) childNode));
        } else if (parentNode instanceof FragmentDefinition) {

            FragmentDefinition parentNodeFragmentDefinition = (FragmentDefinition) parentNode;

            parentNode = parentNodeFragmentDefinition.transform(a -> a.selectionSet((SelectionSet) childNode));
        } else if (parentNode instanceof Document) {
            Document parentNodeDocument = (Document) parentNode;

            List<Definition> nodeList = new ArrayList<>(parentNodeDocument.getDefinitions());
            int indexToReplace = -1;
            for (int i = 0; i < nodeList.size(); i++) {
                Definition definition = nodeList.get(i);
                if (definition instanceof OperationDefinition) {
                    String operationDefinitionName = ((OperationDefinition) definition).getName();

                    if (!Strings.isNullOrEmpty(operationDefinitionName)) {
                        if(childNode instanceof OperationDefinition) {

                            String name = ((OperationDefinition) childNode).getName();
                            String valueFromComment = extractNameFromComments(childNode);
                            if( !Strings.isNullOrEmpty(valueFromComment )){
                                name = valueFromComment;
                            }

                            if (operationDefinitionName.equalsIgnoreCase(name)) {
                                indexToReplace = i;
                            }
                        }else if(childNode instanceof FragmentDefinition) {

                            String name = ((FragmentDefinition) childNode).getName();
                            String valueFromComment = extractNameFromComments(childNode);
                            if( !Strings.isNullOrEmpty(valueFromComment )){
                                name = valueFromComment;
                            }
                            if (operationDefinitionName.equalsIgnoreCase(name)) {
                                indexToReplace = i;
                            }
                        }
                    } else {
                        indexToReplace = i;
                        if (nodeList.size() > 1) {
                            throw new UnsupportedOperationException("not supported of batch queries with void queries");
                        }
                    }
                } else if (definition instanceof FragmentDefinition) {
                    if( childNode instanceof  FragmentDefinition) {

                        String name = ((FragmentDefinition) childNode).getName();
                        String valueFromComment = extractNameFromComments(childNode);
                        if( !Strings.isNullOrEmpty(valueFromComment )){
                            name = valueFromComment;
                        }

                        if (((FragmentDefinition) definition).getName().equalsIgnoreCase(name)) {
                            indexToReplace = i;
                        }
                    }
                }
            }
            nodeList.remove(indexToReplace);
            nodeList.add((Definition) childNode);
            parentNode = parentNodeDocument.transform(a -> a.definitions(nodeList));
        }


        if (nodeStack.isEmpty()) {
            return parentNode;
        }
        GqlNode grandPaNode = nodeStack.pop();

        if (grandPaNode == null) {
            return parentNode;
        } else {
            return bottomToUpCreateNodeTreeChilds(nodeStack, grandPaNode.getNode(), parentNode);
        }
    }

    private static String extractNameFromComments(Node childNode) {
        if( childNode.getComments() != null ){
            String oldName = GeneratedComment.getOldName(childNode);

            if( !Strings.isNullOrEmpty(oldName)){
                return oldName;
            }
        }
        return null;
    }
    public static Node updateNodeAlias(GqlNodeContext nodeContext,
                                      String newName){
        return updateNodeAlias(nodeContext, newName, null);
    }

    public static Node updateNodeAlias(GqlNodeContext nodeContext,
                                      String newAliasName, UUID transactionId) {

        Preconditions.checkArgument(nodeContext!=null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newAliasName));

        if( nodeContext.getNode() instanceof  SelectionSet) {
            throw new UnsupportedOperationException("updateNode support only of Selection");
        }

        Stack<GqlNode> nodeStack = (Stack) nodeContext.getNodeStack().clone();

        Node newNode = nodeContext.getNode();

        if (newNode instanceof Field) {
            Field node = (Field) nodeContext.getNode();
            String oldName = node.getAlias();

            Map<String,String> map = new HashMap<>();
            map.put(GeneratedComment.OLD_ALIAS_KEY, oldName);
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }
            node = (Field) GeneratedComment.addComments(node, map);

            newNode = node.transform(a -> a.alias(newAliasName));
        }else{
            Map<String,String> map = new HashMap<>();
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }
            newNode = GeneratedComment.addComments(newNode, map);
        }

        nodeStack.pop();
        GqlNode grandPaNode = nodeStack.pop();

        return bottomToUpCreateNodeTreeChilds(nodeStack,
                grandPaNode.getNode(),
                newNode);
    }

    public static Node updateNodeName(GqlNodeContext nodeContext,
                                      String newName){
        return updateNodeName(nodeContext, newName, null);
    }
    public static Node updateNodeName(GqlNodeContext nodeContext,
                                      String newName, UUID transactionId) {

        Preconditions.checkArgument(nodeContext!=null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newName));

        if( nodeContext.getNode() instanceof  SelectionSet) {
            throw new UnsupportedOperationException("updateNode support only of Selection");
        }

        Stack<GqlNode> nodeStack = (Stack) nodeContext.getNodeStack().clone();

        Node newNode = nodeContext.getNode();

        if (newNode instanceof Field) {
            Field node = (Field) nodeContext.getNode();
            String oldName = node.getName();

            Map<String,String> map = new HashMap<>();
            map.put(GeneratedComment.OLD_NAME_KEY, oldName);
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }
            node = (Field) GeneratedComment.addComments(node, map);

            newNode = node.transform(a -> a.name(newName));

        } else if (newNode instanceof FragmentDefinition) {

            FragmentDefinition node = (FragmentDefinition) nodeContext.getNode();
            String oldName = node.getName();

            Map<String,String> map = new HashMap<>();
            map.put(GeneratedComment.OLD_NAME_KEY, oldName);
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }

            node = (FragmentDefinition) GeneratedComment.addComments(node, map);
            newNode = node.transform(a -> a.name(newName));

        } else if (newNode instanceof InlineFragment) {

            InlineFragment node = (InlineFragment) nodeContext.getNode();
            String oldName = node.getTypeCondition().getName();

            Map<String,String> map = new HashMap<>();
            map.put(GeneratedComment.OLD_NAME_KEY, oldName);
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }
            node = (InlineFragment) GeneratedComment.addComments(node, map);

            newNode = node.transform(a -> a.typeCondition(new TypeName(newName)));

        } else if (newNode instanceof FragmentSpread) {
           /* FragmentSpread node = (FragmentSpread) nodeContext.getNode();
            String oldName = node.getName();
            List<Comment> comments = GeneratedComment.addComment(node, GeneratedComment.OLD_NAME_KEY, oldName);
            newNode = node.transform(a->a.comments(comments)).transform(a -> a.name(newName));*/
            throw new UnsupportedOperationException("Update of FragmentSpread node name does not supported yet");

        } else if (newNode instanceof OperationDefinition) {
            /*OperationDefinition node = (OperationDefinition) nodeContext.getNode();
            String oldName = node.getName();
            List<Comment> comments = GeneratedComment.addComment(node, GeneratedComment.OLD_NAME_KEY, oldName);
            newNode = node.transform(a->a.comments(comments)).transform(a -> a.name(newName));*/
            throw new UnsupportedOperationException("Update of OperationDefinition node name does not supported yet");
        } else if (newNode instanceof Directive) {
            Directive node = (Directive) nodeContext.getNode();
            String oldName = node.getName();

            Map<String,String> map = new HashMap<>();
            map.put(GeneratedComment.OLD_NAME_KEY, oldName);
            if(transactionId != null ) {
                map.put(GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
            }
            node = (Directive) GeneratedComment.addComments(node, map);

            newNode = node.transform(a -> a.name(newName));

        } else if (newNode instanceof Argument) {
            /*Argument node = (Argument) nodeContext.getNode();
            String oldName = node.getName();
            List<Comment> comments = GeneratedComment.addComment(node, GeneratedComment.OLD_NAME_KEY, oldName);
            newNode = node.transform(a->a.comments(comments)).transform(a -> a.name(newName));*/

            throw new UnsupportedOperationException("Update of Argument node name does not supported yet");
        } else if (newNode instanceof VariableDefinition) {
            /*VariableDefinition node = (VariableDefinition) nodeContext.getNode();
            String oldName = node.getName();
            List<Comment> comments = GeneratedComment.addComment(node, GeneratedComment.OLD_NAME_KEY, oldName);
            newNode = node.transform(a->a.comments(comments)).transform(a -> a.name(newName));*/
            throw new UnsupportedOperationException("Update of VariableDefinition node name does not supported yet");
        }

        nodeStack.pop();
        GqlNode grandPaNode = nodeStack.pop();

        return bottomToUpCreateNodeTreeChilds(nodeStack,
                grandPaNode.getNode(),
                newNode);
    }

    public static Node duplicateChild(GqlNodeContext nodeContext, int duplicationNumber){
        return duplicateChild(nodeContext, duplicationNumber, null);
    }
    public static Node duplicateChild(GqlNodeContext nodeContext, int duplicationNumber, UUID transactionId) {
        return duplicateChild(nodeContext, duplicationNumber, false,transactionId);
    }

    public static Node duplicateChild(GqlNodeContext nodeContext, int duplicationNumber, boolean isConcatenateSeqNumber){
        return duplicateChild(nodeContext, duplicationNumber,isConcatenateSeqNumber, null);
    }
    public static Node duplicateChild(GqlNodeContext nodeContext, int duplicationNumber, boolean isConcatenateSeqNumber, UUID transactionId) {
        if( duplicationNumber < 0 ){
            duplicationNumber = DEFAULT_DUPLICATION_NUMBER;
        }

        Node node = nodeContext.getNode();
        if(!( node instanceof Field)){
            throw new UnsupportedOperationException("Support only field type for duplication");
        }

        int inx = 1;
        SelectionSet nodeToChange = (SelectionSet) nodeContext.getParentNode();
        List<Selection> selectionList = new ArrayList<>(nodeToChange.getSelections());
        for (int i = 0; i < duplicationNumber; i++) {
            Field field = ((Field) node).deepCopy();
            if( isConcatenateSeqNumber){
                String name = field.getName() + "_" + inx;
                inx++;
                field = field.transform(a -> a.name(name));
            }
            selectionList.add((Selection) field);
        }

        SelectionSet selectionSet = new SelectionSet(selectionList);
        nodeToChange = nodeToChange.transform(a -> a.selections(selectionSet.getSelections()));

        if(transactionId != null ) {
            nodeToChange = (SelectionSet) GeneratedComment.addComment(nodeToChange,
                    GeneratedComment.TRANSFORM_TRX_ID, transactionId.toString());
        }

        nodeContext.getNodeStack().pop();
        nodeContext.getNodeStack().pop();
        GqlNode grandPaNode = nodeContext.getNodeStack().pop();

        return bottomToUpCreateNodeTreeChilds(nodeContext.getNodeStack(),
                grandPaNode.getNode(),
                nodeToChange);

    }

    public static Node removeChild(GqlNodeContext nodeContext) {

        SelectionSet parentNode = (SelectionSet) nodeContext.getParentNode();
        Node nodeToRemove = nodeContext.getNode();
        List<Selection> selectionList = new ArrayList<>(parentNode.getSelections());
        int index = -1;
        boolean isFound = false;
        for (Selection child : selectionList) {
            index++;
            if( child instanceof Field && nodeContext.getNode() instanceof Field){

                if (((Field)child).getName().equals( ((Field)nodeContext.getNode()).getName())) {
                    isFound = true;
                    break;
                }
            }else if( child instanceof InlineFragment && nodeContext.getNode() instanceof InlineFragment){

                if (((InlineFragment)child).getTypeCondition().getName().equals( ((InlineFragment)nodeContext.getNode()).getTypeCondition().getName())) {
                    isFound = true;
                    break;
                }
            }else{
                continue;
            }
        }
        if(!isFound ){
            throw new NodeNotFoundException(nodeContext, "Node removal failed, not does not exist");
        }
        selectionList.remove(index);

        SelectionSet selectionSet = null;
        if( selectionList.size() > 0) {
            selectionSet = new SelectionSet(selectionList);
        }else{ //Invalid syntax with offending token '}' at line 20 column 8
            ArrayList<Selection> selections = new ArrayList<>();
            selections.add(0, new Field("NULL"));
            selectionSet = new SelectionSet(selections);
        }
        SelectionSet finalSelectionSet = selectionSet;
        parentNode = parentNode.transform(a -> a.selections(finalSelectionSet.getSelections()));

        nodeContext.getNodeStack().pop();
        nodeContext.getNodeStack().pop();
        GqlNode grandPaNode = nodeContext.getNodeStack().pop();

        return bottomToUpCreateNodeTreeChilds(nodeContext.getNodeStack(),
                grandPaNode.getNode(),
                parentNode);
    }
}
