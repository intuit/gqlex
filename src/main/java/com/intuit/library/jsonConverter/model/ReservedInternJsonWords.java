package com.intuit.library.jsonConverter.model;

public enum ReservedInternJsonWords {
    FIELD("__field"),
    ARGUMENTS("__arguments"),
    VARIABLES("variables"),
    OPERATIONS("__operations"),
    DIRECTIVES_CONTENT("__directs"),
    CONTENT("__content"),
    VAR("__var"),
    ARG("__arg"),
    ELEM("__elem"),
    OPERATION_TYPE("__operationType"),
    KEY("__key"),
    VALUE("__value");

    public static boolean isValueNameIsOfSingleElement(String valueName) {
        if (valueName == null || valueName.isEmpty()) {
            return false;
        }

        if ( valueName.equals(FIELD.label) ||
                valueName.equals(ELEM.label) ||
                valueName.equals(VALUE.label) ||
                valueName.equals(VAR.label) ||
                valueName.equals(ARG.label) ||
                valueName.equals(OPERATION_TYPE.label) ||
                valueName.equals(KEY.label)){
            return true;
        }

        return false;
    }

    public String getLabel() {
        return label;
    }

    private final String label;

    ReservedInternJsonWords(String label) {

        this.label = label;
    }

    public static boolean isContainmentLabel(String label){
        if( isValueNameIsOfSingleElement(label) ){
            return true;
        }
        if( label == null ){
            return false;
        }
        if ( label.equals(ARGUMENTS.label) ||
                label.equals(VARIABLES.label) ||
                label.equals(OPERATIONS.label) ||
                label.equals(DIRECTIVES_CONTENT.label) ||
                label.equals(CONTENT.label) ||
                label.equals(KEY.label)){
            return true;
        }

        return false;
    }
}
