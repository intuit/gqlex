package com.intuit.library.jsonConverter.model.tree;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.FieldNameDescriptor;
import com.intuit.library.jsonConverter.model.Gqlable;
import graphql.com.google.common.base.Strings;

import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class TreeNode implements Gqlable {

    private Set<Integer> mapPreventCircularRef = new HashSet<>();
    List<TreeNode> childs;


    private String key;
    private Object value;

    private boolean isField = false;

    public boolean isField() {
        return isField;
    }

    public void setField(boolean field) {
        isField = field;
    }

    public TreeNode(String key, Object value) {
        /*if (Strings.isNullOrEmpty(key)) {
            throw new IllegalArgumentException("key is null");
        }*/
        this.key = key;
        this.value = value;
    }

    public TreeNode() {

    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "mapPreventCircularRef=" + mapPreventCircularRef +
                ", childs=" + childs +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", isField=" + isField +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JSONObject toJson() {
        JSONObject containerJsonObj = new JSONObject();
        toJson(containerJsonObj, this);
        return containerJsonObj;
    }

    private void toJson(JSONObject containerJsonObject, TreeNode treeNode) {
        boolean isMainKeySet = false;
        String key = ParserConsts.KEY;
        if (!Strings.isNullOrEmpty(treeNode.key)) {
            key = treeNode.getKey();
            isMainKeySet = true;
        }

        Object value = JSONObject.NULL;

        // regular value
        boolean isMainValueSet = false;
        if (!(treeNode.value instanceof TreeNode)) {
            if (treeNode.value == null) {
                value = JSONObject.NULL;
            } else {
                value = treeNode.value;
                isMainValueSet = true;
            }
        }
        if (isMainKeySet) {
            containerJsonObject.put(key, value);
        }

        if (treeNode.hasChilds()) {
            List<TreeNode> childs = treeNode.getChilds();

            int childSize = childs.size();
            if (childSize == 0) {
                return;
            }

            // if childs has name and value fields, links  both fields into one object and return
            if (childSize == 2) {
                TreeNode treeNode1 = childs.get(0);
                TreeNode treeNode2 = childs.get(1);
                boolean isNameSet = false;
                boolean isValueSet = false;
                String nameChild = "";
                String valueChild = "";
                String key1 = treeNode1.getKey();
                String treeNode1KeyVal = key1 != null ? key1.toLowerCase() : null;
                String key2 = treeNode2.getKey();
                String treeNode2KeyVal =  key2 != null ? key2.toLowerCase() : null;
                if (key1 != null && treeNode1KeyVal.equals("name")) {
                    isNameSet = true;
                    nameChild = treeNode1.getValue().toString();
                } else if (key2 != null && treeNode2KeyVal.equals("name")) {
                    isNameSet = true;
                    nameChild = treeNode2.getValue().toString();
                }

                if (key1 != null && treeNode1KeyVal.equals("value")) {
                    isValueSet = true;
                    valueChild = treeNode1.getValue().toString();
                } else if (key2 != null && treeNode2KeyVal.equals("value")) {
                    isValueSet = true;
                    valueChild = treeNode2.getValue().toString();
                }

                if (isNameSet && isValueSet) {
                    containerJsonObject.put(nameChild, valueChild);
                    return;
                }

            }

            JSONObject containerChildJsonObj = new JSONObject();
            //JSONObject containerJson = containerJsonObject;
            /*if (isMainKeySet) {
                containerJson = containerChildJsonObj;
                containerJsonObject.put( isMainValueSet ? ParserConsts.CONTENT : key, containerChildJsonObj);
            }*/

            if (isMainKeySet) {
                containerJsonObject.put((isMainKeySet && isMainValueSet) ? ParserConsts.CONTENT : key, containerChildJsonObj);
            }
            JSONObject containerJson = isMainKeySet ? containerChildJsonObj : containerJsonObject;

            if (childSize == 1) {
                toJson(containerJson, childs.get(0));
            } else {
                int i = 1;

                for (TreeNode childTreeNode : childs) {
                    int hashCode = childTreeNode.hashCode();
                    if( mapPreventCircularRef.contains(hashCode) ){
                        continue;
                    }else{
                        mapPreventCircularRef.add(hashCode);
                    }

                    if (childTreeNode.hasChilds()) {
                        JSONObject elemChildJsonObj = new JSONObject();
                        String key1 = ParserConsts.ELEM + "_" + i++;
                        containerJson.put(key1, elemChildJsonObj);

                        toJson(elemChildJsonObj, childTreeNode);
                        // reduce structure in case only one child
                        /*if( elemChildJsonObj.length() == 1){
                            containerChildJsonObj.remove(key1);
                            String next = elemChildJsonObj.keys().next();
                            containerChildJsonObj.put(next,elemChildJsonObj.get(next) );
                        }*/
                    } else {
                        toJson(containerJson, childTreeNode);
                    }
                }
            }
        }
    }


    /*public JSONObject toJsonObsolete() {
        JSONObject container = new JSONObject();

        // calculate the value
        boolean isKeySet = false;
        String key = ParserConsts.KEY;
        if(!Strings.isNullOrEmpty( this.key )){
            key = this.getKey();
            isKeySet = true;
        }

        Object value = null;

        boolean isValueSet = false;
        // regular value
        if( ! ( this.value instanceof TreeNode) ){
            value = this.value;
            isValueSet = true;
        }

        int size = 0;
        if( this.childs != null)
            size = this.childs.size();

        if (this.hasChilds()) {
            JSONObject childrens = new JSONObject();

            if( size > 1) {

                int i = 1;
                for (TreeNode child : childs) {
                    int hashCode = child.hashCode();
                    if( mapPreventCircularRef.contains(hashCode) ){
                        continue;
                    }else{
                        mapPreventCircularRef.add(hashCode);
                    }

                    JSONObject json = child.toJson();

                    if(child.hasChilds()){

                        String next = json.keys().next();
                        String keyStruct = ParserConsts.ELEM + "_" + i++;
                        if(next.equals(ParserConsts.CONTENT)){
                            JSONObject jsonObject = (JSONObject)json.get(next);

                            int size1 = jsonObject.keySet().size();
                            if(size1 == 2){

                                Boolean nameNameExist = jsonObject.keySet().contains("name");
                                Boolean valueNameExist = jsonObject.keySet().contains("value");
                                if( nameNameExist && valueNameExist){
                                    JsonObjectUtils.addToJsonObject(childrens, jsonObject.getString("name"), jsonObject.get("value"));
                                }else{
                                    JsonObjectUtils.addToJsonObject(childrens,keyStruct, jsonObject);
                                }
                            }else {
                                JsonObjectUtils.addToJsonObject(childrens,keyStruct, jsonObject);
                            }
                        }else{
                            JsonObjectUtils.addToJsonObject(childrens,keyStruct, json);
                        }


                    }else{
                        for (String keyVal : json.keySet()) {
                            JsonObjectUtils.addToJsonObject(childrens, keyVal, json.get(keyVal));
                        }
                    }

                }
            }else{
                TreeNode childTreeNode = getChilds().get(0);
                int hashCode = childTreeNode.hashCode();
                if( mapPreventCircularRef.contains(hashCode) ){
                    return null;
                }else{
                    mapPreventCircularRef.add(hashCode);
                }

                if( value == null ) {
                    value = childTreeNode.toJson();
                }else{
                    JsonObjectUtils.addToJsonObject(childrens,ParserConsts.ELEM, childTreeNode.toJson());
                }
            }

            if(childrens != null && childrens.keySet().size() > 0) {
                int size1 = childrens.keySet().size();
                if (size1 == 2) {
                    Boolean nameNameExist = childrens.keySet().contains("name");
                    Boolean valueNameExist = childrens.keySet().contains("value");
                    if( nameNameExist && valueNameExist) {
                        JsonObjectUtils.addToJsonObject(container,childrens.getString("name"), childrens.get("value"));
                    }else{
                        JsonObjectUtils.addToJsonObject(container,ParserConsts.CONTENT, childrens);
                    }
                }else{
                    JsonObjectUtils.addToJsonObject(container,ParserConsts.CONTENT, childrens);
                }
            }
        }



        if(isKeySet ) {
            JsonObjectUtils.addToJsonObject(container, key, value == null ? JSONObject.NULL : value);
        }

        return container;
    }*/


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {

        this.value = value;

        if (this.value instanceof TreeNode) {
            TreeNode treeNode = (TreeNode) this.value;
            this.addTreeNodeChild(treeNode);
        }
    }

    public List<TreeNode> getChilds() {
        return childs;
    }

    public void addTreeNodeChild(TreeNode treeNode) {
        if( treeNode == null ){
            return;
        }
        if (this.childs == null) {
            this.childs = new ArrayList<>();
        }

        this.childs.add(treeNode);
    }

    public void addTreeNodeChilds(List<TreeNode> treeNodes) {
        if( treeNodes == null || treeNodes.isEmpty()){
            return;
        }

        if (this.childs == null) {
            this.childs = new ArrayList<>();
        }

        this.childs.addAll(treeNodes);
    }

    @Override
    public boolean hasChilds() {
        return childs != null && childs.size() > 0;
    }

    @Override
    public FieldNameDescriptor getFieldNameDescriptor() {
        FieldNameDescriptor fieldNameDescriptor = new FieldNameDescriptor();

        fieldNameDescriptor.addNameAndValue(this.getKey(), this.getValue(), null, false);

        return fieldNameDescriptor;
    }

    @Override
    public List<Gqlable> getChildNodes() {
        if (childs == null) {
            return null;
        }
        return childs.stream().map(a -> (Gqlable) a).collect(Collectors.toList());
    }

    @Override
    public Object generateAsJsonObject() {

        throw new RuntimeException("not supported yet");
    }


    @Override
    public Object generateAsJsonObject(Object parentJsonObject) {
        throw new RuntimeException("not supported");
    }

    public void shallowCopy(TreeNode objectFieldValueToTreeNode) {
        this.setKey( objectFieldValueToTreeNode.getKey());
        this.setValue(objectFieldValueToTreeNode.getValue());
        if( this.hasChilds() ){
            this.childs=null;
        }
        if( objectFieldValueToTreeNode.hasChilds()) {
            this.addTreeNodeChilds(objectFieldValueToTreeNode.getChilds());
        }

    }
}
