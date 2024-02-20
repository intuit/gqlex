package com.intuit.library.common;

import com.intuit.library.jsonConverter.ParserConsts;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RawPayload {

    private static final Logger logger = LoggerFactory.getLogger(RawPayload.class);


    private org.json.simple.JSONObject jsonObjectParsed;
    private String queryValue;
    private Object variables;

    private Map<String, Object> variablesMap;

    public RawPayload() {
    }

    public RawPayload(RawPayload rawPayload) {
        if( rawPayload.getJsonObjectParsed() == null) {
            Preconditions.checkArgument(rawPayload != null);
            this.setQueryValue(rawPayload.getQueryValue());
            this.setVariables(rawPayload.getVariables());
        }else {
            this.setJsonObjectParsed(rawPayload.getJsonObjectParsed());
        }
    }

    public Map<String, Object> getVariablesMap(){
        if(variablesMap != null){
            return variablesMap;
        }
        if(this.getVariables() != null ){
            variablesMap = new HashMap<>();
            org.json.simple.JSONObject varsJsonObject = (org.json.simple.JSONObject) this.getVariables();

            if( logger.isDebugEnabled())
                logger.debug("\n\nVariables : \n=================\n\n" + varsJsonObject.toJSONString() + "\n");

            for (Object key : varsJsonObject.keySet()) {
                variablesMap.put(key.toString(), varsJsonObject.get(key));
            }
        }
        return variablesMap;
    }

    public JSONObject getJsonObjectParsed() {
        return jsonObjectParsed;
    }

    public void setJsonObjectParsed(JSONObject payloadJson) {

        Preconditions.checkNotNull(payloadJson);

        this.jsonObjectParsed = payloadJson;
        this.queryValue = ( (String) payloadJson.get(ParserConsts.QUERY) );
        if(Strings.isNullOrEmpty(queryValue)){
            throw new IllegalArgumentException("Query value is null or empty");
        }
        this.variables = payloadJson.get(ParserConsts.VARIABLES);
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    public Object getVariables() {
        return variables;
    }

    public void setVariables(Object variables) {
        this.variables = variables;
    }
}
