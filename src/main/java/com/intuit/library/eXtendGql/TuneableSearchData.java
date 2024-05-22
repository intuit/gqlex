package com.intuit.library.eXtendGql;

import com.intuit.library.common.DocumentElementType;
import graphql.com.google.common.base.Preconditions;

import java.util.List;

public class TuneableSearchData {

    private boolean isContainsDirectives = false;
    private boolean isContainsArgument = false;

    private boolean isContainsVariables = false;

    private boolean isContainsFragment = false;

    private final List<String> searchElements;

    public TuneableSearchData(String searchSyntax) {

        Preconditions.checkNotNull(searchSyntax);

        searchElements = List.of(searchSyntax.split("\\/"));

        init();
    }

    public TuneableSearchData(List<String> pathsToSearch) {
        Preconditions.checkNotNull(pathsToSearch);
        this.searchElements = pathsToSearch;
        init();
    }

    private void init() {

        for (String searchElement : searchElements) {
            if (searchElement.contains("type=" + DocumentElementType.DIRECTIVE.getShortName())) {
                isContainsDirectives = true;
            }

            if (searchElement.contains("type=" + DocumentElementType.ARGUMENT.getShortName())) {
                isContainsArgument = true;
            }

            if (searchElement.contains("type=" + DocumentElementType.VARIABLE_DEFINITION.getShortName())) {
                isContainsVariables = true;
            }

            if (searchElement.contains("type=" + DocumentElementType.FRAGMENT_DEFINITION.getShortName()) ||
                    searchElement.contains("type=" + DocumentElementType.INLINE_FRAGMENT.getShortName()) ||
                    searchElement.contains("type=" + DocumentElementType.FRAGMENT_SPREAD.getShortName())) {
                isContainsFragment = true;
            }
        }
    }

    public boolean isContainsDirectives() {
        return isContainsDirectives;
    }

    public boolean isContainsArgument() {
        return isContainsArgument;
    }

    public boolean isContainsVariables() {
        return isContainsVariables;
    }

    public boolean isContainsFragment() {
        return isContainsFragment;
    }
}
