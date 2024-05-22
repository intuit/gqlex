package com.intuit.library.jsonConverter.model;

import com.google.gson.JsonObject;
import com.intuit.library.jsonConverter.PayloadFacade;
import com.intuit.library.jsonConverter.PayloadFacadeImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GqlDocumentTest {

    private static final String QUERY = "\"mutation userSession($payload: CreateCompany_UserSessionInput!) { createCompany_UserSession(input: $payload) {clientMutationId,companyUserSession { id,globalUserId, companyPersonaId } }}\"";
    private static final String VARIABLES = "{\"payload\":{\"clientMutationId\":\"A\",\"companyUserSession\":{\"actionType\":\"logout\",\"companyId\":\"D\",\"additionalParameters\":[{\"name\":\"isFirm\",\"value\":false}],\"deleted\":true,\"id\":\"B\",\"globalUserId\":\"C\"}}}";
    private static final String QUERY_KEY = "query";
    private static final String VARIABLES_KEY = "variables";

    @Test
    public void toGsonObjectTest(){
        String payload = String.format("{\"%s\":%s,\"%s\":%s}",QUERY_KEY,QUERY,VARIABLES_KEY,VARIABLES);
        PayloadFacade payloadFacade = new PayloadFacadeImpl(payload);
        payloadFacade.validatePayload();
        payloadFacade.setValidate(true);
        GqlPayload gqlPayload = payloadFacade.loadPayload();
        JsonObject gson = gqlPayload.getDocument().toGsonObject();
        Assertions.assertEquals(QUERY,gson.get(QUERY_KEY).toString());
        Assertions.assertEquals(VARIABLES,gson.get(VARIABLES_KEY).toString());
    }
}