package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.tree.TreeNode;
import graphql.com.google.common.base.Strings;
import graphql.language.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GqlArgument implements Gqlable{

    public static final String IS_VALUE = "__isValue";
    public static final String ENUM_NAME = "__enum";

    private String name;
    private Value value;

    public GqlArgument(graphql.language.Argument arg) {
        if( arg == null ){
            throw new IllegalArgumentException("arg is null");
        }
        name = arg.getName();
        value = arg.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public Value getValue() {
        return value;
    }*/

   /* public void setValue(Value value) {
        this.value = value;
        gqlableList = null;
    }*/

    @Override
    public String toString() {
        return "Argument{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public Object generateAsJsonObject() {
        return generateAsJsonObject(null);
    }

    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {
        if(Strings.isNullOrEmpty(this.name)){
            return null;
        }

        if (parentJsonObject == null) {
            parentJsonObject = new JSONObject();
        }

       if (parentJsonObject instanceof JSONObject) {
           JSONObject parentJsonObj = (JSONObject) parentJsonObject;
           if (value == null) {
                parentJsonObj.put(name, "");
            } else {
                JSONObject innJson = new JSONObject();

                if (value != null) {
                   extractValue(value, innJson);
                }

                // dedact the json
                if(innJson.keySet() != null &&
                innJson.keySet().size() == 1 &&
                innJson.keySet().contains(ParserConsts.VALUE)){
                    parentJsonObj.put(name, innJson.get(ParserConsts.VALUE));
                }else{
                    parentJsonObj.put(name, innJson);
                }
            }
        }else{
            throw new RuntimeException("not supported of type" + parentJsonObject.getClass().getCanonicalName());
        }



        return parentJsonObject;
    }

    private TreeNode treeNode;
    /*private TreeNode extractTreeNodeFromValue(Value val) {
        if( treeNode != null ){
            return treeNode;
        }

        return extractTreeNodeFromValue(null, val);
    }*/
    private JSONObject extractValue(Value val, JSONObject jsonObj) {

        TreeNode treeNode = extractTreeNodeFromValue(val);

        JSONObject treeNodeJson = treeNode.toJson();

        // imrpve performacne
        /*if( jsonObj.keySet().size() == 0){
            jsonObj = treeNodeJson;
        }else {*/
            for (String key : treeNodeJson.keySet()) {
                jsonObj.put(key, treeNodeJson.get(key));
            }
        //}

        return jsonObj;

    }

    private TreeNode extractTreeNodeFromValue( Value val) {
        TreeNode objectValueTreeNode = null;
        if (val instanceof ArrayValue) {
            return extractTreeNodeFromArrayValue((ArrayValue) val,null);
        } else if (val instanceof BooleanValue) {
            BooleanValue booleanValue = (BooleanValue) val;

            objectValueTreeNode = new TreeNode(IS_VALUE, booleanValue.isValue());

        } else if (val instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) val;

            objectValueTreeNode = new TreeNode(ENUM_NAME, enumValue.getName());

        } else if (val instanceof FloatValue) {
            FloatValue floatValue = (FloatValue) val;

            objectValueTreeNode =  new TreeNode(ParserConsts.VALUE, floatValue.getValue().floatValue());

        } else if (val instanceof IntValue) {
            IntValue intValue = (IntValue) val;

            objectValueTreeNode =  new TreeNode(ParserConsts.VALUE, intValue.getValue().toString());

        } else if (val instanceof ObjectValue) {
            objectValueTreeNode = extractTreeNodeFromObjectValue((ObjectValue) val, null);
        } else if (val instanceof StringValue) {
            StringValue stringValue = (StringValue) val;
            objectValueTreeNode =  new TreeNode(ParserConsts.VALUE, stringValue.getValue());

        } else if (val instanceof ScalarValue) {
            ScalarValue scalarValue = (ScalarValue) val;

            objectValueTreeNode =  new TreeNode(ParserConsts.VALUE, ((StringValue) scalarValue).getValue());

        } else if (val instanceof VariableReference) {
            VariableReference variableReference = (VariableReference) val;
            objectValueTreeNode =  new TreeNode(ParserConsts.VALUE, "$"+variableReference.getName());
        }

        return objectValueTreeNode;
    }

    private TreeNode extractTreeNodeFromObjectValue(ObjectValue val, TreeNode objectValueTreeNode) {
        ObjectValue objectValue = val;

        //object values contains of objectFields, each objectField has a name and value
        if(objectValue.getObjectFields() != null) {
            int size = objectValue.getObjectFields().size();

            if( objectValueTreeNode == null)
                objectValueTreeNode = new TreeNode();

            for (ObjectField objectField : objectValue.getObjectFields()) {

                TreeNode objectFieldValueToTreeNode = extractObjectFieldValueToTreeNode(objectField);
                objectFieldValueToTreeNode.setField(true);

                if (size > 1) { // if only one child, replace the objectValueTreeNode parent node with him
                    objectValueTreeNode.addTreeNodeChild(objectFieldValueToTreeNode);
                   // objectValueTreeNode.addChildObjs(objectFieldTreeNode);
                }else{

                    objectValueTreeNode.shallowCopy(objectFieldValueToTreeNode);
                }
            }
        }
        return objectValueTreeNode;
    }

    private TreeNode extractTreeNodeFromArrayValue(ArrayValue arrayValue, TreeNode containerTreeNode) {
        if( arrayValue.getValues() == null){
            return null;
        }


        int size = arrayValue.getValues().size();
        if( size == 0 ){
            return null;
        }
        if( containerTreeNode == null ){
            containerTreeNode = new TreeNode();
        }

        //treeNodeParent.setKey("__key");
        for (Value elementValue : arrayValue.getValues()) {
            if( elementValue instanceof ArrayValue){
                TreeNode innerArrayConatainerTreeNode = new TreeNode();
                extractTreeNodeFromArrayValue((ArrayValue) elementValue,innerArrayConatainerTreeNode );
                containerTreeNode.addTreeNodeChild(innerArrayConatainerTreeNode);
            }else if( elementValue instanceof  ObjectValue){
                if(size == 1){
                    extractTreeNodeFromObjectValue((ObjectValue) elementValue, containerTreeNode);
                }else{
                    TreeNode innerArrayConatainerTreeNode = new TreeNode();
                    extractTreeNodeFromObjectValue((ObjectValue) elementValue, innerArrayConatainerTreeNode);
                    containerTreeNode.addTreeNodeChild(innerArrayConatainerTreeNode);
                }
            }else {
                //if(size == 1){
                    containerTreeNode.addTreeNodeChild(extractTreeNodeFromValue(elementValue));
                /*}else{
                    TreeNode innerArrayConatainerTreeNode = new TreeNode();
                    extractTreeNodeFromObjectValue((ObjectValue) elementValue, innerArrayConatainerTreeNode);
                    containerTreeNode.addChildObjs(innerArrayConatainerTreeNode);
                }*/
            }
        }
        return containerTreeNode;
    }

    private TreeNode extractObjectFieldValueToTreeNode(ObjectField objectField ) {

        TreeNode objectFieldTreeNode = new TreeNode();
        objectFieldTreeNode.setKey(objectField.getName());
        Value value = objectField.getValue();

        if(value instanceof ArrayValue){
            extractTreeNodeFromArrayValue((ArrayValue) value,objectFieldTreeNode);
        }else {
            TreeNode treeNodeFromValue = extractTreeNodeFromValue(value);

            if( treeNodeFromValue != null) {
                if (!treeNodeFromValue.hasChilds() &&
                        treeNodeFromValue.getKey() != null &&
                        objectFieldTreeNode.getKey() != null &&
                        treeNodeFromValue.getKey().equals(objectFieldTreeNode.getKey())) {
                    objectFieldTreeNode.setValue(treeNodeFromValue.getValue());
                } else {
                    if (!treeNodeFromValue.hasChilds()){
                        if( treeNodeFromValue.getKey() != null && (
                                treeNodeFromValue.getKey().equals(ParserConsts.VALUE) ||
                                        treeNodeFromValue.getKey().equals(ParserConsts.CONTENT)
                        )){
                            objectFieldTreeNode.setValue(treeNodeFromValue.getValue());
                        }else{
                            objectFieldTreeNode.setValue(treeNodeFromValue);
                        }
                    }else{
                        if( treeNodeFromValue.getValue() != null ) {
                            objectFieldTreeNode.setValue(treeNodeFromValue.getValue());
                        }

                        objectFieldTreeNode.addTreeNodeChilds(treeNodeFromValue.getChilds());

                    }
                }
            }
        }

        return objectFieldTreeNode;

    }

    @Override
    public boolean hasChilds() {
        List<Gqlable> childNodes = getChildNodes();

        return (childNodes == null || childNodes.size() == 0) ? false : true;

    }

    @Override
    public FieldNameDescriptor getFieldNameDescriptor() {
        return new FieldNameDescriptor(name,"",graphql.language.Argument.class);
    }

    private List<Gqlable> gqlableList;
    @Override
    public List<Gqlable> getChildNodes() {
        if( this.value == null ){
            return null;
        }
        if( gqlableList != null ){
            return gqlableList;
        }
        TreeNode treeNode = extractTreeNodeFromValue(this.value);
        if( treeNode.getChilds() == null || treeNode.getChilds().size() == 0){
            List<Gqlable> entity = new ArrayList<>();
            entity.add(treeNode);
            return entity;
        }
        gqlableList = treeNode.getChilds().stream().map(a -> (Gqlable) a)
                .collect(Collectors.toList());

        return gqlableList;
    }
}
