package com.intuit.gqlex.transformer;

import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.common.RawPayload;
import com.intuit.gqlex.common.eXtendGqlWriter;
import com.intuit.gqlex.gxpath.selector.SelectorFacade;
import com.intuit.gqlex.gxpath.syntax.SyntaxBuilder;
import com.intuit.gqlex.traversal.GqlTraversal;
import graphql.com.google.common.base.Strings;
import graphql.language.Node;

import java.util.UUID;

import static com.intuit.gqlex.transformer.TRANSFORM_COMMAND_TYPE.ADD_SIBLING;

public class TransformExecutor {

    private final TransformBuilder transformBuilder;

    public TransformExecutor(TransformBuilder transformBuilder) {

        this.transformBuilder = transformBuilder;
    }

    public RawPayload execute(RawPayload rawPayload){
        RawPayload rawPayloadExecuted = new RawPayload(rawPayload);
        String graphQuery = rawPayload.getQueryValue();

        Node graphNode =  GqlTraversal.parseDocument(graphQuery);

        for (TransformCommand transformCommand : transformBuilder.getTransformCommands()) {

            SelectorFacade selectorFacade = new SelectorFacade();

            // transaction per command
            UUID transactionId = UUID.randomUUID();

            GqlNodeContext nodeContext = null;
            do {
                SyntaxBuilder syntaxBuilder = transformCommand.getSyntaxBuilder();

                if(transformCommand.getTransformCommandType().equals(TRANSFORM_COMMAND_TYPE.REMOVE) ||
                        transformCommand.getTransformCommandType().equals(TRANSFORM_COMMAND_TYPE.DUPLICATE)){
                    if (syntaxBuilder != null) {
                        nodeContext = selectorFacade.selectSingle(graphNode, syntaxBuilder.build());
                    } else if (!Strings.isNullOrEmpty(transformCommand.getSearchPath())) {
                        nodeContext = selectorFacade.selectSingle(graphNode, transformCommand.getSearchPath());
                    } else {
                        throw new IllegalArgumentException("No selection search path set, please check TransformCommand");
                    }
                }else {
                    if (syntaxBuilder != null) {
                        nodeContext = selectorFacade.selectNext(graphNode, syntaxBuilder.build(), transactionId);
                    } else if (!Strings.isNullOrEmpty(transformCommand.getSearchPath())) {
                        nodeContext = selectorFacade.selectNext(graphNode, transformCommand.getSearchPath(), transactionId);
                    } else {
                        throw new IllegalArgumentException("No selection search path set, please check TransformCommand");
                    }
                }

                if( nodeContext != null) {

                    if (transformCommand.getTransformCommandType().equals(ADD_SIBLING)) {
                        String transactionIdValue = GeneratedComment.getTransactionIdValue(nodeContext.getParentNode());
                        if (!Strings.isNullOrEmpty(transactionIdValue) && transactionIdValue.equals(transactionId.toString())) {
                            nodeContext = null;
                        }
                    }
                }

                if( nodeContext != null) {
                    graphNode = executeCommand(transformCommand, nodeContext, transactionId);
                }

            }while(nodeContext != null
                    &&
                    !transformCommand.getTransformCommandType().equals(TRANSFORM_COMMAND_TYPE.DUPLICATE));

        }

        graphQuery = eXtendGqlWriter.writeToString(graphNode);
        rawPayloadExecuted.setQueryValue(graphQuery);
        return rawPayloadExecuted;
    }

    private static Node executeCommand(TransformCommand transformCommand,
                                       GqlNodeContext gqlNodeContext,
                                       UUID transactionId) {

        Node node = null;

        switch (transformCommand.getTransformCommandType()) {

            case ADD_CHILDREN:
                node = TransformUtils.addChildren(gqlNodeContext,
                        transformCommand.getTargetNode(),transactionId);
                break;
            case ADD_SIBLING:
                node = TransformUtils.addSibling(gqlNodeContext,
                        transformCommand.getTargetNode(),transactionId);
                break;
            case REMOVE:
                node = TransformUtils.removeChild(gqlNodeContext);
                break;
            case DUPLICATE:
                TransformCommandAttributeValue transformCommandAttributeValue1 =
                        transformCommand.getAttributeValueMap().get(
                                TransformBuilder.DUPLICATE_TRANSFORM_ATTRIBUTE_KEY);

                node = TransformUtils.duplicateChild(gqlNodeContext,
                        (Integer) transformCommandAttributeValue1.getValue(),transactionId);
                break;
            case UPDATE:
                TransformCommandAttributeValue transformCommandAttributeValue =
                        transformCommand.getAttributeValueMap().get(TransformBuilder.NODE_NEW_NAME_VALUE);

                if( transformCommandAttributeValue != null) {
                    node = TransformUtils.updateNodeName(gqlNodeContext,
                            transformCommandAttributeValue.getValue().toString(), transactionId);
                    break;
                }

                transformCommandAttributeValue =
                        transformCommand.getAttributeValueMap().get(TransformBuilder.NODE_NEW_ALIAS_VALUE);
                if( transformCommandAttributeValue != null) {
                    node = TransformUtils.updateNodeAlias(gqlNodeContext,
                            transformCommandAttributeValue.getValue().toString(), transactionId);

                }

                break;
        }

        return node;
    }
}
