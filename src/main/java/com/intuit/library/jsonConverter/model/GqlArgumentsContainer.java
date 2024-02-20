package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.interfaces.Jsonable;
import graphql.language.Argument;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GqlArgumentsContainer implements Serializable, Jsonable {
    private List<GqlArgument> gqlArguments;

    public int size() {

        if(gqlArguments == null  ) return 0;
        return this.getGqlArguments().size();
    }

    public GqlArgumentsContainer(List<Argument> arguments) {

        if( arguments == null || arguments.size() == 0){
            return;
        }

        gqlArguments = new ArrayList<>();

        for (Argument argument : arguments) {
            gqlArguments.add(new GqlArgument(argument));
        }
    }

    public List<GqlArgument> getGqlArguments() {
        return gqlArguments;
    }

    @Override
    public Object generateAsJsonObject() {
        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentObject) {
        if( getGqlArguments() == null || getGqlArguments().size() == 0 ){
            return null;
        }
        JSONObject argumentsJsonObject = new JSONObject();
        for (GqlArgument gqlArgument : getGqlArguments()) {
            gqlArgument.generateAsJsonObject(argumentsJsonObject);
        }
        return argumentsJsonObject;
    }
}
