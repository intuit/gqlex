package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.interfaces.Jsonable;
import graphql.language.Argument;
import graphql.language.Directive;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GqlDirective implements Serializable, Jsonable, Gqlable {

    private String name;
    private List<GqlArgument> gqlArguments;

    public GqlDirective(Directive directive) {

        String prefix = "";
        if( !directive.getName().startsWith("@")){
            prefix = "@";
        }
        this.name = prefix + directive.getName();

        List<Argument> args = directive.getArguments();
        if(args != null && args.size() > 0 ) {
            gqlArguments = new ArrayList<>(args.size());
            for (Argument arg : args) {
                this.gqlArguments.add(new GqlArgument(arg));
            }
        }
    }

    public String getName() {
        return name;
    }

    /*public void setName(String name) {
        this.name = name;
    }*/

    public List<GqlArgument> getArguments() {
        return gqlArguments;
    }

    /*public void setArguments(List<GqlArgument> gqlArguments) {
        this.gqlArguments = gqlArguments;
    }*/

    @Override
    public String toString() {
        return "GqlDirective{" +
                "name='" + name + '\'' +
                ", arguments=" + gqlArguments +
                '}';
    }

    @Override
    public Object generateAsJsonObject() {
        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {

        if( parentJsonObject == null){
            parentJsonObject = new JSONObject();
        }

        JSONObject argumentsToJsonObject = addDirectiveArgumentsToJsonObject();

        if( argumentsToJsonObject != null) {
            ((JSONObject) parentJsonObject).put(this.getName(), argumentsToJsonObject);
        }else{
            ((JSONObject) parentJsonObject).put(this.getName(), JSONObject.NULL);
        }

        return parentJsonObject;
    }

    private JSONObject addDirectiveArgumentsToJsonObject() {
        List<GqlArgument> arguments = this.getArguments();
        if( arguments == null || arguments.size() == 0){
            return  null;
        }
        List<GqlArgument> args = arguments;
        JSONObject directiveJson = null;
        if( args != null && args.size() > 0){
            directiveJson = new JSONObject();
            JSONObject argumentsJson = new JSONObject();

            directiveJson.put(ParserConsts.ARGUMENTS,argumentsJson);
            int i=1;
            for (GqlArgument arg : args) {
                JSONObject argobj = new JSONObject();
                arg.generateAsJsonObject(argobj);
                argumentsJson.put(ParserConsts.ELEM + "_"+ i++, argobj);
            }
        }

        return directiveJson;
    }

    @Override
    public boolean hasChilds() {
        List<GqlArgument> arguments = getArguments();
        return arguments != null && arguments.size() > 0;
    }

    @Override
    public FieldNameDescriptor getFieldNameDescriptor() {
        return new FieldNameDescriptor(name, "", Directive.class);
    }

    @Override
    public List<Gqlable> getChildNodes() {
        List<GqlArgument> arguments = getArguments();
        if( arguments ==null ){
            return null;
        }
        return arguments.stream().map(a->(Gqlable)a).collect(Collectors.toList());
    }
}
