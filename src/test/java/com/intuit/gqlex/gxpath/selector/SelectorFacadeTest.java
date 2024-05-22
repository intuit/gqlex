package com.intuit.gqlex.gxpath.selector;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.common.eXtendGqlWriter;
import com.intuit.gqlex.transformer.TransformUtils;
import com.intuit.gqlex.gxpath.syntax.SyntaxBuilder;
import graphql.language.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SelectorFacadeTest {

/*
    // a/b/c/..../e - done

    // a/.../c/r/e - done

    //a/b/c/.../.../e

    //a/.../c/.../e

    //..../c/r/e

    //..../c_mistake/r/e


    //.../c/.../e
    //.../c/.../e - single path result
    //.../c/.../e - multi diff path result


 */
    @Test
    void test_any_fragment_select_fragment_name() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../comparisonFields[type=frag]");

        assertNotNull(select);

        assertEquals(1, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            Assertions.assertEquals(gqlNodeContext.getType(), DocumentElementType.FRAGMENT_DEFINITION);
            FragmentDefinition node = (FragmentDefinition) gqlNodeContext.getNode();

            assertTrue(node.getName().equalsIgnoreCase("comparisonFields"));
        }

    }


    @Test
    void test_any_fragment_select_spread_fragment_with_alias() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
//  /comparisonFields[type=frag]
        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../hero[alias=leftComparison]");

        assertNotNull(select);

        assertEquals(1, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field node = (Field) gqlNodeContext.getNode();
            assertEquals("leftComparison", node.getAlias());
            assertTrue(node.getName().equalsIgnoreCase("hero"));
        }

    /*assertEquals(select.getType(), DocumentElementType.FIELD);
    Field node = (Field) select.getNode();
    assertTrue(node.getName().equalsIgnoreCase("hero"));

    assertTrue(node.getAlias().equalsIgnoreCase("leftComparison"));*/
    }

    @Test
    void test_any_fragment_select_spread_fragment_without_alias() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
//  /comparisonFields[type=frag]
        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../hero");

        assertNotNull(select);

        assertEquals(2, select.size());
        GqlNodeContext gqlNodeContext1 = select.get(0);
        assertEquals(gqlNodeContext1.getType(), DocumentElementType.FIELD);
        Field node = (Field) gqlNodeContext1.getNode();
        assertEquals("leftComparison", node.getAlias());
        assertTrue(node.getName().equalsIgnoreCase("hero"));

        GqlNodeContext gqlNodeContext2 = select.get(1);
        assertEquals(gqlNodeContext2.getType(), DocumentElementType.FIELD);
        Field node2 = (Field) gqlNodeContext2.getNode();
        assertEquals("rightComparison", node2.getAlias());
        assertTrue(node2.getName().equalsIgnoreCase("hero"));


    }

    @Test
    void transform_mutation_add_child_under_alias() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
////query/Instrument/Reference[alias=aliasme]
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../Reference[alias=aliasme]");

        assertNotNull(select);

        assertEquals(3, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field node = (Field) gqlNodeContext.getNode();
            assertEquals("aliasme", node.getAlias());
            assertTrue(node.getName().equalsIgnoreCase("Reference"));
        }

    }

    @Test
    void transform_mutation_add_child_under_second_alias() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
////query/Instrument/Reference[alias=aliasme]
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../Reference[alias=aliasfor]");

        assertNotNull(select);

        assertEquals(2, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field node = (Field) gqlNodeContext.getNode();
            assertEquals("aliasfor", node.getAlias());
            assertTrue(node.getName().equalsIgnoreCase("Reference"));
        }

    }

    @Test
    void transform_mutation_alias_at_end() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
////query/Instrument/Reference[alias=aliasme]
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../title");

        assertNotNull(select);

        assertEquals(5, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field node = (Field) gqlNodeContext.getNode();
            assertTrue(node.getName().equalsIgnoreCase("title"));
        }

    }

    @Test
    void transform_mutation_add_child_under_alias_with_no_alias() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());
