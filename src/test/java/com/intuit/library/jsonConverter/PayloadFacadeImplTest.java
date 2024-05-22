package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.GqlDocument;
import com.intuit.library.jsonConverter.model.GqlPayload;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class PayloadFacadeImplTest {




    @Test
    void payload_big_query_kiril() throws Exception {
        String testFolder = "payload_findApps_query_kiril";
        int fieldsNumber = 6;

        runQueryTest(testFolder, "findApps", fieldsNumber, true);
    }
    @Test
    public void payload_getDeveloperLoginInformation_kiril() throws Exception {

        String testFolder = "payload_getDeveloperLoginInformation_kiril";
        int fieldsNumber = 6;

        runQueryTest(testFolder, "getDeveloperLoginInformation", fieldsNumber, true);
    }

    private void runQueryTest(String testFolder, String queryName, int fieldsNumber, boolean isContainsVariables) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/query.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {

            PayloadFacade payloadFacade = new PayloadFacadeImpl(scanner.nextLine());
            payloadFacade.validatePayload();
            payloadFacade.setValidate(true);
            GqlPayload gqlPayload = payloadFacade.loadPayload();

            assertNotNull(gqlPayload);

            GqlDocument document = gqlPayload.getDocument();
            assertNotNull(document);

            assertNotNull(document.getQueryDefinitionMap());

            String jsonRestFromGql = document.toJsonObject().toString();

            assertNotNull(jsonRestFromGql);

            System.out.println("\nJson:\n\n");
            System.out.println(jsonRestFromGql);

            System.out.println("\nMetaData:\n\n"+ gqlPayload.getMetaData().toString());

            validateJsonStringResult(queryName, jsonRestFromGql, isContainsVariables);

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




            List<String> QueryCanonicalPaths = payloadFacade.getValueCanonicalFields(queryName);


            assertNotNull(QueryCanonicalPaths);

            System.out.println("number of query canonical path : " + QueryCanonicalPaths.size());

           // assertTrue(QueryCanonicalPaths.size() == fieldsNumber);
            System.out.println("\nCanonical Path values\n ");
            for (String queryCanonicalPath : QueryCanonicalPaths) {
                System.out.println("\n" + queryCanonicalPath);
            }


            /*File fileExpectedResult = new File(classLoader.getResource(testFolder + "/expected_normalize_fields.txt").getFile());

            try (Scanner scanner = new Scanner(fileExpectedResult)) {
                while (scanner.hasNextLine()) {
                    String valueToVerify = scanner.nextLine();
                    assertTrue(QueryCanonicalPaths.contains(valueToVerify));
                }
            }*/
        }
    }

    private static void validateJsonStringResult(String queryName, String jsonRestFromGql, boolean isContainsVariables) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONObject jsonObjectParsed = (org.json.simple.JSONObject) jsonParser.parse(jsonRestFromGql);
        assertNotNull(jsonObjectParsed);
        assertNotNull(jsonObjectParsed.get(ParserConsts.OPERATIONS));
        if( isContainsVariables ) {
            assertNotNull(jsonObjectParsed.get(ParserConsts.VARIABLES));
        }
    }
}