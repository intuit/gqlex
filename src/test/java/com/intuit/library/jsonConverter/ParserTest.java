package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.GqlDocument;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

   /* @Test
    void chgeck_bitmap() throws IOException {

        MutableRoaringBitmap rr1 = MutableRoaringBitmap.bitmapOf(1);
        MutableRoaringBitmap rr2 = MutableRoaringBitmap.bitmapOf( 2, 3, 1010);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        // If there were runs of consecutive values, you could
        // call rr1.runOptimize(); or rr2.runOptimize(); to improve compression
        rr1.serialize(dos);
        rr2.serialize(dos);
        dos.close();
        //ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());


        ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
        ImmutableRoaringBitmap rrback1 = new ImmutableRoaringBitmap(bb);
        ImmutableRoaringBitmap rrback2 = new ImmutableRoaringBitmap(bb);

        RoaringBitmap expected = RoaringBitmap.bitmapOf(1, 2, 3, 4, 5, 6, 7, 8);
        RoaringBitmap A = RoaringBitmap.bitmapOf(1, 2, 3, 4, 5);
        RoaringBitmap B = RoaringBitmap.bitmapOf(4, 5, 6, 7, 8);
        RoaringBitmap union = RoaringBitmap.or(A, B);
        assertEquals(expected, union);
    }*/


    @Test
    void test_query_with_limit_directive() throws Exception {
        String testFolder = "query_with_limit_arg";
        int fieldsNumber = 10;

        runQueryTestInvoke(testFolder, null, fieldsNumber);

    }



    @Test
    void getValueCanonicalFields_query_fragments() throws Exception {

        String testFolder = "test_named_query_fragments_by_order_alias";
        int fieldsNumber = 10;

        runQueryTestInvoke(testFolder, "hero", fieldsNumber);
    }
    @Test
    void getValueCanonicalFields_query_unordered_fragments() throws Exception {

        String testFolder = "test_named_query_fragments_not_by_order_alias";
        int fieldsNumber = 10;

        runQueryTestInvoke(testFolder, "hero", fieldsNumber);
    }
    @Test
    public void test_query_with_condition() throws Exception {

        String testFolder = "test_query_with_condition";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, "Hero", fieldsNumber);
    }

    @Test
    public void fragment_with_conditiion() throws Exception {

        String testFolder = "fragment_with_conditiion";
        int fieldsNumber = 3;

        runQueryTestInvoke(testFolder, "MyQuery", fieldsNumber);
    }

    @Test
    public void big_query() throws Exception {

        String testFolder = "big_query";
        int fieldsNumber = 126;

        runQueryTestInvoke(testFolder, "getAppDetail", fieldsNumber);
    }
    @Test
    public void big_sample_query() throws Exception {

        String testFolder = "big_query";
        int fieldsNumber = 126;

        runQueryTestInvoke(testFolder, "getAppDetail", fieldsNumber);
    }

    @Test
    public void test_query_fragment_with_vars() throws Exception {

        String testFolder = "test_query_fragment_with_vars";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "HeroComparison", fieldsNumber);
    }
    @Test
    public void test_check_default_value_not_contains_reserved_words() throws Exception {

        String testFolder = "check_default_value_not_contains_reserved_words";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "myQuery", fieldsNumber);
    }

    @Test
    public void test_query_with_args() throws Exception {

        String testFolder = "marciuos";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "myQuery", fieldsNumber);
    }

    @Test
    public void verify_qbous_buynow_mutation() throws Exception {

        String testFolder = "mutation/qbous_buynow";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "createCompany", fieldsNumber);
    }


    @Test
    public void verify_mutation_argument_element_set_by_name_and_value() throws Exception {

        String testFolder = "mutation/mutation_element_set_by_name_and_value";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "updateTransactions_TransactionLine", fieldsNumber);
    }

    @Test
    public void verify_arg_with_dollar_sign() throws Exception {

        String testFolder = "mutation/arg_with_dollar_sign";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "createCompany", fieldsNumber);
    }

    @Test
    public void verify_nested_args() throws Exception {

        String testFolder = "mutation/nested_args";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "createCompany", fieldsNumber);
    }

    @Test
    public void verify_content_no_childs_should_remove() throws Exception {

        String testFolder = "mutation/content_no_childs_should_remove";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, "M", fieldsNumber);
    }
    @Test
    public void verify_multiple_queries() throws Exception {

        String testFolder = "multiple_queries";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, null, fieldsNumber);
    }


    @Test
    public void verify_depends_directives() throws Exception {

        String testFolder = "verify_depends_directives";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, null, fieldsNumber);
    }

    @Test
    public void verify_multiple_queries_2() throws Exception {

        String testFolder = "multiple_queries_2";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder, null, fieldsNumber);
    }


    @Test
    void test_aliases() throws Exception {

        String testFolder = "test_aliases";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }

    @Test
    void verify_query_directive_args_no_redundant_value() throws Exception {

        String testFolder = "verify_query_directive_args_no_redundant_value";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }


    @Test
    void test_human_with_unit() throws Exception {

        String testFolder = "test_human_with_unit";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }


    @Test
    void test_simple_named_query() throws Exception {

        String testFolder = "test_simple_named_query";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, "queryName", fieldsNumber);

    }

    @Test
    void test_simple_void_query() throws Exception {

        String testFolder = "test_simple_void_query";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }

    @Test
    void test_simple_void_query_no_alias_no_args() throws Exception {

        String testFolder = "test_simple_void_query_no_alias_no_args";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }

    @Test
    void test_simple_void_query_no_alias() throws Exception {

        String testFolder = "test_simple_void_query_no_alias";
        int fieldsNumber = 2;

        runQueryTestInvoke(testFolder, ParserConsts.VOID_NAME, fieldsNumber);

    }

    @Test
    void test_fragments() throws Exception {

        String testFolder = "test_fragments";
        int fieldsNumber = 6;

        runQueryTestInvoke(testFolder,ParserConsts.VOID_NAME, fieldsNumber);

    }
    @Test
    void test_check_mutation_nested_values_in_arg() throws Exception {

        String testFolder = "check_mutation_nesting_values_n_array_n_values";

        runQueryTestInvoke(testFolder,ParserConsts.VOID_NAME, 0);

    }

    @Test
    void test_check_mutation_nesting_values_1_array_nest_obj_1_n_values() throws Exception {

        String testFolder = "check_mutation_nesting_values_1_array_nest_obj_1_n_values";

        runQueryTestInvoke(testFolder,ParserConsts.VOID_NAME, 0);

    }

    @Test
    void verify_content_nested_levels_reduced() throws Exception {

        String testFolder = "verify_content_nested_levels_reduced";

        runQueryTestInvoke(testFolder,ParserConsts.VOID_NAME, 0);

    }


    private void runQueryTestInvoke(String testFolder, String name, int fieldsNumber) throws Exception {


        runQueryTest(testFolder,name, 0,false);
        /*Class<? extends ParserTest> classInvoker = this.getClass();
        if( classInvoker == null ){
            throw new Exception("kuku");
        }
        GqlTestUtils.runQueryTest(testFolder,name, fieldsNumber, classInvoker);*/
    }


    private void runQueryTest(String testFolder, String queryName, int fieldsNumber, boolean isContainsVariables) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        File testFile = new File(classLoader.getResource(testFolder + "/query.txt").getFile());

        try (Reader targetReader = new FileReader(testFile)) {

            ParserFacade parser = new ParserFacadeImpl();

            GqlDocument gqlDocument = parser.parse(targetReader, null);

            assertNotNull(gqlDocument);

            assertNotNull(gqlDocument.getQueryDefinitionMap());

            String jsonRestFromGql = gqlDocument.toJsonObject().toString();

            assertNotNull(jsonRestFromGql);

            org.json.JSONObject json = new org.json.JSONObject(jsonRestFromGql);
            String identJsonValue = json.toString();
            System.out.println("\nActual Json: \n==========\n\n" + identJsonValue);

            URL url = classLoader.getResource(testFile.getParentFile().getName());

            if( url != null ) {
                File parentDirectory = new File(new URI(url.toString()));
                //System.out.println("parentDirectory.getName()" + parentDirectory.getName());
                File expectedFile = new File(classLoader.getResource(parentDirectory.getName() + "/" + "expected.json").getFile());

                if (expectedFile.exists()) {
                    String expectedContent = new String(Files.readAllBytes(Paths.get(expectedFile.getPath())));
                    expectedContent = expectedContent.replaceAll("\\n", "");
                    System.out.println("Expected json: " + expectedContent);
                    assertTrue(expectedContent.equalsIgnoreCase(json.toString()));
                }


/// --> SAVE THE RESULT - EASY WAY FOR THE DEVELOPER TO CREATE
                /*try (OutputStream os = new FileOutputStream(parentDirectory + "/" + "expected_2.json")) {
                    try (PrintStream printStream = new PrintStream(os)) {
                        printStream.println(jsonRestFromGql);
                    }
                }*/
            }

           // validateJsonLang(jsonRestFromGql);
            // validateJsonStringResult(queryName, jsonRestFromGql);
            List<String> heroQueryCanonicalPaths = parser.getValueCanonicalFields(queryName);
            System.out.println("\n\nCanonical names");
            System.out.println("===============");
            int i=1;
            for (String heroQueryCanonicalPath : heroQueryCanonicalPaths) {
                System.out.println(i + " -> " + heroQueryCanonicalPath);
                i++;
            }


            /*URL resource = classLoader.getResource(testFolder + "/expected_json_file.json");
            if( resource != null ) {
                File fileExpectedJsonDataResult = new File(resource.getFile());

                if (fileExpectedJsonDataResult.exists()) {

                    try (FileReader fileReader = new FileReader(fileExpectedJsonDataResult)) {

                        JSONParser jsonParser = new JSONParser();
                        org.json.simple.JSONObject jsonObjectParsed = (org.json.simple.JSONObject) jsonParser.parse(fileReader);

                        assertNotNull(jsonObjectParsed);
                        org.json.simple.JSONArray operationJsonArray = (org.json.simple.JSONArray) jsonObjectParsed.get("Operations");
                        assertNotNull(operationJsonArray);
                        org.json.simple.JSONObject jsonObjectValue = (org.json.simple.JSONObject)operationJsonArray.get(0);
                        assertTrue(queryName.equalsIgnoreCase(jsonObjectValue.get("name").toString()));
                        assertTrue(ParserConsts.QUERY.equalsIgnoreCase(jsonObjectValue.get("operationType").toString()));

                        assertNotNull(jsonObjectValue.get("fields"));

                    }
                }
            }*/
            /*List<String> heroQueryCanonicalPaths = parser.getValueCanonicalFields(queryName);


            assertNotNull(heroQueryCanonicalPaths);

            System.out.println("number of query canonical path : " + heroQueryCanonicalPaths.size());*/

            //assertTrue(heroQueryCanonicalPaths.size() == fieldsNumber);

           /* File fileExpectedResult = new File(classLoader.getResource(testFolder + "/expected_normalize_fields.txt").getFile());

            try (Scanner scanner = new Scanner(fileExpectedResult)) {
                while (scanner.hasNextLine()) {
                    String valueToVerify = scanner.nextLine();
                    assertTrue(heroQueryCanonicalPaths.contains(valueToVerify));
                }
            }*/
        }
    }

}