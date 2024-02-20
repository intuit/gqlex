package com.intuit.library.eXtendGql;

import com.intuit.library.common.GqlNodeContext;
import com.intuit.library.common.GqlPayloadLoader;
import com.intuit.library.common.RawPayload;
import com.intuit.library.eXtendGql.gxpath.selector.SelectorFacade;
import com.intuit.library.eXtendGql.gxpath.syntax.SyntaxPath;
import com.intuit.library.eXtendGql.transformer.TransformBuilder;
import com.intuit.library.eXtendGql.transformer.TransformExecutor;
import graphql.com.google.common.base.Preconditions;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class eXtendGqlFacade {
    private static final Logger logger = LoggerFactory.getLogger(eXtendGqlFacade.class);

    private RawPayload payload;

    public RawPayload getPayload() {
        return payload;
    }

    /*
        1. load
        3. transform node / variable
         */
    public void loadPayload(String payloadData) {

        Preconditions.checkNotNull(payloadData);

        GqlPayloadLoader gqlPayloadLoader = new GqlPayloadLoader();

        payload = gqlPayloadLoader.load(payloadData);
    }


    public RawPayload transform(TransformBuilder transformBuilder) {

        Preconditions.checkNotNull(transformBuilder);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        return transformExecutor.execute(payload);
    }

    public List<GqlNodeContext> select(SyntaxPath syntaxPath){
        Preconditions.checkArgument(syntaxPath != null );

        Preconditions.checkArgument(syntaxPath.isValid());
        SelectorFacade selectorFacade = new SelectorFacade();
        return selectorFacade.selectMany(payload.getQueryValue(), syntaxPath);
    }

    public void transformVariable(String key, JSONObject value){
        changeNode(key, value.toString());
    }

    public void transformVariable(String key, Object value){
        changeNode(key, value);
    }

    private void changeNode(String key, Object value) {
        JSONObject varsJsonObject = (JSONObject) payload.getVariables();

        if( logger.isDebugEnabled())
            logger.debug("\n\nVariables Before transform: \n=================\n\n" + varsJsonObject.toJSONString() + "\n");

        varsJsonObject.put(key, value);

        if( logger.isDebugEnabled())
            logger.debug("\n\nVariables Before transform: \n=================\n\n" + varsJsonObject.toJSONString() + "\n");
    }

    public void transformVariable(String key, JSONArray value){
        changeNode(key, value.toString());
    }
}