////query/Instrument/Reference[alias=aliasme]
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//.../Reference");

        assertNotNull(select);

        assertEquals(5, select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field node = (Field) gqlNodeContext.getNode();

            assertTrue(node.getName().equalsIgnoreCase("Reference"));
        }

    }

    // // a/.../c/r/e
    @Test
    void verify_select_with_any_a_b_c_any_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/hero/.../include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_with_any_a_any_mistake__c_d_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/.../x/.../friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(0, nodeContexts.size());

        /*for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }*/
    }

    @Test
        // diff_path -> //.../c/.../e - multi diff path result
    void verify_select_with_any_a_single_between_multi_path_any__c_d_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables_check_any.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../x/.../friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertEquals(15, gqlNodeContext.getNodeStack().size());
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
        // diff_path -> //.../c/.../e - multi diff path result
    void verify_select_multi_path_same_name_with_any_prefix() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables_check_any.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(5, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
        // diff_path -> //.../e - multi diff path result
    void seldct_fields_with_any_multi_queries_different_path() throws IOException {

        String fileName = "multiple_queries_any_test.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../b/title");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());


        GqlNodeContext gqlNodeContext = nodeContexts.get(0);

        assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
        Field field = (Field) gqlNodeContext.getNodeStack().get(3).getNode();
        assertEquals("a3", field.getName());

        assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("title"));

    }


    @Test
        // diff_path -> //.../e - multi diff path result
    void seldct_fields_with_any_multi_queries_different_path_2() throws IOException {

        String fileName = "multiple_queries_any_test.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../posts/title");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());


        GqlNodeContext gqlNodeContext = nodeContexts.get(0);

        assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
        Field field = (Field) gqlNodeContext.getNodeStack().get(3).getNode();
        assertEquals("a2", field.getName());

        assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("title"));

    }


    @Test
        // diff_path -> //.../e - multi diff path result
    void seldct_fields_with_any_multi_queries() throws IOException {

        String fileName = "multiple_queries_any_test.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../id");

        assertNotNull(nodeContexts);
        assertEquals(3, nodeContexts.size());

        int i = 1;
        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            Field field = (Field) gqlNodeContext.getNodeStack().get(3).getNode();
            assertEquals(field.getName(), "a" + i);
            i++;
            assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("id"));
        }
    }

    @Test
        // diff_path -> //.../c/.../e - multi diff path result
    void verify_select_multi_path_same_group_under_hero_exclude_single() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables_check_any.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertEquals(7, gqlNodeContext.getNodeStack().size());
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
    void verify_select_with_any_a_any_c_d_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/.../friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
    void verify_select_with_any_a_any_any_d_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/.../.../include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
//a/.../c/.../e
    void verify_select_with_a_any_d_any_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../hero/.../include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
