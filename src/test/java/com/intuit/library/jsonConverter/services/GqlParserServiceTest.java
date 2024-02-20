package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.model.GqlValidation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GqlParserServiceTest {

    @Test
    public void validateVoidQueryTest() {
        String payload = "{\"query\":\"query { findApps { id, name, description, appType, appStatus, appStatusReason } }\"}";
        GqlParserService gqlParserService = new GqlParserService(payload);
        GqlValidation validation = gqlParserService.validate();
        assertEquals(true, validation.isVoidQuery());
    }

    @Test
    public void validateChainQueryTest(){
        // craete string with chain query
        String payload = "{\"query\":\"query GetLoggedInUserName {  me {    name @export(as: \\\"search\\\")  }}" +
                "query GetPostsContainingString @depends(on: \\\"GetLoggedInUserName\\\") {  posts(filter: { search: $search })" +
                " {    id    title  }}\"}";
        GqlParserService gqlParserService = new GqlParserService(payload);
        GqlValidation validation = gqlParserService.validate();
        assertEquals(true, validation.isChainQuery());

    }

    @Test
    public void validateFailedParsingTest(){
        String payload = "{\"query\":\"query GetLoggedInUserName {  me {    name @export(as: \"search\")  }}" +
                "query GetPostsContainingString @depends(on: \"GetLoggedInUserName\") {  posts(filter: { search: $search })" +
                " {    id    title  }}\"}";
        assertThrows(RuntimeException.class, () -> {
            GqlParserService gqlParserService = new GqlParserService(payload);
            GqlValidation validation = gqlParserService.validate();
        });
    }

    @Test
    public void validateJsonWithoutQueryTest(){
        // create string of simple json 'query' key is missing
        String payload = "{\"test\":\"query GetLoggedInUserName {  me {    name @export(as: \\\"search\\\")  }}" +
                "GetPostsContainingString @depends(on: \\\"GetLoggedInUserName\\\") {  posts(filter: { search: $search })" +
                " {    id    title  }}\"}";
        assertThrows(IllegalArgumentException.class, () -> {
            GqlParserService gqlParserService = new GqlParserService(payload);
            GqlValidation validation = gqlParserService.validate();
        });
    }

    @Test
    public void getVariablesMapTest(){
        String payload = "{\"query\":\"query GetLoggedInUserName {  me {    name @export(as: \\\"search\\\")  }}" +
                "query GetPostsContainingString @depends(on: \\\"GetLoggedInUserName\\\") {  posts(filter: { search: $search })" +
                " {    id    title  }}\", \"variables\": {\"search\": \"test\"}}";
        GqlParserService gqlParserService = new GqlParserService(payload);
        gqlParserService.validate();
        assertEquals("test", gqlParserService.getGqlPayload().getVariablesMap().get("search"));
    }

    @Test
    public void getVariablesMapNotNullTest(){
        String payload = "{\"query\":\"query GetLoggedInUserName {  me {    name @export(as: \\\"search\\\")  }}" +
                "query GetPostsContainingString @depends(on: \\\"GetLoggedInUserName\\\") {  posts(filter: { search: $search })" +
                " {    id    title  }}\", \"variables\": {\"search\": \"test\"}}";
        GqlParserService gqlParserService = new GqlParserService(payload);
        gqlParserService.validate();
        Map<String, Object> objectMap =  gqlParserService.getGqlPayload().getVariablesMap();
        int hashCodeMap = objectMap.hashCode();
        int hashCodeMap2 = gqlParserService.getGqlPayload().getVariablesMap().hashCode();
        assertEquals(hashCodeMap, hashCodeMap2);
    }

}