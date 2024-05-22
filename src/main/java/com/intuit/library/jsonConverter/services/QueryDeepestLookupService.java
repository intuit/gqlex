package com.intuit.library.jsonConverter.services;

import graphql.language.Document;

public interface QueryDeepestLookupService {
    public int checkDepthLimit(Document document);
}
