package com.intuit.library.jsonConverter.services;
import com.intuit.library.common.RawPayload;
import com.intuit.library.common.GqlPayloadLoader;
import com.intuit.library.jsonConverter.model.GqlValidation;
import graphql.language.Definition;
import graphql.language.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GqlParserService {
    private static final Logger logger = LoggerFactory.getLogger(GqlParserService.class);
    private String payload;

    private Document document;

    public GqlParserService(String payload) {
        this.payload = payload;
    }

    private RawPayload rawPayload = null;

    public RawPayload getGqlPayload() {
        return rawPayload;
    }

    public GqlValidation validate() {

        GqlPayloadLoader gqlPayloadLoader = new GqlPayloadLoader();
        rawPayload = gqlPayloadLoader.load(payload);
        document = parseDocument(rawPayload.getQueryValue());
        List<Definition> definitions =  document.getDefinitions();
        return GqlValidationService.isVoidOrChainQuery(definitions);
    }

    private static Document parseDocument(String queryValue){
        graphql.parser.Parser parser = new graphql.parser.Parser();
        return parser.parseDocument(queryValue);
    }

    public Document getDocument() {
        return document;
    }
}
