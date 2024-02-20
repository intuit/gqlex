package com.intuit.library.common;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GqlPayloadLoader {
    private static final Logger logger = LoggerFactory.getLogger(GqlPayloadLoader.class);


    public RawPayload load(String payload){

        RawPayload rawPayload = new RawPayload();
        JSONParser jsonParser = new JSONParser();
        try {
            logger.debug("parse the  payload");
            rawPayload.setJsonObjectParsed( (org.json.simple.JSONObject) jsonParser.parse(payload) );
        } catch (ParseException e) {
            logger.error("Failed to parse the payload, Exception: {0}", e.getMessage());
            throw new RuntimeException("Failed to load graphql payload");
        }

        return rawPayload;
    }


}
