package com.intuit.library.jsonConverter.model;

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.StringValue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GqlDirectiveTest {

    @Test
    public void verify_no_arg(){
        Directive directive = new Directive("include", new ArrayList<>());
        GqlDirective gqlDirective = new GqlDirective(directive);
        assertTrue(gqlDirective.getArguments() == null );

        assertTrue(gqlDirective.getChildNodes() == null );

        assertFalse(gqlDirective.hasChilds() );

        assertTrue(gqlDirective.getFieldNameDescriptor() != null);

        assertEquals(gqlDirective.getFieldNameDescriptor().toString(), "FieldNameDescriptor{name='directv<@include>', alias='', typeName='Directive'}");
    }

    @Test
    public void verify_one_arg(){
        List<Argument> argumentList = new ArrayList<>();
        argumentList.add(new Argument("arg_name",new StringValue("arg_value")));
        Directive directive = new Directive("include", argumentList);
        GqlDirective gqlDirective = new GqlDirective(directive);
        assertTrue(gqlDirective.getArguments() != null );

        assertTrue(gqlDirective.hasChilds());

        assertTrue(gqlDirective.getChildNodes() != null);
    }
}