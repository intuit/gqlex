package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.GqlDocument;
import com.intuit.library.jsonConverter.model.GqlPayload;
import com.intuit.library.jsonConverter.services.DefinitionParser;
import com.intuit.library.jsonConverter.services.DefinitionParserImpl;
import com.intuit.library.jsonConverter.services.FragmentParserImpl;
import graphql.language.Document;

import java.io.Reader;
import java.util.Map;

public interface ParserFacade extends DefinitionParser {

    //public String generateJsonFromGraphql();
    DefinitionParserImpl getDefinitionParser();
    FragmentParserImpl getFragmentParser();
    GqlPayload parse(String queryString, Map<String, Object> variables, Document document) throws Exception;
    GqlDocument parse(Reader documenÏtReader, Map<String, Object> variables) throws Exception;
}
