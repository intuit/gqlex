package com.intuit.library.jsonConverter;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intuit.library.jsonConverter.model.GqlDocument;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GqlTestUtils {

    public static void runQueryTest(String testFolder, String queryName, int fieldsNumber, Class classInvoker) throws Exception {

        runQueryTest(testFolder, "query.txt",queryName ,fieldsNumber,classInvoker);
    }
    public static void runQueryTest(String testFolder, String fileName, String queryName, int fieldsNumber, Class classInvoker) throws Exception {

        if( classInvoker == null){
            throw new IllegalArgumentException("classInvoker must be set to load the resources");
        }

        ClassLoader classLoader = classInvoker.getClassLoader();

        if( classLoader == null ){
            throw new Exception("classLoader is null, test failed");
        }

        //String path = classLoader.getResource(".").getPath();
        String name = "./"+  testFolder + "/" + fileName;



        URL resource = classLoader.getResource(name);
        if( resource == null ){
            throw new Exception("resource is null, test failed " + classLoader.getResource(".").getPath() );
        }

        //File file = new File(resource.getFile());

        return;
        //runQueryTest(file,  queryName, fieldsNumber,classLoader);
    }
    public static void runQueryTest( File testFile, String queryName, int fieldsNumber,  ClassLoader classLoader) throws Exception {

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

            validateJsonLang(jsonRestFromGql);
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

    public static void validateJsonLang(String jsonRestFromGql) {

        boolean isSuccess = true;
        String errMsg  ="";
        try {
            JsonParser.parseString(jsonRestFromGql);
        } catch (JsonSyntaxException e) {
            errMsg = e.toString();
            isSuccess =  false;
        }
        assertTrue(isSuccess, "invalid json, Error : " + errMsg);

    }

    public static void validateJsonStringResult(String queryName, String jsonRestFromGql) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONObject jsonObjectParsed = (org.json.simple.JSONObject) jsonParser.parse(jsonRestFromGql);
        assertNotNull(jsonObjectParsed);
        org.json.simple.JSONArray operationJsonArray = (org.json.simple.JSONArray) jsonObjectParsed.get(ParserConsts.OPERATIONS);
        assertNotNull(operationJsonArray);
        org.json.simple.JSONObject jsonObjectValue = (org.json.simple.JSONObject)operationJsonArray.get(0);
        // check it
       /* assertTrue(queryName.equalsIgnoreCase(jsonObjectValue.get("methodName").toString()));
        assertTrue(ParserConsts.QUERY.equalsIgnoreCase(jsonObjectValue.get("operationType").toString()));*/

        assertNotNull(jsonObjectValue.get("fields"));
    }

}
