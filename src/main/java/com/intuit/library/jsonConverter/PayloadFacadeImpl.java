package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.*;
import com.intuit.library.jsonConverter.services.GqlParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PayloadFacadeImpl implements PayloadFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadFacadeImpl.class);

    private ParserFacade parserFacade ;

    private GqlParserService gqlParserService;

    private boolean isValidate = false;

    public PayloadFacadeImpl(String payload){
        if(payload == null){
            throw new IllegalArgumentException("payload is null");
        }
        gqlParserService = new GqlParserService(payload);
    }

    @Override
    public GqlValidation validatePayload() {
        GqlValidation gqlValidation = null;
        try{
            LOGGER.debug("validate the payload");
            gqlValidation = gqlParserService.validate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate", e);
        }
        isValidate = true;
        return gqlValidation;
    }

    //  Load payload and convert it GqlDocument
    @Override
    public GqlPayload loadPayload() {
        if(!isValidate){
            // todo what if the payload is not validated
            LOGGER.error("payload is not validated");
            throw new RuntimeException("Payload is not validated");
        }
        if(parserFacade == null){
            parserFacade = new ParserFacadeImpl();
        }
        String queryValue = gqlParserService.getGqlPayload().getQueryValue();
        Map<String, Object>  variablesMap = gqlParserService.getGqlPayload().getVariablesMap();
        GqlPayload gqlPayload = null;
        try {
            LOGGER.debug("Parse the query value and the variables");
            gqlPayload = parserFacade.parse(queryValue, variablesMap,gqlParserService.getDocument());
        } catch (Exception e) {
            LOGGER.error("Failed to parse the query value, Exception: {}. stack :{}", e,e.getStackTrace());

            throw new RuntimeException("Failed to parse", e);
        }
        // set the query value
        gqlPayload.getDocument().setQuery(queryValue);

        return gqlPayload;
    }

    @Override
    public List<String> getValueCanonicalFields(String operationName){
        if(parserFacade == null){
            return null;
        }
        return parserFacade.getDefinitionParser().getValueCanonicalFields(operationName);
    }

    @Override
    public Map<String, GqlQueryDefinition> getQueryOperationMap(){
        if(parserFacade == null){
            return null;
        }
        return parserFacade.getDefinitionParser().getQueryDefinitionMap();
    }

    public void setValidate(boolean validate) {
        isValidate = validate;
    }
}
