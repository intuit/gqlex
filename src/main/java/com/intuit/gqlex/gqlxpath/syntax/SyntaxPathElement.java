package com.intuit.gqlex.gqlxpath.syntax;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.gqlxpath.selector.SearchElementConstants;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SyntaxPathElement {

    private boolean isStartElem = false;

    public boolean isStartElem() {
        return isStartElem;
    }

    private boolean isAnyField = false;
    private String name;
    private boolean isMultiSelect = false;
    private Map<String, String> attributes;

    public boolean isValid(){
        if( isAnyField){
            return true;
        }
        return !Strings.isNullOrEmpty(name);
    }

    public SyntaxPathElement() {
    }

    public boolean isAnyField() {
        return isAnyField;
    }

    public void setAnyField(boolean anyField) {
        isAnyField = anyField;
    }

    /* public gXPathElement(DocumentElementType documentElementType) {

             if( documentElementType.equals(DocumentElementType.OPERATION_DEFINITION) ||
                     documentElementType.equals(DocumentElementType.MUTATION_DEFINITION)){
                 isStartElem = true;
                 this.name = documentElementType.getShortName();;
             }else{
                 throw new IllegalArgumentException(MessageFormat.format("Support only {0} and {1}",
                         DocumentElementType.OPERATION_DEFINITION.getShortName(),
                         DocumentElementType.MUTATION_DEFINITION.getShortName()));
             }


         }*/
    public SyntaxPathElement(String fieldName, DocumentElementType documentElementType) {
        this(fieldName,documentElementType, false);
    }
    public SyntaxPathElement(String fieldName, DocumentElementType documentElementType, boolean isMultiSelect) {
        this.name = fieldName;
        this.isMultiSelect = isMultiSelect;

        if (   (name.equals(DocumentElementType.OPERATION_DEFINITION.getShortName()) && documentElementType.equals(DocumentElementType.OPERATION_DEFINITION))
                ||
                (name.equals(DocumentElementType.MUTATION_DEFINITION.getShortName()) && documentElementType.equals(DocumentElementType.MUTATION_DEFINITION)))  {
            isStartElem = true;
        } else {
            switch (documentElementType) {
                case DIRECTIVE:
                case INLINE_FRAGMENT:
                case FRAGMENT_DEFINITION:
                case FRAGMENT_SPREAD:
                case VARIABLE_DEFINITION:
                case ARGUMENT:
                    addAttribute(SearchElementConstants.TYPE, documentElementType.getShortName());
                    break;

            }
        }


    }

    public SyntaxPathElement(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

   /* public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }*/

    public void addAttributes(Map<String, String> attributes) {


        if (attributes != null && !attributes.isEmpty()) {
            initAttributes();

            this.attributes.putAll(attributes);
        }
    }

    private void initAttributes() {
        if (this.attributes == null) this.attributes = new HashMap<>();
    }

    public void addAttribute(String name, String value) {
        Preconditions.checkNotNull(name);

        initAttributes();

        this.attributes.put(name, value);
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        if (this.isStartElem) {
            if( isMultiSelect ) {
                stringBuilder.append("//");
            }else{
                stringBuilder.append("/");
            }
        }

        if( isAnyField){
            return SearchElementConstants.ANY_SEARCH_VALUE;
        }

        stringBuilder.append(this.name);
        if (this.attributes != null && this.attributes.size() > 0) {
            stringBuilder.append(SearchElementConstants.FIELD_LABELS_START_SIGN);

            int i = 0;
            for (Map.Entry<String, String> stringStringEntry : attributes.entrySet()) {
                if (stringStringEntry.getValue() != null) {
                    stringBuilder.append(MessageFormat.format("{0}{1}{2}", stringStringEntry.getKey(),SearchElementConstants.LABEL_COMPARER, stringStringEntry.getValue()));
                } else {
                    stringBuilder.append(MessageFormat.format("{0}", stringStringEntry.getKey()));
                }
                if (i + 1 < attributes.size()) {
                    stringBuilder.append(" ");
                }
                i++;
            }


            stringBuilder.append(SearchElementConstants.FIELD_LABELS_END_SIGN);
        }

        return stringBuilder.toString();
    }
}
