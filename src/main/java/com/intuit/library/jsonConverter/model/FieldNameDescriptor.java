package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.interfaces.Jsonable;
import graphql.com.google.common.base.Strings;
import graphql.language.*;
import org.json.JSONObject;

import java.io.Serializable;

public class FieldNameDescriptor implements Serializable, Jsonable {

    public static final String ALIAS = "__alias";
    private String name;
    private String alias;
    private  String typeName;

    public FieldNameDescriptor(){

    }
    public FieldNameDescriptor(String name, String alias) {

        this(name, alias, ParserConsts.FIELD);

    }

    public FieldNameDescriptor(String name, String alias, String typeName) {

        extractNameVal(name, null, typeName, false);
        this.alias = alias;
        this.typeName = typeName;

    }

    public void addNameAndValue(String name, Object value, Class type, boolean isDefaultValue){


        applyValues(name,alias, value, type,isDefaultValue);
    }
    public FieldNameDescriptor(String name, String alias, Class type) {

        applyValues(name,alias, null, type, false);
    }

    private void applyValues(String name, String alias, Object value, Class type, boolean isDefaultValue){

        extractNameVal(name, value,type !=null? type.toString() : null, isDefaultValue);
        this.alias = alias;
        if(type !=null) {
            String typeNameVal = type.getName();
            this.setTypeName(typeNameVal.substring(typeNameVal.lastIndexOf(".") + 1));
        }
    }

    private void extractNameVal(String name, Object value, String type, boolean isDefaultValue) {
        String typePrefix = null;
        if(type !=null) {
            if( type.equals(VariableReference.class.toString())){
                typePrefix = "varef";
            }else if( type.equals(Argument.class.toString())){
                typePrefix = "arg";
            }else if( type.equals(Directive.class.toString())){
                typePrefix = "directv";
            }else if( type.equals(InlineFragment.class.toString())){
                typePrefix = "inlfrag";
            }
        }
        String descriptor = "";
        if(value == null || Strings.isNullOrEmpty(value.toString())){
            descriptor = name;
        }else{
            if(isDefaultValue){
                descriptor = name + "[default:" + value + "]";
            }else {
                descriptor = name + "[" + value + "]";
            }
        }

        if(!Strings.isNullOrEmpty(typePrefix)){
            descriptor =  typePrefix+ "<" + descriptor + ">";
        }

        this.name = descriptor;
    }

    public String getName() {
        return name;
    }
/*public void setName(String name) {
        this.name = name;
    }*/

    public String getAlias() {
        if( alias == null ){
            return null;
        }
        if( alias.trim().equalsIgnoreCase("null")){
            return null;
        }
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getConcatenatedName(){
        String alias1 = getAlias();

        if(! Strings.isNullOrEmpty(alias1)) {
            return alias1 +":"+getName();
        }
        return getName();

    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Object generateAsJsonObject() {
        JSONObject parentJsonObject = new JSONObject();
        return generateAsJsonObject(parentJsonObject);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {


        String name = this.getName();
        if (Strings.isNullOrEmpty(name)) {
            return null;
        }else{
            String alias = this.getAlias();
            if (!Strings.isNullOrEmpty(alias)) {

                if( parentJsonObject == null ){
                    parentJsonObject = new JSONObject();
                }
                JSONObject parentJsonObj = (JSONObject) parentJsonObject;

                parentJsonObj.put(alias, ALIAS);

            }

        }

        return parentJsonObject;
    }

    @Override
    public String toString() {
        return "FieldNameDescriptor{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
