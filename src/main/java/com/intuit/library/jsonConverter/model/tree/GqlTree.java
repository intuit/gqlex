package com.intuit.library.jsonConverter.model.tree;

import com.intuit.library.jsonConverter.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GqlTree {

    private  GqlTreeNode gqlTreeNode;

    public GqlTreeNode getGqlTreeNode() {
        if( gqlTreeNode == null ){
            gqlTreeNode = new GqlTreeNode();
        }
        return gqlTreeNode;
    }

    public  GqlTreeNode addRootNode(FieldNameDescriptor fieldNameDescriptor){
        if(fieldNameDescriptor ==null){
            throw new IllegalArgumentException("fieldNameDescriptor is null");
        }
        GqlTreeNode node = getGqlTreeNode();
        node.setFieldNameDescriptor(fieldNameDescriptor);
        return node;
    }

    /*public List<Field> getFields(){
        if( gqlTreeNode == null) {
            return null;
        }

        List<Field> leafs = new ArrayList<>();
        extractedFieldLeafs(leafs, gqlTreeNode);
        return leafs;
    }*/

    public List<TreeNodePath>  extractedFieldLeafs(){
        List<TreeNodePath> leafs = new ArrayList<>();
        Stack<FieldNameDescriptor> parentRouteStack = new Stack<>();
        extractedFieldLeafs(leafs, this.getGqlTreeNode(), parentRouteStack);
        return leafs;
    }

    private void extractedFieldLeafs(List<TreeNodePath> fieldsLeafs, Gqlable node, Stack<FieldNameDescriptor> parentRouteStack) {
        if( node != null) {
            if( node.hasChilds() ){

                parentRouteStack.push(node.getFieldNameDescriptor());


                if( node instanceof  GqlTreeNode) {

                    GqlTreeNode gqlTreeNodeObj = (GqlTreeNode)node;
                    if (gqlTreeNodeObj.getGqlArgumentsContainer() != null && gqlTreeNodeObj.getGqlArgumentsContainer().size() > 0) {
                        for (GqlArgument childNode : gqlTreeNodeObj.getGqlArgumentsContainer().getGqlArguments()) {
                            extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                        }
                    }

                    if (gqlTreeNodeObj.getGqlVariableDefinitionsContainer() != null && gqlTreeNodeObj.getGqlVariableDefinitionsContainer().size() > 0) {
                        for (GqlVariableDefinition childNode : gqlTreeNodeObj.getGqlVariableDefinitionsContainer().getGqlVariableDefinitions()) {
                            extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                        }
                    }

                    if (gqlTreeNodeObj.getGqlDirectivesContainer() != null && gqlTreeNodeObj.getGqlDirectivesContainer().size() > 0) {
                        for (GqlDirective childNode : gqlTreeNodeObj.getGqlDirectivesContainer().getGqlDirectives()) {
                            extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                        }
                    }

                }/*else if( node instanceof  GqlArgument) {
                    GqlArgument gqlArgument = (GqlArgument)node;
                    for (Gqlable childNode : gqlArgument.getChildNodes()) {
                        isCheckChild = true;
                        extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                    }
                }else if( node instanceof  GqlDirective) {
                    GqlDirective gqlArgument = (GqlDirective)node;
                    for (Gqlable childNode : gqlArgument.getChildNodes()) {
                        isCheckChild = true;
                        extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                    }
                }else if( node instanceof  GqlVariableDefinition) {
                    GqlVariableDefinition gqlArgument = (GqlVariableDefinition)node;
                    for (Gqlable childNode : gqlArgument.getChildNodes()) {
                        isCheckChild = true;
                        extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                    }
                }*/





                if (node.getChildNodes() != null && node.getChildNodes().size() > 0) {
                    for (Gqlable childNode : node.getChildNodes()) {
                        extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                    }
                }




               /* if (node.getChildNodes() != null && node.getChildNodes().size() > 0) {
                    for (Gqlable childNode : node.getChildNodes()) {
                        extractedFieldLeafs(fieldsLeafs, childNode, parentRouteStack);
                    }
                }*/

                parentRouteStack.pop();
            }else{
                TreeNodePath treeNodePath = new TreeNodePath();
                treeNodePath.setNode(node);
                treeNodePath.setPath(parentRouteStack.toArray(new FieldNameDescriptor[0]));
                fieldsLeafs.add(treeNodePath);
                return;
            }
        }
    }
}
