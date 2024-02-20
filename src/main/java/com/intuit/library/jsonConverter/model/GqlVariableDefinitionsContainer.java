package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.interfaces.Jsonable;
import graphql.language.*;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GqlVariableDefinitionsContainer implements Serializable, Jsonable {
    public int size() {
        return gqlVariableDefinitions == null ? 0 : this.gqlVariableDefinitions.size();
    }

    public List<GqlVariableDefinition> getGqlVariableDefinitions() {
        return gqlVariableDefinitions;
    }

    private List<GqlVariableDefinition> gqlVariableDefinitions;

    public GqlVariableDefinitionsContainer(List<VariableDefinition> gqlVariableDefinitions) {

        if( gqlVariableDefinitions ==null || gqlVariableDefinitions.size() == 0){
            return;
        }

        this.gqlVariableDefinitions = new ArrayList<>();
        for (VariableDefinition variableDefinition : gqlVariableDefinitions) {
            this.gqlVariableDefinitions.add(getGqlVariableDefinition(variableDefinition));
        }
    }

    private static GqlVariableDefinition getGqlVariableDefinition(VariableDefinition variableDefinition) {
        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName( variableDefinition.getName());
        gqlVariableDefinition.setDefaultValue(variableDefinition.getDefaultValue());

        gqlVariableDefinition.setType(variableDefinition.getType());

        gqlVariableDefinition.setDirectives( variableDefinition.getDirectives());
        return gqlVariableDefinition;
    }

   /* private static Value getDefaultValue(VariableDefinition variableDefinition) {
        return variableDefinition.getDefaultValue();
    }*/

    @Override
    public Object generateAsJsonObject() {

        if( this.gqlVariableDefinitions ==null || this.gqlVariableDefinitions.size() == 0){
            return null;
        }

        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {

        if( this.gqlVariableDefinitions ==null || this.gqlVariableDefinitions.size() == 0){
            return null;
        }

        if( parentJsonObject == null ){
            parentJsonObject = new JSONObject();
        }

        JSONObject arrayJson = new JSONObject();
        ((JSONObject)parentJsonObject).put(ParserConsts.VARIABLES, arrayJson);
        int i =1;
        for (GqlVariableDefinition gqlVariableDefinition : gqlVariableDefinitions) {
            arrayJson.put(ParserConsts.ELEM + "_" + i++,  gqlVariableDefinition.generateAsJsonObject());
        }

        return parentJsonObject;

    }
}
