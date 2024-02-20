package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.model.GqlValidation;
import graphql.com.google.common.base.Strings;
import graphql.language.Definition;
import graphql.language.OperationDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class GqlValidationService {
    public static GqlValidation isVoidOrChainQuery(List<Definition> definitions){
        GqlValidation gqlValidation = new GqlValidation();
        List<Definition> operationDefList = definitions.stream().filter(definition -> definition instanceof OperationDefinition).collect(Collectors.toList());
        if(operationDefList.size()>1){
            gqlValidation.setChainQuery(true);
        }else {
            gqlValidation.setChainQuery(false);
        }
        String operationName = ((OperationDefinition) operationDefList.get(0)).getName();
        if(Strings.isNullOrEmpty(operationName)){
            gqlValidation.setVoidQuery(true);
        }else{
            gqlValidation.setVoidQuery(false);
        }
        return gqlValidation;
    }
}
