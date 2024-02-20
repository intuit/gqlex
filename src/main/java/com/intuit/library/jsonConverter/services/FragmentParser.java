package com.intuit.library.jsonConverter.services;

import com.intuit.library.jsonConverter.model.tree.GqlTree;
import graphql.language.Definition;

import java.util.List;
import java.util.Map;

public interface FragmentParser {
    // For each definition name -> list of: fragment name with list of fields
    Map<String, GqlTree> getFragmentsMap();

    void parseDefinitionsForFragments(List<Definition> definitions);
}
