package com.intuit.library.jsonConverter.model.tree;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.ReservedInternJsonWords;
import com.intuit.library.jsonConverter.model.*;
import graphql.language.TypeName;
import graphql.language.VariableDefinition;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class GqlTreeNode implements Serializable, Gqlable {


    private Map<String, Object> additionalData = null;
    private String id;
    private List<Gqlable> childNodes;
    private FieldNameDescriptor fieldNameDescriptor;
    private Set<String> childIds;
    private GqlDirectivesContainer gqlDirectivesContainer;
    private GqlVariableDefinitionsContainer gqlVariableDefinitionsContainer;
    private TypeName typeConditionName;
    private GqlArgumentsContainer gqlArgumentsContainer;

    public GqlTreeNode() {

    }

    public Map<String, Object> getAdditionalData() {
        if (additionalData == null) {
            additionalData = new HashMap<>();
        }
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Gqlable> getChildNodes() {
        return childNodes;
    }

    public GqlTreeNode addChildNode(GqlTreeNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node is null");
        }

        if (this.childNodes == null) {
            this.childNodes = new ArrayList<>();
        }

        if (node.getId() != null && childIds != null && childIds.contains(node.getId())) {
            return node;
        }
        this.childNodes.add(node);

        if (node.getId() != null) {
            if (childIds == null) {
                childIds = new HashSet<>();
            }
        }

        if (node.getId() != null) {
            childIds.add(node.getId());
        }
        return node;
    }

    public boolean hasChilds() {
        return childNodes != null && childNodes.size() > 0;
    }

    public FieldNameDescriptor getFieldNameDescriptor() {
        return fieldNameDescriptor;
    }

    public void setFieldNameDescriptor(FieldNameDescriptor fieldNameDescriptor) {

        this.fieldNameDescriptor = fieldNameDescriptor;
    }

    public void shallowCopy(GqlTreeNode sourceSubTreeToAdd) {

        if (sourceSubTreeToAdd == null) {
            throw new IllegalArgumentException("sourceSubTreeToAdd is null");
        }

        this.setId(sourceSubTreeToAdd.getId());
        this.setAdditionalData(sourceSubTreeToAdd.getAdditionalData());

        this.setFieldNameDescriptor(sourceSubTreeToAdd.getFieldNameDescriptor());
        this.setGqlDirectivesContainer(sourceSubTreeToAdd.getGqlDirectivesContainer());
    }

    @Override
    public Object generateAsJsonObject() {

        return generateAsJsonObject(this);
    }

    @Override
    public Object generateAsJsonObject(Object nodeToCreateAsJson) {

        GqlTreeNode node = (GqlTreeNode) nodeToCreateAsJson;
        //JSONObject objFieldNameValueJsonObj = (JSONObject)node.getFieldNameDescriptor().generateAsJsonObject(null);

        JSONObject nodeJsonObject = populateNode(node, null);

        if (node.getGqlDirectivesContainer() != null && node.getGqlDirectivesContainer().size() > 0) {

            JSONObject mainJson = new JSONObject();

            JSONObject directiveContentJson = new JSONObject();

            // directives
            boolean isDirectiveData = true;
            JSONObject directNode = (JSONObject)node.getGqlDirectivesContainer().generateAsJsonObject();
            String key = directNode.keys().next();
            JSONObject directValuesJsonObj = (JSONObject) directNode.get(key);
            if( directValuesJsonObj != null){
                Set<String> keys = directValuesJsonObj.keySet();
                if( keys.size() == 0){
                    // do nothing
                    isDirectiveData = false;
                }else if( keys.size() == 1){
                    String keyVal = directValuesJsonObj.keys().next();
                    directiveContentJson.put(keyVal, directValuesJsonObj.get(keyVal));
                }else{
                    for (String keyVal : keys) {
                       directiveContentJson.put("__"+keyVal, directValuesJsonObj.get(keyVal));
                    }
                }
            }

            if( isDirectiveData){

                String keyOfValue = directiveContentJson.keys().next();
                if( directiveContentJson.keySet().size() == 1 &&
                        (keyOfValue.equals(ParserConsts.DIRECTIVES_CONTENT ) ||
                                keyOfValue.equals(ParserConsts.CONTENT ) ||
                                keyOfValue.equals(ParserConsts.VALUE ))){

                    mainJson.put(ParserConsts.DIRECTIVES_CONTENT, directiveContentJson.get(keyOfValue));
                }else {
                    mainJson.put(ParserConsts.DIRECTIVES_CONTENT, directiveContentJson);
                }
            }



            //directiveContentJson.put(key, directNode.get(key));

            if( nodeJsonObject != null && nodeJsonObject.length() != 0)
                directiveContentJson.put(ParserConsts.CONTENT, nodeJsonObject);

            return mainJson;
        }


        return nodeJsonObject;
    }

    private JSONObject populateNode(GqlTreeNode node, JSONObject contentJsonObj) {

        if (node.getGqlArgumentsContainer() != null) {
            JSONObject argsJson = (JSONObject) node.getGqlArgumentsContainer().generateAsJsonObject();
            if (argsJson != null) {
                if( contentJsonObj == null){
                    contentJsonObj = new JSONObject();
                }
                for (String key : argsJson.keySet()) {
                    Object argJsonValue = argsJson.get(key);

                    String keyStructName = ParserConsts.ARG  + "<<" + key + ">>";

                    if( argJsonValue == null ){
                        contentJsonObj.put(keyStructName, JSONObject.NULL);
                        continue;
                    }


                    if(ReservedInternJsonWords.isValueNameIsOfSingleElement(key)){
                        if( argJsonValue instanceof  JSONObject) {
                            JSONObject value = (JSONObject) argJsonValue;

                            if (value.keySet() != null && value.keySet().size() > 1) {
                                contentJsonObj.put(keyStructName, value);
                            } else {
                                if( value.keys().hasNext()) {
                                    String keyIntern = value.keys().next();

                                    contentJsonObj.put(keyStructName, value.get(keyIntern));
                                }
                            }
                        }else{
                            contentJsonObj.put(keyStructName, argJsonValue);
                        }
                    }else{
                        contentJsonObj.put(keyStructName, argJsonValue);
                    }

                   /* if( argJsonValue != null) {
                        if( argJsonValue instanceof  JSONObject) {
                            JSONObject value = (JSONObject) argJsonValue;

                            if (value.keySet() != null && value.keySet().size() > 1) {
                                contentJsonObj.put(keyStructName, value);
                            } else {
                                if( value.keys().hasNext()) {
                                    String keyIntern = value.keys().next();
                                    if (keyIntern.equals(ParserConsts.CONTENT) || keyIntern.equals(ParserConsts.VALUE) || keyIntern.equals(ParserConsts.KEY)) {
                                        contentJsonObj.put(keyStructName, value.get(keyIntern));
                                    } else {
                                        contentJsonObj.put(keyIntern, value.get(keyIntern));
                                    }
                                }
                            }
                        }else{
                            contentJsonObj.put(keyStructName, argJsonValue);
                        }
                    }else{
                        contentJsonObj.put(keyStructName, JSONObject.NULL);
                    }*/

                }
                //contentJsonObj.put(ParserConsts.ARGS,argsJson);
            }
        }

        if (getGqlVariableDefinitionsContainer() != null) {
            JSONObject varsValue = (JSONObject) node.getGqlVariableDefinitionsContainer().generateAsJsonObject();
            if (varsValue != null) {
                if( contentJsonObj == null){
                    contentJsonObj = new JSONObject();
                }
                for (String key : varsValue.keySet()) {
                    contentJsonObj.put(key + "<"+ParserConsts.VAR+">",varsValue.get(key));
                }
               // contentJsonObj.put(ParserConsts.VARS,varsValue);
            }
        }

        TypeName typeConditionNameVal = node.getTypeConditionName();
        if (typeConditionNameVal != null) {
            if( contentJsonObj == null){
                contentJsonObj = new JSONObject();
            }
            contentJsonObj.put("typeConditionName", typeConditionNameVal.toString());

        }
        //JSONArray childArray = new JSONArray();
        JSONObject childAsJsonObj = generateChildAsJson(node);
       // childArray.put(childAsJsonObj);
        if (childAsJsonObj != null) {
            if( contentJsonObj == null){
                contentJsonObj = new JSONObject();
            }
            //contentDataArray.put(childAsJsonObj);
            for (String key : childAsJsonObj.keySet()) {
           ///     contentJsonObj.put(ParserConsts.CHILDS, childAsJsonObj);
                contentJsonObj.put(key, childAsJsonObj.get(key));
            }

        }

        return contentJsonObj;
    }

    private JSONObject generateChildAsJson(GqlTreeNode node) {
        if (node.hasChilds()) {

            JSONObject jsonObject = new JSONObject();

            int i=1;
            for (Gqlable childNode : node.getChildNodes()) {
                Object childNodeValue = generateAsJsonObject(childNode);

                String concatenatedName = childNode.getFieldNameDescriptor().getConcatenatedName();
                if( childNodeValue == null ) {
                    childNodeValue = JSONObject.NULL;//new JSONObject();
                }
                jsonObject.put(/*ParserConsts.CHILD + "_" + (i++)*/concatenatedName,
                        childNodeValue);
            }

            return jsonObject;
        }

        return null;
    }

    public GqlVariableDefinitionsContainer getGqlVariableDefinitionsContainer() {
        return gqlVariableDefinitionsContainer;
    }

    public void setGqlVariableDefinitionsContainer(List<VariableDefinition> gqlVariableDefinitions) {

        this.gqlVariableDefinitionsContainer = new GqlVariableDefinitionsContainer(gqlVariableDefinitions);

    }

    public GqlDirectivesContainer getGqlDirectivesContainer() {
        return gqlDirectivesContainer;
    }

    public void setGqlDirectivesContainer(GqlDirectivesContainer gqlDirectivesContainer) {
        this.gqlDirectivesContainer = gqlDirectivesContainer;
    }

    public TypeName getTypeConditionName() {
        return typeConditionName;
    }

    public void setTypeConditionName(TypeName typeConditionName) {
        this.typeConditionName = typeConditionName;
    }

    public GqlArgumentsContainer getGqlArgumentsContainer() {
        return gqlArgumentsContainer;
    }

    public void setGqlArgumentsContainer(GqlArgumentsContainer gqlArgumentsContainer) {
        this.gqlArgumentsContainer = gqlArgumentsContainer;
    }


}

