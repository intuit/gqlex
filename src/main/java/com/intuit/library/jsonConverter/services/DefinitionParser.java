package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.model.GqlQueryDefinition;
import graphql.language.Definition;

import java.util.List;
import java.util.Map;

public interface DefinitionParser {
     Map<String, GqlQueryDefinition> parseDefinitions(List<Definition> definitions) throws Exception;

     List<GqlQueryDefinition> getQueryDefinition(String operationName);

     List<String> getValueCanonicalFields(String operationName);
}
