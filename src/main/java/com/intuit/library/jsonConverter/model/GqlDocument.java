package com.intuit.library.jsonConverter.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.interfaces.Jsonable;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

public class GqlDocument implements Serializable, Jsonable {

    private Map<String, GqlQueryDefinition> queryDefinitionMap;

    private Map<String, Object> variables;

    private String query;

    public Map<String, GqlQueryDefinition> getQueryDefinitionMap() {
        return queryDefinitionMap;
    }

    public void setQueryDefinitionMap(Map<String, GqlQueryDefinition> queryDefinitionMap) {
        this.queryDefinitionMap = queryDefinitionMap;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void setQuery(String query) { this.query = query;}

    @Override
    public String toString() {
        return "GqlDocument{" +
                "queryDefinitionMap=" + queryDefinitionMap +
                ", variables=" + variables +
                ", query='" + query + '\'' +
                '}';
    }

    public JSONObject toJsonObject() {
        if (this.getQueryDefinitionMap() == null || this.getQueryDefinitionMap().size() == 0) {
            return null;
        }

        JSONObject parentJsonObject = new JSONObject();

        JSONObject operationJsonObj = new JSONObject();
        parentJsonObject.put(ParserConsts.OPERATIONS, operationJsonObj);

        this.getQueryDefinitionMap().forEach((key, value) -> {
            operationJsonObj.put(key, value.toJsonObject());
        });

        if (variables != null && variables.size() > 0) {
            JSONObject varsJsonObj = new JSONObject();
            parentJsonObject.put(ParserConsts.VARIABLES, varsJsonObj);

            variables.forEach((key, value) -> {
              /*  JSONObject varObj = new JSONObject();
                varObj.put(key, value);*/
                varsJsonObj.put(key, value);
            });
        }

        return parentJsonObject;
    }

    public JsonObject toGsonObject() {
        if (this.getQueryDefinitionMap() == null || this.getQueryDefinitionMap().size() == 0) {
            return null;
        }

        JSONObject parentJsonObject = new JSONObject();
        if(this.query != null){
            parentJsonObject.put(ParserConsts.QUERY, this.query);
        }
        JSONObject operationJsonArray = new JSONObject();
        parentJsonObject.put(ParserConsts.OPERATIONS, operationJsonArray);

        this.getQueryDefinitionMap().forEach((key, value) -> {
            operationJsonArray.put(key, value.toJsonObject());
        });

        if (variables != null && variables.size() > 0) {
            JSONObject varsJsonArray = new JSONObject();
            parentJsonObject.put(ParserConsts.VARIABLES, varsJsonArray);

            variables.forEach((key, value) -> {
              /*  JSONObject varObj = new JSONObject();
                varObj.put(key, value);*/
                varsJsonArray.put(key, value);
            });
        }

        JsonObject jsonObject = (JsonObject) JsonParser.parseString(parentJsonObject.toString());
        return jsonObject;
    }

    @Override
    public Object generateAsJsonObject() {
        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {
        if( this.getQueryDefinitionMap() == null || this.getQueryDefinitionMap().size() == 0 ){
            return null;
        }

        if( parentJsonObject == null ){
            parentJsonObject = new JSONObject();
        }

        return parentJsonObject;

    }
}
