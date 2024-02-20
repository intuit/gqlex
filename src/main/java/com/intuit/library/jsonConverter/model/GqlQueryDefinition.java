package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.model.tree.GqlTree;
import com.intuit.library.jsonConverter.model.tree.GqlTreeNode;
//import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class GqlQueryDefinition implements Serializable {
    private String definitionName;

    private GqlTree gqlTree;


    public void setGqlTree(GqlTree gqlTree) {
        this.gqlTree = gqlTree;
    }

    public GqlTree getGqlTree() {
        if( gqlTree ==null ){
            gqlTree = new GqlTree();
        }
        return gqlTree;
    }

    public String getDefinitionName() {
        return definitionName;
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public JSONObject toJsonObject() {
        if( this.getGqlTree() == null || this.getGqlTree().getGqlTreeNode() == null ){
            return null;
        }

        JSONObject parentJsonObject = new JSONObject();

        //JSONObject queryDefinitionJsonObject = (JSONObject)parentJsonObject;
        //JSONArray queryDefinitionJsonArray = (JSONArray)parentJsonObject;

        GqlTreeNode gqlTreeNode = this.getGqlTree().getGqlTreeNode();


        /*List<GqlTreeNode> */ List<Gqlable> methodChildNodes = gqlTreeNode.getChildNodes();
        GqlTreeNode methodChildNode = (GqlTreeNode)methodChildNodes.get(0);


        /*parentJsonObject.put(methodChildNode.getFieldNameDescriptor().getTypeName(),
                methodChildNode.getFieldNameDescriptor().getName());
*/
        parentJsonObject.put(gqlTreeNode.getFieldNameDescriptor().getTypeName(),
                gqlTreeNode.getFieldNameDescriptor().getName());

        GqlArgumentsContainer gqlArgumentsContainer = methodChildNode.getGqlArgumentsContainer();
        if( gqlArgumentsContainer != null ) {
            gqlArgumentsContainer.generateAsJsonObject(parentJsonObject);
        }

        GqlDirectivesContainer gqlDirectivesContainer = methodChildNode.getGqlDirectivesContainer();
        if( gqlDirectivesContainer != null ) {
            gqlDirectivesContainer.generateAsJsonObject(parentJsonObject);
        }

        GqlVariableDefinitionsContainer gqlVariableDefinitionsContainer = methodChildNode.getGqlVariableDefinitionsContainer();
        if( gqlVariableDefinitionsContainer != null ) {
            gqlVariableDefinitionsContainer.generateAsJsonObject(parentJsonObject);
        }

        if (gqlTreeNode.hasChilds()) {

          //  JSONArray jsonArray = new JSONArray();
            //JSONObject jsonObjectContainer = new JSONObject();
           // parentJsonObject.put("__elems",jsonObjectContainer);
            int i=1;
            for (Gqlable childNode : methodChildNode.getChildNodes()) {
                Object o = childNode.generateAsJsonObject();
                if( o == null ) {
                    o = JSONObject.NULL;
                }
                parentJsonObject.put(childNode.getFieldNameDescriptor().getConcatenatedName(), o);
            }

        }

        return parentJsonObject;

    }

    /*private static JSONObject getJsonObject(String typeName, String name) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(typeName, name);
        return jsonObject;
    }*/
}
