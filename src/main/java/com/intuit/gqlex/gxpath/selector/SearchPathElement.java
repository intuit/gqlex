package com.intuit.gqlex.gxpath.selector;

import com.intuit.gqlex.common.DocumentElementType;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;

import java.util.Map;

public class SearchPathElement {

    public static final String NAME = "name";
    public static final String OPERATION_NAME = "operation_name";

    
    public static final String ALIAS = "alias";

    public static final String DOC_ELEM_TYPE = "doc_elem_type";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String ANY_VALUE = "ANY_VALUE";
    private SelectionAttributeMap selectionAttributeMap;

    public String getName(){
        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
        selectionAttributeMap.getAttributes().size() == 0){
            return  null;
        }

        return selectionAttributeMap.getAttributes().get(NAME);
    }

    public String getByKey(String key){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));

        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
                selectionAttributeMap.getAttributes().size() == 0){
            return  null;
        }

        return selectionAttributeMap.getAttributes().get(key);
    }

    public String getOperationName(){
        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
                selectionAttributeMap.getAttributes().size() == 0){
            return  null;
        }

        return selectionAttributeMap.getAttributes().get(OPERATION_NAME);
    }

    public String getAlias(){
        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
                selectionAttributeMap.getAttributes().size() == 0){
            return  null;
        }

        return selectionAttributeMap.getAttributes().get(ALIAS);
    }


    public String getType(){
        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
                selectionAttributeMap.getAttributes().size() == 0){
            return  null;
        }

        return selectionAttributeMap.getAttributes().get(DOC_ELEM_TYPE);
    }

    public Boolean isAnyValue(){
        if( selectionAttributeMap == null || selectionAttributeMap.getAttributes() == null ||
                selectionAttributeMap.getAttributes().size() == 0){
            return  false;
        }

        return Boolean.valueOf(selectionAttributeMap.getAttributes().get(ANY_VALUE));
    }


    public SearchPathElement(DocumentElementType documentElementType, Map<String, String> attributes) {

        if (selectionAttributeMap == null) {
            selectionAttributeMap = new SelectionAttributeMap();
        }

        if (documentElementType != null)
            selectionAttributeMap.add(DOC_ELEM_TYPE, documentElementType.getShortName());

        if (attributes == null) {
            return;
        }

        if( !attributes.isEmpty()) {
            for (String key : attributes.keySet()) {
                selectionAttributeMap.add(key, attributes.get(key));
            }
        }
    }

    /*public SearchPathElement(DocumentElementType type) {
        this(type, null);
    }*/

    public SearchPathElement(Map<String, String> attributes) {
        this(null, attributes);
    }

    public SearchPathElement add(String attributeKey, String attributeValue) {
        selectionAttributeMap.add(attributeKey, attributeValue);
        return this;
    }

    public SelectionAttributeMap getSelectionAttributeMap() {
        return selectionAttributeMap;
    }

    @Override
    public String toString() {
        return "SearchPathElement{" +
                "selectionAttributeMap=" + selectionAttributeMap +
                '}';
    }
}
