package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.FieldNameDescriptor;
import com.intuit.library.jsonConverter.model.GqlDirectivesContainer;
import com.intuit.library.jsonConverter.model.tree.GqlTree;
import com.intuit.library.jsonConverter.model.tree.GqlTreeNode;
import graphql.com.google.common.base.Strings;
import graphql.language.*;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FragmentParserImpl implements FragmentParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentParserImpl.class);
    // fragment name --> list of fragment names it referes which still not resolved
    private Map<String, List<String>> waitedFragmentToResolveForRefFragmentMap = null;


    // definition  name -> < fragment name, list of fields>
    private  Map<String, GqlTree> fragmentsMap = new HashMap<>();

    private Map<String, FragmentDefinition> fragmentDefinitionMap = new HashMap<>();



    // for each definition name -> list of: fragment name with list of fields
    @Override
    public Map<String, GqlTree> getFragmentsMap() {
        return fragmentsMap;
    }

    @Override
    public void parseDefinitionsForFragments(List<Definition> definitions) {
        if (definitions == null) {
            throw new IllegalArgumentException("definitions is null");
        }

        if (definitions.size() == 0) {
            LOGGER.debug("no fragment definitions to parse");
            return;
        }

        for (Definition definition : definitions) {
            if (definition instanceof FragmentDefinition) {
                FragmentDefinition fragmentDefinition = (FragmentDefinition)definition;
                fragmentDefinitionMap.put(fragmentDefinition.getName(), fragmentDefinition);
            }
        }

        for (FragmentDefinition fragmentDefinition : fragmentDefinitionMap.values()) {
            extractFragmentsFromDefinition(fragmentDefinition);
        }
    }

    private void extractFragmentsFromDefinition(FragmentDefinition fragmentDefinition) {

        //FrgName -> fields
        //        --> other FRG

        String fragmentName = fragmentDefinition.getName();
        LOGGER.debug("Extract data from fragment definition : {}", fragmentName);
        //String fragmentTypeName = fragmentDefinition.getTypeCondition().getName();

        GqlTree gqlTree = new GqlTree();
        GqlTreeNode gqlTreeNode = gqlTree.getGqlTreeNode();
        List<Directive> directives = fragmentDefinition.getDirectives();
        if( directives != null && directives.size() > 0) {
            GqlDirectivesContainer gqlDirectivesContainer = new GqlDirectivesContainer(directives);
            gqlTreeNode.setGqlDirectivesContainer(gqlDirectivesContainer);
            gqlTreeNode.getAdditionalData().put(ParserConsts.IS_WITH_CONDITION, true);
        }

        gqlTreeNode.setFieldNameDescriptor(new FieldNameDescriptor(fragmentName,
                null,
                FragmentDefinition.class));

        gqlTreeNode.getAdditionalData().put(ParserConsts.IS_FRAGMENT_DEFINITION, true);
        fragmentsMap.put(fragmentDefinition.getName(), gqlTree);


        collectFragmentFieldsStructure(fragmentDefinition.getSelectionSet(),
                waitedFragmentToResolveForRefFragmentMap,
                fragmentName,
                gqlTree.getGqlTreeNode());
    }

    private void collectFragmentFieldsStructure(SelectionSet selectionSet,
                                                Map<String, List<String>> waitedFragmentToResolveForRefFragmentMap,
                                                String fragmentTypeName,
                                                GqlTreeNode parentNode) {

        if (selectionSet == null) {
            return;
        }

        LOGGER.debug("Calculate fragment, selectionSet : {}", selectionSet.toString());

        List<Selection> selections = selectionSet.getSelections();

        if (selections == null || selections.size() == 0) {
            LOGGER.debug("return, no selections");
            return;
        }

        for (Selection selection : selections) {

            if (Strings.isNullOrEmpty(fragmentTypeName)) {
                fragmentTypeName = extractName(selection);
                LOGGER.debug("Calculate the fragmentTypeName : {}", fragmentTypeName);
            }

            LOGGER.debug("Calculate of selection name : {}", fragmentTypeName);

            if (selection instanceof graphql.language.Field) {
                graphql.language.Field fieldInFrg = (graphql.language.Field) selection;

                GqlTreeNode nodeToAdd = new GqlTreeNode();
                FieldNameDescriptor fieldNameDescriptor = new FieldNameDescriptor(fieldInFrg.getName(), fieldInFrg.getAlias());
                nodeToAdd.setFieldNameDescriptor(fieldNameDescriptor);

                LOGGER.debug("Selection field name is: {}", fieldNameDescriptor.toString());

                List<Directive> directives = fieldInFrg.getDirectives();
                if( directives != null && directives.size() > 0) {
                    GqlDirectivesContainer gqlDirectivesContainer = new GqlDirectivesContainer(directives);
                    nodeToAdd.setGqlDirectivesContainer(gqlDirectivesContainer);

                    LOGGER.debug("Set directive in field");

                    nodeToAdd.getAdditionalData().put(ParserConsts.IS_WITH_CONDITION, true);
                }

                GqlTreeNode fieldNode = parentNode.addChildNode(nodeToAdd);

                SelectionSet selectionSetOfInternField = fieldInFrg.getSelectionSet();

                if (selectionSetOfInternField != null &&
                        selectionSetOfInternField.getSelections() != null &&
                        selectionSetOfInternField.getSelections().size() > 0) {

                    LOGGER.debug("invoke internal selections calculation");

                    collectFragmentFieldsStructure(selectionSetOfInternField,
                            waitedFragmentToResolveForRefFragmentMap,
                            null,
                            fieldNode);

                }

            } else if (selection instanceof FragmentSpread) {

                // this field is point to fragment
                FragmentSpread fragmentSpread = (FragmentSpread) selection;

                String fragmentSpreadName = fragmentSpread.getName();

                LOGGER.debug("Selection is FragmentSpread: {}", fragmentSpreadName);

                GqlTree refSubGqlTree = fragmentsMap.get(fragmentSpreadName);

                // already understand the fragment schema
                if (refSubGqlTree != null ) {
                    parentNode.addChildNode(refSubGqlTree.getGqlTreeNode());
                } else {

                    /// get the fragment spread name from , if not x
                    extractFragmentsFromDefinition(fragmentDefinitionMap.get(fragmentSpreadName));
                    GqlTree fragmentTreeData = fragmentsMap.get(fragmentSpreadName);
                    if( fragmentTreeData == null) {
                        throw  new NullArgumentException("fragment " + fragmentSpreadName + " cannot be inferred from the graphql data");
                    }
                    parentNode.addChildNode(fragmentTreeData.getGqlTreeNode());

                }
            }
        }
    }

    private String extractName(Selection selection) {
        if (selection instanceof FragmentSpread) {
            return ((FragmentSpread) selection).getName();
        } else if (selection instanceof graphql.language.Field) {
            return ((graphql.language.Field) selection).getName();
        } else
            return "";
    }
}
