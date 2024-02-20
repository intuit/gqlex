package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.interfaces.Jsonable;
import graphql.language.Directive;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GqlDirectivesContainer implements Serializable, Jsonable {

    private List<GqlDirective> directives;

    public GqlDirectivesContainer(List<Directive> directives) {
        for (Directive directive : directives) {
            this.add(new GqlDirective(directive));
        }
    }

    private void add(GqlDirective gqlDirective) {
        if( directives  == null )
            directives = new ArrayList<>();
        directives.add(gqlDirective);
    }


    public List<GqlDirective> getGqlDirectives() {
        return directives;
    }

    public void setGqlDirectives(List<GqlDirective> directives) {
        this.directives = directives;
    }


    public int size() {
        return directives == null ? 0 : directives.size();
    }

    @Override
    public Object generateAsJsonObject() {

        return generateAsJsonObject(null);

    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {

        if( this.getGqlDirectives() == null || this.getGqlDirectives().size() == 0 ){
            return null;
        }

        if( parentJsonObject ==null){
            parentJsonObject = new JSONObject();
        }
        if( parentJsonObject instanceof JSONObject){
            //JSONArray cObj = new JSONArray();
            JSONObject cObj = new JSONObject();
            ((JSONObject)parentJsonObject).put(ParserConsts.CONTENT, cObj);

            for (GqlDirective directive : getGqlDirectives()) {
                Object value = directive.generateAsJsonObject();
                if( value != null) {
                    JSONObject jsonObject = (JSONObject)value;

                    if( jsonObject.keySet().size() == 1){
                        String key = jsonObject.keys().next();
                        cObj.put(key, jsonObject.get(key));
                    }else {
                        cObj.put(ParserConsts.CONTENT, jsonObject);
                    }
                }
            }
        }else{
            throw new RuntimeException("not supported of type" +  parentJsonObject.getClass().getCanonicalName());
        }

        return parentJsonObject;
    }

}
