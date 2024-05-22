package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.model.tree.TreeNode;
import graphql.language.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GqlArgumentTest {

    // float
    @Test
    void verify_single_none_null_arg_float_value() {

        String key = "arg_name";
        BigDecimal value = BigDecimal.valueOf(1234.89);

        FloatValue fltValue = new FloatValue(value);
        verifySingleValue(key, fltValue, value, "{\"arg_name\":1234.89}");
    }
    // int

    @Test
    void verify_single_none_null_arg_int_value() {

        String key = "arg_name";
        BigInteger value = BigInteger.valueOf(1234L);

        IntValue intValue = new IntValue(value);
        verifySingleValue(key, intValue, value, "{\"arg_name\":\"1234\"}");
    }

    @Test
    void verify_single_none_null_arg_boolean_value() {

        String key = "arg_name";
        boolean value = true;

        BooleanValue booleanValue = new BooleanValue(value);
        verifySingleValue(key, booleanValue, value, "{\"arg_name\":{\"__isValue\":true}}");
    }

    @Test
    void verify_single_none_null_arg_string_value() {

        String key = "arg_name";
        String value = "arg_str_value";

        StringValue stringValue = new StringValue(value);
        verifySingleValue(key, stringValue, value, "{\"arg_name\":\"" + value + "\"}");
    }

    private static void verifySingleValue(String key, ScalarValue scalarValue, Object value, String expectedJson) {
        Argument argument = new Argument(key, scalarValue);
        GqlArgument gqlArgument = new GqlArgument(argument);
        assertEquals(gqlArgument.getName(), key);
        Object jsonValue = gqlArgument.generateAsJsonObject();

        assertNotNull(jsonValue);

        assertTrue(gqlArgument.hasChilds());

        List<Gqlable> childNodes = gqlArgument.getChildNodes();
        assertNotNull(childNodes);

        assertTrue(childNodes.size() == 1);

        Gqlable gqlable = childNodes.get(0);
        boolean condition = gqlable instanceof TreeNode;
        assertTrue(condition);

        assertEquals(((TreeNode) gqlable).getValue().toString(), value.toString());

        assertEquals(expectedJson, jsonValue.toString());
    }
}

