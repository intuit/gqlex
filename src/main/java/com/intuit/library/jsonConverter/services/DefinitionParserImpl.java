package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.*;
import com.intuit.library.jsonConverter.model.tree.GqlTree;
import com.intuit.library.jsonConverter.model.tree.GqlTreeNode;
import com.intuit.library.jsonConverter.model.tree.TreeNodePath;
import graphql.com.google.common.base.Strings;
import graphql.language.*;
//import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionParserImpl implements DefinitionParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionParserImpl.class);

    private final FragmentParserImpl fragmentParser;
    private Map<String, GqlQueryDefinition> queryDefinitionMap;

    public DefinitionParserImpl(FragmentParserImpl fragmentParser) {
        this.fragmentParser = fragmentParser;
    }

    private String getOperationDefinitionName(OperationDefinition operationDefinition) {
        return getOperationDefinitionCoreValue(operationDefinition.getName());
    }

    private String getOperationDefinitionCoreValue(String name) {
        if (Strings.isNullOrEmpty(name)) {
            name = ParserConsts.VOID_NAME;
        }
        return name;
    }

    public Map<String, GqlQueryDefinition> parseDefinitions(List<Definition> definitions) {
        queryDefinitionMap = new HashMap<>(definitions.size());

        for (Definition definition : definitions) {
            if (definition instanceof OperationDefinition) {
                GqlQueryDefinition gqlQueryDefinition = handleOperationDefinition(definition);
                queryDefinitionMap.put(gqlQueryDefinition.getDefinitionName(), gqlQueryDefinition);
            }
        }

        return queryDefinitionMap;
    }

    public Map<String, GqlQueryDefinition> getQueryDefinitionMap() {
        return queryDefinitionMap;
    }

    public List<GqlQueryDefinition> getQueryDefinition(String operationName) {
        List<GqlQueryDefinition> gqlQueryDefinitions = new ArrayList<>();
        if (Strings.isNullOrEmpty(operationName)) {
            operationName = getOperationDefinitionCoreValue(operationName);

            gqlQueryDefinitions.add(getQueryDefinitionMap().get(operationName));
        } else {
            gqlQueryDefinitions.addAll(getQueryDefinitionMap().values());
        }
        return gqlQueryDefinitions;
    }

    /*public String generateJsonFromGraphql() {
        JSONObject jsonObject  =new JSONObject();
        //JSONArray jsonArray = new JSONArray();

        this.getQueryDefinitionMap().forEach((key, value) -> {
            jsonObject.put(key,value.generateAsJsonObject());
        });

        return jsonObject.toString();
    }*/


    public List<String> getValueCanonicalFields(String operationName) {
        if (Strings.isNullOrEmpty(operationName)) {
            operationName = getOperationDefinitionCoreValue(operationName);
        }

        List<GqlQueryDefinition> gqlQueryDefinition = getQueryDefinition(operationName);
        List<String> paths = new ArrayList<>();
        for (GqlQueryDefinition definition : gqlQueryDefinition) {
            if (definition == null || definition.getGqlTree() == null) {
                continue;
            }
            List<TreeNodePath> treeNodePaths = definition.getGqlTree().extractedFieldLeafs();
            for (TreeNodePath treeNodePath : treeNodePaths) {
                paths.add(treeNodePath.getCanonicalPath());
            }
        }

        return paths;
    }

    private void handleSelectionRoot(OperationDefinition operationDefinition, GqlTreeNode treeNode) {
        for (Selection selection : operationDefinition.getSelectionSet().getSelections()) {
            LOGGER.debug("invoke handleSelection for operationDefinition : {}", operationDefinition);
            handleSelection(operationDefinition, treeNode, selection, null);
        }
    }

    /*
    query -> hero -> fld -> direct attribute
                         -> ref fragment ...
                                --> copy ref frag attribute
                                   --> frag  fld ?
                                           -> copy under
                                   --> copy as direct attribute
     */
    private void handleSelection(OperationDefinition operationDefinition, GqlTreeNode parentNode, Selection selection,
                                 Boolean isMutation) {
        if (selection instanceof graphql.language.Field) {
            graphql.language.Field gqlField = (graphql.language.Field) selection;

            // selection
            GqlTreeNode nodeToAdd = new GqlTreeNode();

            boolean isParent = false;

            SelectionSet selectionSet = ((graphql.language.Field) selection).getSelectionSet();
            if (selectionSet != null && selectionSet.getSelections().size() > 0) {
                isParent = true;
            }

            FieldNameDescriptor fieldNameDescriptor = new FieldNameDescriptor(gqlField.getName(),
                    gqlField.getAlias(),
                    ParserConsts.FIELD);
            nodeToAdd.setFieldNameDescriptor(fieldNameDescriptor);

            LOGGER.debug("Add field node {} to tree",fieldNameDescriptor );

            List<Directive> directives = gqlField.getDirectives();
            if (directives != null && directives.size() > 0) {
                LOGGER.debug("field has directives");
                nodeToAdd.setGqlDirectivesContainer(new GqlDirectivesContainer(directives));
                nodeToAdd.getAdditionalData().put(ParserConsts.IS_WITH_CONDITION, true);
            }

            if (gqlField.getArguments() != null && gqlField.getArguments().size() > 0) {
                LOGGER.debug("field has arguments");
                nodeToAdd.setGqlArgumentsContainer(new GqlArgumentsContainer(gqlField.getArguments()));
            }
            GqlTreeNode fieldNode = parentNode.addChildNode(nodeToAdd);

            if (isParent) {
                for (Selection selectionChild : selectionSet.getSelections()) {
                    LOGGER.debug("field has internal structure to add, invoke handleSelection for fieldNode {} ", fieldNode.getFieldNameDescriptor());
                    handleSelection(operationDefinition, fieldNode, selectionChild,
                            operationDefinition.getOperation().toString().equals("MUTATION"));
                }
            }
        } else if (selection instanceof graphql.language.InlineFragment) {
            graphql.language.InlineFragment inlineFragment = (InlineFragment) selection;

            GqlTreeNode nodeToAdd = new GqlTreeNode();

            FieldNameDescriptor fieldNameDescriptor = new
                    FieldNameDescriptor(inlineFragment.getTypeCondition().getName(),
                    "", InlineFragment.class);
            nodeToAdd.setFieldNameDescriptor(fieldNameDescriptor);

            LOGGER.debug("Add inline fragment node {} to tree",fieldNameDescriptor );


            nodeToAdd.getAdditionalData().put(ParserConsts.IS_INLINE_FRAGMENT, true);
            List<Directive> directives = inlineFragment.getDirectives();
            if (directives != null && directives.size() > 0) {
                LOGGER.debug("inline fragment has directives");
                nodeToAdd.setGqlDirectivesContainer(new GqlDirectivesContainer(directives));
                nodeToAdd.getAdditionalData().put(ParserConsts.IS_WITH_CONDITION, true);
            }

            GqlTreeNode inlineFragmentNode = parentNode.addChildNode(nodeToAdd);

            SelectionSet selectionSet = inlineFragment.getSelectionSet();

            if (selectionSet != null) {
                for (Selection selectionChild : selectionSet.getSelections()) {
                    LOGGER.debug("inline fragment has internal structure to add, invoke handleSelection for fieldNode {} ", fieldNameDescriptor);

                    handleSelection(operationDefinition, inlineFragmentNode, selectionChild, null);
                }
            }


        } else if (selection instanceof graphql.language.FragmentSpread) {
            graphql.language.FragmentSpread fragmentSpread = (FragmentSpread) selection;
            GqlTree fragTree = fragmentParser.getFragmentsMap().get(fragmentSpread.getName());
            GqlTreeNode gqlTreeNode = fragTree.getGqlTreeNode();
            LOGGER.debug("Selection is a FragmentSpread : {}, add it to tree node", fragmentSpread.getName());
            addChildSubRefTree(parentNode, gqlTreeNode);
        }
    }

    private void addChildSubRefTree(GqlTreeNode targetNode, GqlTreeNode sourceSubTreeToAdd) {
        if (sourceSubTreeToAdd != null) {

            boolean isSkip = false;
            //GqlTreeNode workingNode = /*(GqlTreeNode) SerializationUtils.clone(*/targetNode;//);
            Map<String, Object> additionalData = sourceSubTreeToAdd.getAdditionalData();
            if (additionalData != null) {
                Object val = additionalData.get(ParserConsts.IS_FRAGMENT_DEFINITION);
                if (val != null) {
                    if (Boolean.parseBoolean(val.toString())) {
                        isSkip = true;
                    }
                }
            }

            if (!isSkip) {
                GqlTreeNode copyNode = new GqlTreeNode();
                copyNode.shallowCopy(sourceSubTreeToAdd);

                targetNode = targetNode.addChildNode(copyNode);
            }

            List<Gqlable> childNodes = sourceSubTreeToAdd.getChildNodes();
            if (childNodes != null) {
                for (Gqlable childNode : childNodes) {
                    addChildSubRefTree(targetNode, (GqlTreeNode) childNode);
                }
            }

        }
    }

    private GqlQueryDefinition handleOperationDefinition(Definition definition) {

        OperationDefinition operationDefinition = (OperationDefinition) definition;
        LOGGER.debug("handle operation definition named : {}", operationDefinition.getName());

        OperationDefinition.Operation operation = operationDefinition.getOperation();

        GqlQueryDefinition gqlQueryDefinition = new GqlQueryDefinition();

        // root node  -- QUERY
        FieldNameDescriptor operationTypeDescriptor = new FieldNameDescriptor(operation.name(), null,
                ParserConsts.OPERATION_TYPE);

        GqlTreeNode rootNode = gqlQueryDefinition.getGqlTree().addRootNode(operationTypeDescriptor);

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Add operationTupeNode {} to the rootNode", operationTypeDescriptor);

        // sub-root node -- getAppDetail
        String operationDefinitionName = getOperationDefinitionName(operationDefinition);
        gqlQueryDefinition.setDefinitionName(operationDefinitionName);
        FieldNameDescriptor operationNameFieldNameDescriptor = new FieldNameDescriptor(operationDefinitionName,
                "",
                "methodName");

        LOGGER.debug("Create the operation node");
        GqlTreeNode operationNode = new GqlTreeNode();
        operationNode.setFieldNameDescriptor(operationNameFieldNameDescriptor);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Add methodName {} to the rootNode", operationNameFieldNameDescriptor);

        List<VariableDefinition> variableDefinitions = operationDefinition.getVariableDefinitions();
        if (variableDefinitions != null && variableDefinitions.size() > 0) {
            LOGGER.debug("set variables");
            operationNode.setGqlVariableDefinitionsContainer(variableDefinitions);
        }
        List<Directive> directives = operationDefinition.getDirectives();
        if (directives != null && directives.size() > 0) {
            LOGGER.debug("set directives");
            operationNode.setGqlDirectivesContainer(new GqlDirectivesContainer(directives));
        }

        GqlTreeNode operationDefinitionNode = rootNode.addChildNode(operationNode);

        handleSelectionRoot(operationDefinition, operationDefinitionNode);

        return gqlQueryDefinition;
    }
}
