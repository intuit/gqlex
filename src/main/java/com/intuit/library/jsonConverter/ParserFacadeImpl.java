package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.*;
import com.intuit.library.jsonConverter.services.DefinitionParserImpl;
import com.intuit.library.jsonConverter.services.FragmentParserImpl;
import com.intuit.library.jsonConverter.services.QueryDeepestLookupService;
import com.intuit.library.jsonConverter.services.QueryDeepestLookupServiceImpl;
import graphql.language.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.*;


public class ParserFacadeImpl  implements ParserFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserFacadeImpl.class);
    private FragmentParserImpl fragmentParser;
    private DefinitionParserImpl definitionParser;

    public GqlPayload parse(String queryString, Map<String, Object> variables,Document document) throws Exception {
        LOGGER.debug("calculate the metadata");
        GqlMetaData metaData = new GqlMetaData();

        // todo - eliminate meta data use of indicator
        metaData.setNumberOfWhiteSpace(StringUtils.countMatches(queryString,StringUtils.SPACE));
        StringTokenizer stringTokenizer = new StringTokenizer(queryString);
        metaData.setNumberOfToken(stringTokenizer.countTokens());

        QueryDeepestLookupService queryDeepestLookupService = new QueryDeepestLookupServiceImpl();
        metaData.setMaxDepth( queryDeepestLookupService.checkDepthLimit(document) );

        GqlDocument parsedGqlDocument = getParsedGqlDocument(variables, document);


        Map<String, GqlQueryDefinition> queryDefinitionMap = parsedGqlDocument.getQueryDefinitionMap();

        boolean isAppendOperationType = false;
        for (Map.Entry<String, GqlQueryDefinition> stringGqlQueryDefinitionEntry : queryDefinitionMap.entrySet()) {
            if( ! isAppendOperationType) {
                FieldNameDescriptor fieldNameDescriptor = stringGqlQueryDefinitionEntry.getValue().getGqlTree().getGqlTreeNode().getFieldNameDescriptor();
                if (fieldNameDescriptor.getTypeName().equals(ParserConsts.OPERATION_TYPE)) {
                    metaData.setOperationType(fieldNameDescriptor.getName().toLowerCase());
                    if(fieldNameDescriptor.getName().equals(OperationDefinition.Operation.QUERY.name())){
                        metaData.setMethod(ParserConsts.GET_METHOD);
                    } else if(fieldNameDescriptor.getName().equals(OperationDefinition.Operation.MUTATION.name())){
                        metaData.setMethod(ParserConsts.POST_METHOD);
                    }
                    isAppendOperationType = true;
                }
            }
            metaData.setOperationName(stringGqlQueryDefinitionEntry.getKey());
        }


        LOGGER.debug("metadata : {}", metaData.toString() );
        return new GqlPayload(parsedGqlDocument, metaData);
    }

    private GqlDocument getParsedGqlDocument(Map<String, Object> variables, Document document) throws Exception {
        System.out.println("\nGql: \n=======\n\n" + AstPrinter.printAst(document));

        return parseDocumentIntern(document, variables);
    }

    public GqlDocument parse(Reader documentReader, Map<String, Object> variables) throws Exception {
        graphql.parser.Parser parser = new graphql.parser.Parser();
        Document document = parser.parseDocument(documentReader);
        return getParsedGqlDocument(variables, document);
    }

    private GqlDocument parseDocumentIntern(Document document, Map<String, Object> variables) throws Exception {
        if (document == null) {
            throw new NullPointerException("graphql document to parse is null");
        }

        List<Definition> definitions = document.getDefinitions();
        if (definitions == null || definitions.size() == 0) {
            LOGGER.debug("no definitions defined in graphql document");
            return null;
        }

        fragmentParser = new FragmentParserImpl();
        fragmentParser.parseDefinitionsForFragments(definitions);

        GqlDocument gqlDocument = new GqlDocument();
        definitionParser = new DefinitionParserImpl(fragmentParser);
        gqlDocument.setQueryDefinitionMap( definitionParser.parseDefinitions(definitions) );

        gqlDocument.setVariables(variables);

        return gqlDocument;
    }

    public FragmentParserImpl getFragmentParser() {
        return fragmentParser;
    }

    public DefinitionParserImpl getDefinitionParser() {
        return definitionParser;
    }

    @Override
    public Map<String, GqlQueryDefinition> parseDefinitions(List<Definition> definitions) throws Exception {
        return definitionParser.parseDefinitions(definitions);
    }

    @Override
    public List<GqlQueryDefinition> getQueryDefinition(String operationName) {
        return definitionParser.getQueryDefinition(operationName);
    }

    @Override
    public List<String> getValueCanonicalFields(String operationName) {
        return definitionParser.getValueCanonicalFields(operationName);
    }
    /*public String generateJsonFromGraphql() {
        return definitionParser.generateJsonFromGraphql();
    }*/
}
