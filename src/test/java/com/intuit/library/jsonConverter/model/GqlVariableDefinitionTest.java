package com.intuit.library.jsonConverter.model;

import graphql.language.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GqlVariableDefinitionTest {



    @Test
    void verify_object_value_size_0() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        List<ObjectField> objectFieldList = new ArrayList<>();
        ObjectValue defaultValue = new ObjectValue(objectFieldList);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"null\"}}", jsonObject.toString());
    }

    @Test
    void verify_array_object_value_size_1() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());

        List<Value> listOfValues = new ArrayList<>();
        listOfValues.add(new FloatValue(BigDecimal.valueOf(12.22)));
        listOfValues.add(new EnumValue("ENUM_VALUE"));
        listOfValues.add(new FloatValue(BigDecimal.valueOf(34.33)));
        listOfValues.add(new BooleanValue(true));
        //listOfValues.add(new ArrayValue(listOfValues.subList(0,2)));
        ArrayValue defaultValue = new ArrayValue(listOfValues);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"__array_12.22.ENUM_VALUE.34.33.true\"}}", jsonObject.toString());
    }
    @Test
    void verify_array_object_value_contains_inner_array_more_value_in_ext_array() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());

        List<Value> listOfValues = new ArrayList<>();
        listOfValues.add(new FloatValue(BigDecimal.valueOf(12.22)));
        listOfValues.add(new EnumValue("ENUM_VALUE"));
        listOfValues.add(new BooleanValue(true));

        List<Value> listOfValues2 = new ArrayList<>();
        listOfValues2.add(new FloatValue(BigDecimal.valueOf(567)));
        listOfValues.add(new ArrayValue(listOfValues2));

        // listOfValues2.add(new EnumValue("ENUM_VALUE_2"));
        ArrayValue defaultValue = new ArrayValue(listOfValues);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"__array_12.22.ENUM_VALUE.true.__array_567\"}}", jsonObject.toString());
    }

    @Test
    void verify_array_object_value_contains_inner_array() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());

        List<Value> listOfValues = new ArrayList<>();
        listOfValues.add(new FloatValue(BigDecimal.valueOf(12.22)));
        /*listOfValues.add(new EnumValue("ENUM_VALUE"));
        listOfValues.add(new FloatValue(BigDecimal.valueOf(34.33)));
        listOfValues.add(new BooleanValue(true));*/

        List<Value> listOfValues2 = new ArrayList<>();
        listOfValues2.add(new FloatValue(BigDecimal.valueOf(567)));
        listOfValues.add(new ArrayValue(listOfValues2));

       // listOfValues2.add(new EnumValue("ENUM_VALUE_2"));
        ArrayValue defaultValue = new ArrayValue(listOfValues);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"__array_12.22.__array_567\"}}", jsonObject.toString());
    }

    @Test
    void verify_object_value_size_1() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        List<ObjectField> objectFieldList = new ArrayList<>();
        objectFieldList.add(new ObjectField("object_field_1_name", new FloatValue(BigDecimal.valueOf(1234.22))));
        ObjectValue defaultValue = new ObjectValue(objectFieldList);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"__objVal_object_field_1_name<1234.22>\"}}", jsonObject.toString());
    }

    @Test
    void verify_object_value_size_4() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        List<ObjectField> objectFieldList = new ArrayList<>();
        objectFieldList.add(new ObjectField("object_field_1_name", new FloatValue(BigDecimal.valueOf(1234.22))));
        objectFieldList.add(new ObjectField("object_field_2_name", new IntValue(BigInteger.valueOf(99L))));
        objectFieldList.add(new ObjectField("object_field_3_name", new EnumValue("ENUM_KEY")));
        objectFieldList.add(new ObjectField("object_field_4_name", new VariableReference("var_ref_value")));


        ObjectValue defaultValue = new ObjectValue(objectFieldList);
        gqlVariableDefinition.setDefaultValue(defaultValue);
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"__objVal_object_field_1_name<1234.22>.object_field_2_name<99>.object_field_3_name<ENUM_KEY>.object_field_4_name<$var_ref_value>\"}}", jsonObject.toString());
    }

    @Test
    void verify_float() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        gqlVariableDefinition.setDefaultValue(new FloatValue(BigDecimal.valueOf(1234.77)));
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"1234.77\"}}", jsonObject.toString());
    }

    @Test
    void verify_boolean() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        gqlVariableDefinition.setDefaultValue(new BooleanValue(true));
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"true\"}}", jsonObject.toString());
    }
    @Test
    void verify_int() {

        GqlVariableDefinition gqlVariableDefinition = new GqlVariableDefinition();
        gqlVariableDefinition.setName("var1_name");
        gqlVariableDefinition.setType(TypeName.newTypeName().build());
        gqlVariableDefinition.setDefaultValue(new IntValue(BigInteger.valueOf(1234L)));
        Object jsonObject = gqlVariableDefinition.generateAsJsonObject();
        assertNotNull(jsonObject);
        assertEquals("{\"$var1_name\":{\"defaultValue\":\"1234\"}}", jsonObject.toString());
    }
}