package com.intuit.library.jsonConverter;

import com.intuit.library.jsonConverter.model.GqlPayload;
import com.intuit.library.jsonConverter.model.GqlQueryDefinition;
import com.intuit.library.jsonConverter.model.GqlValidation;

import java.util.List;
import java.util.Map;

public interface PayloadFacade {
    //GqlPayload generateGqlPayload(String payload);
    /*
    validate the payload
     */
    GqlValidation validatePayload();
    /*
        Load payload and get the @GqlDocument
        use of the @GqlDocument, you can get the json value of the graphql document
         */
    GqlPayload loadPayload();

    /*
    get query operation tree value map
     */
    Map<String, GqlQueryDefinition> getQueryOperationMap();

    /*
    get canonical path value for graphql fields
     */
    List<String> getValueCanonicalFields(String operationName);

    void setValidate(boolean b);
}
