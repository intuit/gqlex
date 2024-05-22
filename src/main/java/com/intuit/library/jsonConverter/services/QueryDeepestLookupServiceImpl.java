package com.intuit.library.jsonConverter.services;

import graphql.language.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryDeepestLookupServiceImpl implements QueryDeepestLookupService {
    private Map<String, Integer> fragmentToDepth = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDeepestLookupServiceImpl.class);
    @Override
    public int checkDepthLimit(Document document) {
        LOGGER.debug("check depth limit for document");

        calculateDepthLimitOperationDefinition(document);

        int depthValue = checkDepthLimitOperationDefinition(document);

        LOGGER.debug("Depth for document is : {}", depthValue);
        return depthValue;

    }

    private void calculateDepthLimitOperationDefinition(Document document) {
        List<Node> fragmentDefinition = document.getChildren().stream()
                .filter(n -> (n.getClass() == FragmentDefinition.class)).collect(Collectors.toList());

        if (fragmentDefinition == null || fragmentDefinition.size() == 0) {
            LOGGER.debug("no fragment to calculate depth according to");
            return;
        }
        fragmentToDepth = new HashMap<>();
        for (Node fragmentDefinitionNode : fragmentDefinition) {
            String fragmentName = ((FragmentDefinition) fragmentDefinitionNode).getName();

            int depthValue = checkDepthLimitForNode(fragmentDefinitionNode);
            fragmentToDepth.put(fragmentName, depthValue);

            LOGGER.debug("Calulate depth for fragment {}, depath is: {}", fragmentName,depthValue);
        }

    }

    private int checkDepthLimitOperationDefinition(Document document) {
        Optional<Node> queryNode = document.getChildren().stream()
                .filter(n -> (n.getClass() == OperationDefinition.class))
                .findFirst();

        Node queryNodeName = queryNode.get();
        if( queryNodeName instanceof Field) {
            LOGGER.debug("check depth for node: {}", ((Field)queryNodeName).getName());
        }else{
            LOGGER.debug("check depth for node");
        }
        return checkDepthLimitForNode(queryNodeName);
    }

    private int checkDepthLimitForNode(Node queryNode) {

        int depth = 0;
        String nodeType = queryNode.getClass().getSimpleName().toUpperCase();

        if (nodeType.equals(Field.class.getSimpleName().toUpperCase()) ||
                nodeType.equals(FragmentDefinition.class.getSimpleName().toUpperCase())) {
            depth += 1;
        }

        if( fragmentToDepth != null && fragmentToDepth.size() > 0 ) {
            if (nodeType.equals(FragmentSpread.class.getSimpleName().toUpperCase())) {

                String fragmentName = ((FragmentSpread) queryNode).getName();
                Integer fragmentDepthPreCalc = fragmentToDepth.get(fragmentName);
                depth += fragmentDepthPreCalc;
                LOGGER.debug("Fragment {} depth is : {}, set node depth to: {}",fragmentName, fragmentDepthPreCalc, depth);
            }
        }

        List<Node> nodeChildren = queryNode.getChildren();
        int maxChildDepth = 0;
        for (int i = 0; i < nodeChildren.size(); i++) {
            final int currentChildDepth = checkDepthLimitForNode(nodeChildren.get(i));
            maxChildDepth = Math.max(maxChildDepth, currentChildDepth);
        }
        depth += maxChildDepth;

        LOGGER.debug("set depth to {}", depth);
        return depth;

    }
}
