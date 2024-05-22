package com.intuit.gqlex;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.common.RawPayload;
import com.intuit.gqlex.transformer.TransformBuilder;
import com.intuit.gqlex.gxpath.syntax.SyntaxPath;
import com.intuit.gqlex.gxpath.syntax.SyntaxPathElement;
import graphql.language.Field;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class eXtendGqlFacadeTest {

    @Test
    void loadPayload_and_transform() throws FileNotFoundException {

        eXtendGqlFacade eXtendGqlFacade = new eXtendGqlFacade();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("eXtendGql/payload" + "/kiril_payload.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            eXtendGqlFacade.loadPayload(scanner.nextLine());

            int expectedSize = 3;

            assertLoadedPayload(eXtendGqlFacade, expectedSize);

            TransformBuilder transformBuilder = new TransformBuilder();
            transformBuilder.addChildrenNode("//query[name=findApps]/user/allApplications/pageInfo",new Field("new_name1"))
                    .addSiblingNode("//query[name=findApps]/user/allApplications/aggregates",new Field("new_name2"));

            RawPayload transform = eXtendGqlFacade.transform(transformBuilder);
            System.out.println(transform.getQueryValue());
        }
    }


    @Test
    void loadPayload_and_select() throws FileNotFoundException {

        eXtendGqlFacade eXtendGqlFacade = new eXtendGqlFacade();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("eXtendGql/payload" + "/kiril_payload.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            eXtendGqlFacade.loadPayload(scanner.nextLine());

            TransformBuilder transformBuilder = new TransformBuilder();
            transformBuilder.addChildrenNode("//query[name=findApps]/user/allApplications/pageInfo",new Field("new_name1"))
                    .addSiblingNode("//query[name=findApps]/user/allApplications/aggregates",new Field("new_name2"));

            SyntaxPath syntaxPath = new SyntaxPath();
            SyntaxPathElement querySyntaxPathElement = new SyntaxPathElement("query", DocumentElementType.OPERATION_DEFINITION);
            querySyntaxPathElement.addAttribute("name", "findApps");
            syntaxPath.appendSyntaxPathElement(querySyntaxPathElement);
            syntaxPath.appendFieldNameElement("user");
            syntaxPath.appendFieldNameElement("allApplications");
            syntaxPath.appendFieldNameElement("pageInfo");

            List<GqlNodeContext> select = eXtendGqlFacade.select(syntaxPath);

            assertNotNull(select);
            for (GqlNodeContext gqlNodeContext : select) {
                System.out.println(gqlNodeContext.toShortString());
            }

        }
    }

    @Test
    void loadPayload() throws FileNotFoundException {

        eXtendGqlFacade eXtendGqlFacade = new eXtendGqlFacade();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("eXtendGql/payload" + "/kiril_payload.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            eXtendGqlFacade.loadPayload(scanner.nextLine());

            int expectedSize = 3;

            assertLoadedPayload(eXtendGqlFacade, expectedSize);
        }
    }

    private static void assertLoadedPayload(eXtendGqlFacade eXtendGqlFacade, int expectedSize) {
        assertNotNull(eXtendGqlFacade.getPayload());
        assertNotNull(eXtendGqlFacade.getPayload().getVariablesMap());
        Assertions.assertEquals(expectedSize, eXtendGqlFacade.getPayload().getVariablesMap().size());
        assertNotNull(eXtendGqlFacade.getPayload().getQueryValue());
        assertNotNull(eXtendGqlFacade.getPayload().getJsonObjectParsed());
        assertEntities(eXtendGqlFacade, "variables");
        assertEntities(eXtendGqlFacade, "query");
        assertEntities(eXtendGqlFacade, "operationName");
    }

    private static void assertEntities(eXtendGqlFacade eXtendGqlFacade, String name) {
        Object entities = eXtendGqlFacade.getPayload().getJsonObjectParsed().get(name);
        assertNotNull(entities);
        System.out.println("\n\n"+name + "\n-----------\n");
        System.out.println(entities);
    }

    @Test
    void loadPayload_transform_variable() throws FileNotFoundException {

        eXtendGqlFacade eXtendGqlFacade = new eXtendGqlFacade();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("eXtendGql/payload" + "/kiril_payload.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            eXtendGqlFacade.loadPayload(scanner.nextLine());

            int expectedSize = 3;

            System.out.println("\nBefore:\n\n");
            assertLoadedPayload(eXtendGqlFacade, expectedSize);

            System.out.println("\n\n\nAfter:\n\n");
            eXtendGqlFacade.transformVariable("offset", 1000);

            assertEntities(eXtendGqlFacade, "variables");

            HashMap o = (HashMap) eXtendGqlFacade.getPayload().getJsonObjectParsed().get("variables");
            assertEquals(1000, (int)o.get("offset"));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("a_key", "a_value");

            JSONObject jsonObject2 = new JSONObject();
            for (int i = 1; i <= 2; i++) {
                jsonObject2.put("x_key_"+i, "x_value_"+i);
            }

            System.out.println("\n\n\nAfter - JsonObject:\n\n");
            jsonObject.put("b_key", jsonObject2);
            eXtendGqlFacade.transformVariable("filterByString", jsonObject);

            o = (HashMap) eXtendGqlFacade.getPayload().getJsonObjectParsed().get("variables");
            assertEquals("{\"a_key\":\"a_value\",\"b_key\":{\"x_key_2\":\"x_value_2\",\"x_key_1\":\"x_value_1\"}}", o.get("filterByString"));

            assertEntities(eXtendGqlFacade, "variables");

            JSONArray value = new JSONArray();
            value.put(1);
            value.put(2);
            eXtendGqlFacade.transformVariable("filterByString", value);

            assertEntities(eXtendGqlFacade, "variables");

            o = (HashMap) eXtendGqlFacade.getPayload().getJsonObjectParsed().get("variables");
            assertEquals("[1,2]", o.get("filterByString").toString());
        }
    }


}