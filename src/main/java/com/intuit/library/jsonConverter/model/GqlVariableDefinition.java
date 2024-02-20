package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import graphql.language.*;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class GqlVariableDefinition implements Serializable, Gqlable {
    private String name;
    private String defaultValue;
    private String type;
    private GqlDirectivesContainer gqlDirectivesContainer;

    public void setName(String name) {

        String prefix = "";
        if( !name.startsWith("$")){
            prefix = "$";
        }
        this.name = prefix + name;
    }

    public String getName() {
        return name;
    }

    public void setDefaultValue(Value defaultValue) {
        if( defaultValue == null ) {
            return;
        }

        this.defaultValue = extractDefaultValue(defaultValue);;
    }


    private String extractDefaultValue(Value val) {
        if (val instanceof ArrayValue) {
            ArrayValue elems = (ArrayValue) val;
            int size = elems.getValues().size();
            if(size == 0){
                return "null";
            }
            StringBuilder strs = new StringBuilder();

            strs.append("__array_");
            for (int j = 0; j < size; j++) {
                strs.append(extractDefaultValue(elems.getValues().get(j)));
                if( j+1<size){
                    strs.append(".");
                }
            }

            return strs.toString();
        } else if (val instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) val;

            return Boolean.toString(booleanValue.isValue());

        } else if (val instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) val;
            return enumValue.getName();

        } else if (val instanceof FloatValue) {
            FloatValue floatValue = (FloatValue) val;

           return floatValue.getValue().toString();

        } else if (val instanceof IntValue) {
            IntValue intValue = (IntValue) val;

            return intValue.getValue().toString();

        } else if (val instanceof ObjectValue) {
            ObjectValue objectValue = (ObjectValue) val;

            int size = objectValue.getObjectFields().size();
            if(size == 0){
                return "null";
            }
            StringBuilder strs = new StringBuilder();
            strs.append("__objVal_");
            for (int i = 0; i < size; i++) {
                ObjectField objectField = objectValue.getObjectFields().get(i);
                if( objectField == null) {
                    return "null";
                }
                if( objectField.getName() != null) {
                    strs.append(objectField.getName());
                }

                if( objectField.getValue() != null){
                    strs.append("<" + extractDefaultValue(objectField.getValue()) + ">");
                }

                if( i+1<size){
                    strs.append(".");
                }
            }


            return strs.toString();
          //  return extractStringFromObjectValue((ObjectValue)val);
        } else if (val instanceof StringValue) {
            StringValue stringValue = (StringValue) val;
            return stringValue.getValue();

        } else if (val instanceof ScalarValue) {
            ScalarValue scalarValue = (ScalarValue) val;

            return scalarValue.toString();

        } else if (val instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) val;
            return "$"+variableReference.getName();
        }

        return "null";
    }


    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setType(Type type) {
        String  typeValue = "";
        if( type instanceof ListType){
            setType(((ListType)type).getType());
        }else if( type instanceof TypeName){
            typeValue = ((TypeName)type).getName();
        }else{
            setType(((NonNullType)type).getType());
            return;
        }
        this.type = typeValue;
    }

    public String getType() {
        return type;
    }

    public void setDirectives(List<Directive> directives) {
        if( directives == null || directives.size() == 0){
            return;
        }
        gqlDirectivesContainer = new GqlDirectivesContainer(directives);

    }

    /*public Boolean hasDirectives(){
        if( gqlDirectivesContainer == null || gqlDirectivesContainer.size() == 0){
            return false;
        }

        return true;
    }*/

    public GqlDirectivesContainer getDirectives() {
        return gqlDirectivesContainer;
    }

    @Override
    public String toString() {
        return "GqlVariableDefinition{" +
                "name='" + name + '\'' +
                ", defaultValue=" + defaultValue +
                ", type=" + type +
                ", gqlDirectives=" + gqlDirectivesContainer +
                '}';
    }

    @Override
    public Object generateAsJsonObject() {
        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {

        /*if( this.getDirectives() == null || this.getDirectives().size() == 0 ){
            return null;
        }*/

        if( parentJsonObject == null ){
            parentJsonObject = new JSONObject();
        }


        JSONObject containerJsonObj = new JSONObject();
        ((JSONObject)parentJsonObject).put(this.getName(), containerJsonObj);
        //JSONObject jsonObject = new JSONObject();
        containerJsonObj.put("defaultValue", this.getDefaultValue());
        containerJsonObj.put("type", this.getType());

       // containerJsonObj.put(jsonObject);
        GqlDirectivesContainer directives = this.getDirectives();
        if( directives != null ) {
            containerJsonObj.put(ParserConsts.CONTENT,directives.generateAsJsonObject());
        }


        return parentJsonObject;

    }

    @Override
    public boolean hasChilds() {
        List<Gqlable> childNodes = getChildNodes();
        return childNodes != null && childNodes.size()>0;
    }

    @Override
    public FieldNameDescriptor getFieldNameDescriptor() {

        FieldNameDescriptor fieldNameDescriptor = new FieldNameDescriptor();

        boolean isDefault = getDefaultValue()!=null? true: false;
        fieldNameDescriptor.addNameAndValue(getName(),this.getDefaultValue(),
                VariableDefinition.class,
                isDefault);

        return fieldNameDescriptor;
    }

    private List<Gqlable> gqlableList;

    @Override
    public List<Gqlable> getChildNodes() {
        if( this.gqlDirectivesContainer == null || this.gqlDirectivesContainer.getGqlDirectives() == null){
            return null;
        }

        if( gqlableList != null ){
            return gqlableList;
        }

        gqlableList = this.gqlDirectivesContainer.getGqlDirectives().stream().map(a->(Gqlable)a).collect(Collectors.toList());

        return gqlableList;
    }
}
