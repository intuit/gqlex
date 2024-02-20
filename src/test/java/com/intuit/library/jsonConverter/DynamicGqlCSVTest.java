package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.GqlPayload;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Deprecated
public class DynamicGqlCSVTest {
    //@TestFactory //yaron
    public Stream<DynamicTest> dynamic_factory_verify_csv_from_data_lake() throws IOException {

        String testFolder = "athena/csv";

        ClassLoader classLoader = getClass().getClassLoader();

        Path tempDirectory = Files.createTempDirectory("benchmark_gql_to_json_benchmark");

        System.out.println("Benchmark result will be saved under: " + tempDirectory.toString());

        URL resource = classLoader.getResource(testFolder);
        if( resource == null){
            throw new RuntimeException("resourceFile is null, please check whether files to verify exist under " +testFolder);
        }
        String resourceFile = resource.getFile();
        if( resourceFile == null ){
            throw new RuntimeException("resourceFile is null, please check whether files to verify exist under " +testFolder);
        }
        File file = new File(resourceFile);
        File[] listFiles = file.listFiles();

        if( listFiles == null ){
            throw new RuntimeException("no files for tests");
        }

        List<FileTestValue> valueToTestList = new ArrayList<>();
        for (File listFile : listFiles) {
            try (Scanner scanner = new Scanner(listFile)) {
                while (scanner.hasNextLine()) {
                    String valueToVerify = scanner.nextLine();
                    valueToVerify = valueToVerify.replaceAll("\"\"", "\"");
                    valueToVerify = valueToVerify.substring(1, valueToVerify.length() - 1);

                    valueToTestList.add(new FileTestValue(listFile.getName(), valueToVerify));

                }
            }
        }

        AtomicInteger atomicIntegerModuloVerifyGqlChanges = new AtomicInteger();
        return valueToTestList.stream().map(gqlValueToTest -> DynamicTest.dynamicTest(
                MessageFormat.format("FileName: {0} > Verify GQL->Json for {1}  --> [{2}]", gqlValueToTest.fileName,
                        gqlValueToTest.getValueToVerify().hashCode(), gqlValueToTest.getValueToVerify()),
                () -> {


                    atomicIntegerModuloVerifyGqlChanges.getAndIncrement();


                    JSONParser parser = new JSONParser();

                    //Use JSONObject for simple JSON and JSONArray for array of JSON.

                    String valueToVerify = gqlValueToTest.getValueToVerify();
                    JSONObject data;
                    try {
                        data = (JSONObject) parser.parse(new StringReader(valueToVerify));
                    } catch (Exception ex) {
                        System.err.println("Exception while trying to parse : " + valueToVerify);
                        return;
                    }
                    String requestBodyStr = (String) data.get("requestBody");
                    requestBodyStr = requestBodyStr.replaceAll("\\\"", "\"");
                    requestBodyStr = requestBodyStr.replaceAll(";\"", "\"");
                    PayloadFacade payloadFacade = new PayloadFacadeImpl(requestBodyStr);
                    //System.out.println("GQL: "+ requestBodyStr);
                    GqlPayload gqlPayload;

                    GqlPayload gqlPayload1 = null;
                    boolean isVerifyChangesModuleValue = atomicIntegerModuloVerifyGqlChanges.get() % 5 == 0;
                    try {
                        payloadFacade.validatePayload();
                        payloadFacade.setValidate(true);
                        gqlPayload = payloadFacade.loadPayload();
                        if (isVerifyChangesModuleValue) {
                            gqlPayload1 = payloadFacade.loadPayload();
                        }
                    } catch (Exception ex) {
                        System.err.println("Exception while trying to load payload: " + requestBodyStr);
                        //throw new RuntimeException(ex);
                        return;
                    }
                    if (isVerifyChangesModuleValue) {
                        assertNotNull(gqlPayload);
                    }
                    //  assertNotNull(gqlPayload1);

                    String jsonString = gqlPayload.getDocument().toJsonObject().toString();
                    org.json.JSONObject mainJson = new org.json.JSONObject(jsonString);
                    assertNotNull(jsonString);

                    GqlTestUtils.validateJsonLang(jsonString);

                    if (isVerifyChangesModuleValue) {
                        String jsonStringB = gqlPayload1.getDocument().toJsonObject().toString();
                        GqlTestUtils.validateJsonLang(jsonStringB);

                        assertEquals(jsonString, jsonStringB);
                    }
                    String identJsonValue = mainJson.toString(4);

                    System.out.println("\nJson: \n==========\n\n" + identJsonValue);

                    System.out.println("\nMetaData:\n=============\n\n"+ gqlPayload.getMetaData().toString());


                    saveResult(tempDirectory.toString(), valueToVerify, identJsonValue);

                }
        ));
    }

    private void saveResult(String tempDirectory, String gql, String json) throws IOException {
        String directoryName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());

        File todayBenchmarkResultFolder = new File(String.valueOf(Paths.get(tempDirectory, directoryName)));

        boolean isTodayBenchmarkResultFolderExist = true;
        if (!todayBenchmarkResultFolder.exists()) {
            isTodayBenchmarkResultFolderExist = todayBenchmarkResultFolder.mkdir();
        }
       /* if (isTodayBenchmarkResultFolderExist) {
            // save gql
            Path gqlPath = Paths.get(String.valueOf(todayBenchmarkResultFolder), gql.hashCode() + "___GQL");
            Files.writeString(gqlPath, gql);

            Path jsonPath = Paths.get(String.valueOf(todayBenchmarkResultFolder), gql.hashCode() + "___JSON");
            Files.writeString(jsonPath, json);

            // save json
        }*/

    }


    class FileTestValue {
        String fileName;
        String valueToVerify;

        public FileTestValue(String fileName, String valueToVerify) {
            this.fileName = fileName;
            this.valueToVerify = valueToVerify;
        }

        public String getValueToVerify() {
            return valueToVerify;
        }

    }

}