//.../e
    void verify_select_with_any_e() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//.../include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
    void verify_select_single_path_double_slash_invalid_range_string_end_value() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{4353:yryt}//query[name=hero]/hero/friends/include[type=direc]");

            assertNotNull(nodeContexts);
            assertEquals(4, nodeContexts.size());

            for (GqlNodeContext gqlNodeContext : nodeContexts) {
                assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
                assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
            }
        });

        System.out.println(exception);
    }

    @Test
    void verify_select_single_path_double_slash_invalid_range_string_start_value() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{aa:5}//query[name=hero]/hero/friends/include[type=direc]");

            assertNotNull(nodeContexts);
            assertEquals(4, nodeContexts.size());

            for (GqlNodeContext gqlNodeContext : nodeContexts) {
                assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
                assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
            }
        });

        System.out.println(exception);
    }

    @Test
    void verify_select_single_path_double_slash_invalid_range_semicollon() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{}//query[name=hero]/hero/friends/include[type=direc]");

            assertNotNull(nodeContexts);
            assertEquals(4, nodeContexts.size());

            for (GqlNodeContext gqlNodeContext : nodeContexts) {
                assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
                assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
            }
        });

        System.out.println(exception);
    }

    @Test
    void verify_select_single_path_double_slash_invalid_range_suffix() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{://query[name=hero]/hero/friends/include[type=direc]");

            assertNotNull(nodeContexts);
            assertEquals(4, nodeContexts.size());

            for (GqlNodeContext gqlNodeContext : nodeContexts) {
                assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
                assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
            }
        });

        System.out.println(exception);
    }

    @Test
    void verify_select_single_path_double_slash_invalid_range_prefix() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, ":}//query[name=hero]/hero/friends/include[type=direc]");

            assertNotNull(nodeContexts);
            assertEquals(4, nodeContexts.size());

            for (GqlNodeContext gqlNodeContext : nodeContexts) {
                assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
                assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
            }
        });

        System.out.println(exception);
    }

    @Test
    void verify_select_single_path_double_slash_empty_range_values() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{:}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash_range_large_range_end_value() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{0:1000}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    @Test
    void verify_select_single_path_double_slash_range_large_range_end_value_with_large_start_value() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{345:463463}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(0, nodeContexts.size());

    }

    @Test
    void verify_select_single_path_double_slash_range_with_start_zero_and_end_is_empty() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{0:}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash_range_with_start_and_end_is_empty() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{1:}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(3, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash_range_with_end_and_start_is_empty() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{:2}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(3, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash_range_with_start_and_end_equal() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{2:2}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash_range_with_start_and_end() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "{2:3}//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(2, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_double_slash() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }

    @Test
    void verify_select_single_path_one_slash() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "/query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }
    }


    // .txt
/*
mutation createCompany {
  createCompany_CompanySetupInfo(
    input: {
      clientMutationId: "1"
      companyCompanySetupInfo: {
        profile: {
          localization: { country: "US" }
          companyName: "Rcc Paid company"
          contactMethods: [
            {
              addresses: [
                {
                  addressComponents: [
                    { name: "CITY", value: "New York" }
 */
    @Test
    void multi_select_intuit_real_mutation_select_names() throws IOException {

        String fileName = "multi_select_intuit_real_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        ////mutation[name=createCompany]/createCompany_CompanySetupInfo/companyCompanySetupInfo/companyProfile
        // query {  Instrument(id: "1234") } ///companyCompanySetupInfo/profile/contactMethods/addresses/addressComponents/name
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//mutation[name=createCompany]/createCompany_CompanySetupInfo/companyCompanySetupInfo/companyProfile");

        assertNotNull(nodeContexts);
        assertEquals(2, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("companyProfile"));
        }


    }


    // multi_select_intuit_real_mutation__diff.txt

    @Test
    void multi_select_intuit_real_mutation_select_names___diff_path() throws IOException {

        String fileName = "multi_select_intuit_real_mutation__diff.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        ////mutation[name=createCompany]/createCompany_CompanySetupInfo/companyCompanySetupInfo/companyProfile
        // query {  Instrument(id: "1234") } ///companyCompanySetupInfo/profile/contactMethods/addresses/addressComponents/name
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//mutation[name=createCompany]/createCompany_CompanySetupInfo/companyCompanySetupInfo/companyProfile");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());
        GqlNodeContext gqlNodeContext = nodeContexts.get(0);
        assertEquals(8, gqlNodeContext.getNodeStack().size());
        assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("companyProfile"));

    }

    @Test
    void multi_select_verify_directive_path_diff() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables___diff_friends.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(nodeContexts);
        assertEquals(4, nodeContexts.size());

        for (GqlNodeContext gqlNodeContext : nodeContexts) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.DIRECTIVE);
            assertTrue(((Directive) gqlNodeContext.getNode()).getName().equalsIgnoreCase("include"));
        }


    }

    @Test
    void multi_select_query_hero_example_directives_variables_select_main_field() throws IOException {

        String fileName = "multi_select_hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query[name=hero]");

        assertNotNull(nodeContexts);
        assertEquals(1, nodeContexts.size());

        GqlNodeContext gqlNodeContext = nodeContexts.get(0);
        assertEquals(gqlNodeContext.getType(), DocumentElementType.OPERATION_DEFINITION);
        assertTrue(((OperationDefinition) gqlNodeContext.getNode()).getName().equalsIgnoreCase("hero"));

    }

    @Test
    void multi_select_multiple_queries_select_field_first_query_argument_stringValue() throws IOException {

        String fileName = "multi_multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }

        /*
        query GetLoggedInUserName {
          me {
            name @export(as: "search")
          }
        }
         */
        List<GqlNodeContext> nodeContexts = selectorFacade.selectMany(queryString, "//query/GetLoggedInUserName/me/name/export[type=direc]");

        assertNotNull(nodeContexts);

        assertEquals(1, nodeContexts.size());
        GqlNodeContext select = nodeContexts.get(0);
        assertEquals(select.getType(), DocumentElementType.DIRECTIVE);
        Directive node = (Directive) select.getNode();

        assertTrue(node.getName().equalsIgnoreCase("export"));
    }

    @Test
    void select_multi_simple_different_path_size_1() throws IOException {

        String fileName = "multi_diff_path_size_1_simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> selectList = selectorFacade.selectMany(queryString, "//query/   Instrument /   Reference  /");

        assertNotNull(selectList);
        assertEquals(1, selectList.size());
        for (GqlNodeContext gqlNodeContext : selectList) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            assertEquals(6, gqlNodeContext.getNodeStack().size());
            assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("Reference"));
        }
    }

    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void select_multi_simple() throws IOException {

        String fileName = "multi_simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> selectList = selectorFacade.selectMany(queryString, "//query/   Instrument /   Reference  /");

        assertNotNull(selectList);
        assertEquals(2, selectList.size());
        for (GqlNodeContext gqlNodeContext : selectList) {
            assertEquals(gqlNodeContext.getType(), DocumentElementType.FIELD);
            assertTrue(((Field) gqlNodeContext.getNode()).getName().equalsIgnoreCase("Reference"));
        }
    }


    ////////


    // select many nodes
    @Test
    // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void select_many_nodes_flat() throws IOException {

        String fileName = "simple_many_nodes.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//query/Instrument/Reference/title");

        assertNotNull(select);
        //assertEquals(select.getType(), DocumentElementType.FIELD);
        // assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("title"));
    }

    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void verify_path_with_empty_name_illegal_exception() throws IOException {
        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            String fileName = "simple_query_with_body_and_arg.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            selectorFacade.selectSingle(queryString, "//query//Instrument/Reference");
        });
    }

    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void verify_path_with_empty_name_ilegal2_spaces_tabs_exception() throws IOException {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {

            String fileName = "simple_query_with_body_and_arg.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            selectorFacade.selectSingle(queryString, "//query/           /Instrument/Reference");
        });
    }

    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void verify_path_with_empty_name_redundant_slash_at_end_NO_exception() throws IOException {

        String fileName = "simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/   Instrument /   Reference  /");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Reference"));
    }


    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void verify_path_with_empty_name_redundant_slash_at_end_NO_exception____use_of_syntaxPath() throws IOException {

        String fileName = "simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());


        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        eXtendGqlBuilder.appendQuery();

        eXtendGqlBuilder.appendField("Instrument");
        eXtendGqlBuilder.appendField("Reference");

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, eXtendGqlBuilder.build());

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Reference"));
    }


    // simple_query_with_body_and_arg.txt
    @Test
    // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void select_body_fields_select_leaf_field() throws IOException {

        String fileName = "simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/title");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("title"));
    }

    @Test
        // query {  Instrument(id: "1234") {    Reference {      Name title   }  }}
    void select_body_fields_select_parent_leaf_field() throws IOException {

        String fileName = "simple_query_with_body_and_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Reference"));
    }


    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.OPERATION_DEFINITION);
        assertTrue(((OperationDefinition) select.getNode()).getOperation().name().equalsIgnoreCase("query"));
    }

    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg_select_second_field_spaces_in_the_name() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/ Instrument ");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Instrument"));
    }

    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg_select_second_field() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument");

        assertNotNull(select);
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Instrument"));
    }

    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg_unorder_path_inexist() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/id/Instrument");

        assertNull(select);
    }

    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg_select_inner_field_id_not_exist() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/id");

        assertNull(select);// field does not exist, only argument with name of id exist
    }

    @Test
        // query {  Instrument(id: "1234") }
    void select_simple_query_only_arg_select_inner_arg_id_exist() throws IOException {

        String fileName = "simple_query_only_arg.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/id[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("id"));

    }


    @Test
        // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    void duplicate_fields_name_select_arg() throws IOException {

        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/name[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("name"));

    }


    @Test
        // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    void duplicate_fields_name_select_arg_upper_case_name() throws IOException {

        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/name[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("name"));

    }


    @Test
        // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    void duplicate_fields_name_select_field_upper_case_name() throws IOException {

        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        assertNotNull(select);

        Node newNode = new Field("new_name");
        Node node = TransformUtils.addChildren(select, newNode);

        //   SelectionDebugData selectionDebugData = selectorFacade.getSelectionDebugData();

        //   assertNotNull(selectionDebugData);
        //    assertNotNull(selectionDebugData.getMsgs());
        // eXtendGqlWriter.writeToString(node);
        StringWriter stringWriter = new StringWriter();
        eXtendGqlWriter.writeToStream(stringWriter, node);
        String newGqlValue = stringWriter.toString();
        assertTrue(newGqlValue.contains("new_name"));
        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("NAME"));

    }

    // hero_example_directives_variables.txt

    /*
    query Hero($episode: Episode, $withFriends: Boolean!) {
  hero(episode: $episode) {
    name
    friends @include(if: $withFriends) {
      name
    }
  }
}
     */

    // select hero
    @Test
    void hero_example_directives_variables_select_main_field() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.OPERATION_DEFINITION);
        assertTrue(((OperationDefinition) select.getNode()).getName().equalsIgnoreCase("hero"));

    }


    // select $episode

    @Test
    void hero_example_directives_variables_select_main_field_variable() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/episode[type=var]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.VARIABLE_DEFINITION);
        assertTrue(((VariableDefinition) select.getNode()).getName().equalsIgnoreCase("episode"));

    }


    // select $withFriends
    @Test
    void hero_example_directives_variables_select_field__second_variable() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/withFriends[type=var]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.VARIABLE_DEFINITION);
        assertTrue(((VariableDefinition) select.getNode()).getName().equalsIgnoreCase("withFriends"));

    }

    // select hero/hero/varef<episode>
    @Test
    void select_var_ref_under_field() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/episode[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("episode"));

    }

    // select hero.name
    @Test
    void select_field() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/name");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("name"));

    }

    // select hero.friends
    @Test
    void select_friends_with_directive() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/friends");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("friends"));

    }

    // select hero.friends.@include
    @Test
    void select_friends_with_directive_select_inner_directive() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.DIRECTIVE);
        assertTrue(((Directive) select.getNode()).getName().equalsIgnoreCase("include"));

    }

    @Test
    void select_friends_with_directive_select_inner_directive_inner_arg() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/friends/include[type=direc]/if[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("if"));

    }

    // select hero.friends.name
    @Test
    void select_friends_with_directive_leaf_node() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/friends/name");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("name"));

    }


    /*
    {
    query GetLoggedInUserName {
      me {
        name @export(as: "search")
      }
    }

    query GetPostsContainingString @depends(on: "GetLoggedInUserName") {
      posts(filter: { search: $search }) {
        id
        title
      }
    }
}
     */

    //GetLoggedInUserName.me.name

    //GetLoggedInUserName.me.name.@export


    @Test
    void multiple_queries_select_field_first_query_argument_stringValue() throws IOException {

        String fileName = "multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }

        /*
        query GetLoggedInUserName {
          me {
            name @export(as: "search")
          }
        }
         */
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/GetLoggedInUserName/me/name/export[type=direc]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.DIRECTIVE);
        Directive node = (Directive) select.getNode();

        assertTrue(node.getName().equalsIgnoreCase("export"));
    }

    //GetPostsContainingString.posts.filter.search - cannot be select, only the arg
    @Test
    void multiple_queries_select_field_second_query_select_field() throws IOException {

        String fileName = "multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/GetPostsContainingString/posts");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();

        assertTrue(node.getName().equalsIgnoreCase("posts"));

    }

    @Test
        //GetPostsContainingString.posts.filter
    void multiple_queries_select_field_second_query_var_with_argument_objectValue() throws IOException {

        String fileName = "multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/GetPostsContainingString/posts/filter[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        Argument node = (Argument) select.getNode();

        assertTrue(node.getName().equalsIgnoreCase("filter"));

        assertTrue(node.getValue() instanceof ObjectValue);
        ObjectValue value = (ObjectValue) node.getValue();
        //value.get


        List<ObjectField> objectFields = value.getObjectFields();
        assertNotNull(objectFields);
        ObjectField objectField = objectFields.get(0);
        assertNotNull(objectField);
        assertEquals("search", objectField.getName());


    }

    //GetPostsContainingString.posts.id
    /*{
        panel {
        listOfMovies(limit: 4) {
            title
                    playback
        }
    }
    }*/
    @Test
    // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    void check_limit_arg() throws IOException {

        String fileName = "limit.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/panel/listOfMovies/limit[type=arg]");

        // System.out.println(selectorFacade.getSelectionDebugData().toString());
        assertNotNull(select);


        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        assertTrue(((Argument) select.getNode()).getName().equalsIgnoreCase("limit"));

    }


    @Test
    void multiple_queries_select_field_second_query_1() throws IOException {

        String fileName = "multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/GetPostsContainingString/posts/id");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("id"));

    }

    //GetPostsContainingString.posts.title
    @Test
    void multiple_queries_select_field_second_query_2() throws IOException {

        String fileName = "multiple_queries.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/GetPostsContainingString/posts/title");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("title"));

    }



    /*
    query HeroComparison($first: Int = 3) {
  leftComparison: hero(episode: EMPIRE) {
    ...comparisonFields
  }
  rightComparison: hero(episode: JEDI) {
    ...comparisonFields
  }
}

fragment comparisonFields on Character {
  name
  friendsConnection(first: $first) {
    totalCount
    edges {
      node {
        name
      }
    }
  }
}
     */

    @Test
    void query_select_field_by_alias_first_query() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroComparison]/hero[alias=leftComparison]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("hero"));

        assertTrue(node.getAlias().equalsIgnoreCase("leftComparison"));
    }


    /*
    query HeroComparison($first: Int = 3) {
  leftComparison: hero(episode: EMPIRE) {
    ...comparisonFields
  }
  rightComparison: hero(episode: JEDI) {
    ...comparisonFields
  }
}

fragment comparisonFields on Character {
  name
  friendsConnection(first: $first) {
    totalCount
    edges {
      node {
        name
      }
    }
  }
}
     */
    @Test
    void query_select_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FRAGMENT_DEFINITION);
        FragmentDefinition node = (FragmentDefinition) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("comparisonFields"));

        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);

        assertTrue(typeCondition.getName().equalsIgnoreCase("Character"));

    }


    /*
    query HeroForEpisode($ep: Episode!) {
  hero(episode: $ep) {
    name
    ... on Droid {
      primaryFunction
    }
    ... on Human {
      height
    }
  }
}
     */
    @Test
    void query_select_inline_fragment_select_canonical_field() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroForEpisode]/hero/Droid[type=infrag]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.INLINE_FRAGMENT);
        InlineFragment node = (InlineFragment) select.getNode();
        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);
        assertTrue(typeCondition.getName().equalsIgnoreCase("Droid"));
    }

    @Test
    void query_select_fragment_select_cannonical_field() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("totalCount"));
    }

    @Test
    void query_select_fragment_select_field() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/name");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("name"));
    }

    @Test
    void query_select_field_by_alias_second_query() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        //GqlNode select = selectorFacade.select(queryString, "//opr<query>/opr_name<HeroComparison>/hero[alias=rightComparison]");

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroComparison]/hero[alias=rightComparison]");
        //query[name=HeroComparison]/hero[alias=rightComparison]

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("hero"));

        assertTrue(node.getAlias().equalsIgnoreCase("rightComparison"));
    }

    @Test
    void verify_simple_mutation() throws IOException {

        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.MUTATION_DEFINITION);
        OperationDefinition node = (OperationDefinition) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("CreateReviewForEpisode"));
    }

    @Test
    void verify_simple_mutation_select_node() throws IOException {

        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));
    }

    @Test
    void verify_simple_mutation_select_inner_node() throws IOException {

        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview/commentary");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("commentary"));
    }

    //[type=arg]

    @Test
    void verify_simple_mutation_select_arg() throws IOException {

        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview/episode[type=arg]");
        //System.out.println(selectorFacade.getSelectionDebugData().toString());
        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        Argument node = (Argument) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("episode"));
    }

    @Test
    void verify_simple_mutation_select_var() throws IOException {

        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/ep[type=var]");
        //  System.out.println(selectorFacade.getSelectionDebugData().toString());
        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.VARIABLE_DEFINITION);
        VariableDefinition node = (VariableDefinition) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("ep"));
    }

    @Test
    void select_input_mutation_real_intuit_sample() throws IOException {

        String fileName = "intuit_real_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=createCompany]/createCompany_CompanySetupInfo/input[type=arg]");

        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.ARGUMENT);
        Argument node = (Argument) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("input"));
    }

    @Test
    void select_field_mutation_real_intuit_sample() throws IOException {

        String fileName = "intuit_real_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=createCompany]/createCompany_CompanySetupInfo/companyCompanySetupInfo/companyProfile");
        //  System.out.println(selectorFacade.getSelectionDebugData().toString());
        assertNotNull(select);

        assertEquals(select.getType(), DocumentElementType.FIELD);
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("companyProfile"));
    }
}

//.txt
