package com.intuit.library.jsonConverter.services;

import graphql.language.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryDeepestLookupServiceTest {

    @Test
    void checkDepthLimit_simple() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        Document document = parser.parseDocument("query AAA {\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "    }\n" +
                "  }\n" +
                "}\n");

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(3, deepSize);
    }

    @Test
    void checkDepthLimit_simple_3_layers() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query AAA {\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      level2{\n" +
                "          level3\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(4, deepSize);
    }


    @Test
    void checkDepthLimit_simple_3_layers_same_stucture() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query AAA {\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    alias2: A2 {\n" +
                "      B1_1\n" +
                "      B2_1\n" +
                "      B3_1{\n" +
                "          C1_1\n" +
                "      }\n" +
                "    }\n" +
                "    alias2: A2 {\n" +
                "      B1_2\n" +
                "      B2_2\n" +
                "      B3_2{\n" +
                "          C1_2\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(4, deepSize);
    }




    @Test
    public void checkDepthLimit_simple_3_layers_same_structuresys_plus_2_levels() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query AAA {\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    alias2: A2 {\n" +
                "      B1_1\n" +
                "      B2_1\n" +
                "      B3_1 {\n" +
                "        C1_1\n" +
                "      }\n" +
                "    }\n" +
                "    alias2: A2 {\n" +
                "      B1_2\n" +
                "      B2_2\n" +
                "      B3_2 {\n" +
                "        C1_2 {\n" +
                "          D1_2\n" +
                "          D2_2 {\n" +
                "              E1_2\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(6, deepSize);
    }

    @Test
    public void checkDepthLimit_nested_layers() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query AAA {\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    alias2: A2 {\n" +
                "      B1_1\n" +
                "      B2_1\n" +
                "      B3_1 {\n" +
                "        C1_1\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    A8 {\n" +
                "      B1_2\n" +
                "      B2_2\n" +
                "      B3_2 {\n" +
                "        C4_2 {\n" +
                "          D5_2\n" +
                "          D5_2 {\n" +
                "            E6_2 {\n" +
                "              F7_2 {\n" +
                "                G8_2 {\n" +
                "                  H9_2 {\n" +
                "                    I10_2 {\n" +
                "                      D11_2 {\n" +
                "                        E12_2 {\n" +
                "                          F13_2 {\n" +
                "                            G14_2 {\n" +
                "                              H15_2 {\n" +
                "                                I16_2 {\n" +
                "                                  J17\n" +
                "                                }\n" +
                "                              }\n" +
                "                            }\n" +
                "                          }\n" +
                "                        }\n" +
                "                      }\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "\n" +
                "    alias2: A2 {\n" +
                "      B1_2\n" +
                "      B2_2\n" +
                "      B3_2 {\n" +
                "        C4_2 {\n" +
                "          D5_2\n" +
                "          D5_2 {\n" +
                "            E6_2 {\n" +
                "              F7_2 {\n" +
                "                G8_2 {\n" +
                "                  H9_2 {\n" +
                "                    I10_2 {\n" +
                "                      J11\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(17, deepSize);
    }


    @Test
    public void checkDepthLimit_fragments() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query MyQuery ($showDate: Boolean!){\n" +
                "  allFilms {\n" +
                "    films {\n" +
                "      ...FilmFields\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment FilmFields on Film {\n" +
                "  id\n" +
                "  title\n" +
                "  releaseDate @include(if: $showDate)\n" +
                "}";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(4, deepSize);
    }



    @Test
    public void checkDepthLimit_2_fragments() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query MyQuery($showDate: Boolean!) {\n" +
                "  allFilms {\n" +
                "    films {\n" +
                "      ...FilmFields\n" +
                "      r\n" +
                "      t\n" +
                "      ...A\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "fragment FilmFields on Film {\n" +
                "  id\n" +
                "  title\n" +
                "  releaseDate @include(if: $showDate)\n" +
                "}\n" +
                "\n" +
                "fragment A on B {\n" +
                "  a1\n" +
                "  a1_1 {\n" +
                "    a2\n" +
                "    a2_1 {\n" +
                "      a3\n" +
                "      a3_1 {\n" +
                "        a4\n" +
                "        a4_1 {\n" +
                "          a5\n" +
                "          a5_1 {\n" +
                "            a6\n" +
                "            a6_2\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "  a3 @include(if: $showDate)\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(9, deepSize);
    }

    // nested fragment
    @Test
    public void checkDepthLimit_nested_fragments() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "fragment Bar on Foo {\n" +
                "  bar {\n" +
                "    id\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment Baz on Foo {\n" +
                "  baz {\n" +
                "    id {\n" +
                "    title}\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "fragment MetaFoo on Foo {\n" +
                "  id\n" +
                "  ...Bar\n" +
                "  ...Baz\n" +
                "}\n" +
                "\n" +
                "query Qux {\n" +
                "  foo {\n" +
                "    ...MetaFoo\n" +
                "  }\n" +
                "}";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(6, deepSize);
    }

    @Test
    public void checkDepthLimit_nested_fragments_at_the_end() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "query Qux {\n" +
                "  foo {\n" +
                "    ...MetaFoo\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment Bar on Foo {\n" +
                "  bar {\n" +
                "    id\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment Baz on Foo {\n" +
                "  baz {\n" +
                "    id {\n" +
                "      id {\n" +
                "        id\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment MetaFoo on Foo {\n" +
                "  ...Bar\n" +
                "  ...Baz\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(7, deepSize);
    }

    @Test
    public void checkDepthLimit_mutation() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "mutation M {\n" +
                "  updateTransactions_TransactionLine(input: {clientMutationId : \"0\", transactionsTransactionLine : {id : \"djQuMTo5MTMwMzUxNzUxNTIwMTI2Ojk3ODhjZjQ3NWI:0-0\", account : {id : \"djQuMTo5MTMwMzUxNzUxNTIwMTI2OjUxY2VkODUzNmM:143\"}, batchUpdateFilters : {batchOperationName : \"tickAll\", filterProperties : [{name : \"manualTxns\", value : \"false\"}, {name : \"reconcileStatus\", value : \"2\"}]}}}) {\n" +
                "    a{\n" +
                "    b}\n" +

                "  }\n" +
                "}";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(3, deepSize);
    }
    @Test
    public void checkDepthLimit_mutation_nested_fields() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "mutation AddNewPet($name: String!, $petType: PetType) {\n" +
                "  addPet(name: $name, petType: $petType) {\n" +
                "    id\n" +
                "    name\n" +
                "    a2 {\n" +
                "      id\n" +
                "      name\n" +
                "      a3 {\n" +
                "        id\n" +
                "        name\n" +
                "        a4 {\n" +
                "          id\n" +
                "          name\n" +
                "          a5 {\n" +
                "            a6\n" +
                "            name\n" +
                "            petType\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(6, deepSize);
    }
    @Test
    public void checkDepthLimit_mutation_fragment() {

        graphql.parser.Parser parser = new graphql.parser.Parser();
        String queryString = "mutation M {\n" +
                "  updateTransactions_TransactionLine(input: {clientMutationId : \"0\", transactionsTransactionLine : {id : \"djQuMTo5MTMwMzUxNzUxNTIwMTI2Ojk3ODhjZjQ3NWI:0-0\", account : {id : \"djQuMTo5MTMwMzUxNzUxNTIwMTI2OjUxY2VkODUzNmM:143\"}, batchUpdateFilters : {batchOperationName : \"tickAll\", filterProperties : [{name : \"manualTxns\", value : \"false\"}, {name : \"reconcileStatus\", value : \"2\"}]}}}) {\n" +
                "    a{\n" +
                "    b}\n" +

                "  }\n" +
                "}";
        Document document = parser.parseDocument(queryString);

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        int deepSize = queryDeepestLookupService.checkDepthLimit(document);
        assertEquals(3, deepSize);
    }
}
