package com.intuit.gqlex.common;

import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public enum DocumentElementType {


    DOCUMENT("doc"),
    //FRAGMENT("frag"),
    DIRECTIVE("direc"),
    FIELD("fld"),
    MUTATION_DEFINITION("mutation"),
    OPERATION_DEFINITION("query"),
    INLINE_FRAGMENT("infrag"),
    FRAGMENT_DEFINITION("frag"),
    FRAGMENT_SPREAD("fragsprd"),
    VARIABLE_DEFINITION("var"),
    ARGUMENT("arg"),
    ARGUMENTS("args"),
    SELECTION_SET("sset"),
    VARIABLE_DEFINITIONS("vardefs"),
    DIRECTIVES("direcs"),
    DEFINITIONS("defs"),
    DEFINITION("def"), SELECTION("sel");

    private static final Map<String, DocumentElementType> lookup = new HashMap<>();

    static {
        for (DocumentElementType documentElementType : DocumentElementType.values()) {

            lookup.put(documentElementType.getShortName(), documentElementType);
        }
    }

    private final String shortName;

    DocumentElementType(String shortName) {

        this.shortName = shortName;
    }

    public static DocumentElementType getByShortName(String shortName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(shortName));

        return lookup.get(shortName);
    }

    public String getShortName() {
        return shortName;
    }
}
