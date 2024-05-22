package com.intuit.gqlex.gqlxpath.selector;

public class GqlSelectionSyntaxException extends RuntimeException {
    private final String message;

    public GqlSelectionSyntaxException(String message) {

        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
