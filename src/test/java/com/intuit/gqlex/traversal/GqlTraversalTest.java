package com.intuit.gqlex.traversal;

import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class GqlTraversalTest {

    @Test
    void browse_multiple_queries() throws IOException {

        String fileName = "multiple_queries.txt";

        String p = runTestInternal(fileName);

        System.out.println(p);
        assertEquals("Node : Document ||  Type : DOCUMENT Name : QUERY ||  Type : OPERATION_DEFINITION Name : query || Alias : null ||  Type : FIELD Name : GetLoggedInUserName || Alias : null ||  Type : FIELD Name : me || Alias : null ||  Type : FIELD Name : name || Alias : null ||  Type : FIELD Name : export ||  Type : DIRECTIVE Name : as || Value : StringValue{value='search'} ||  Type : ARGUMENT Name : query || Alias : null ||  Type : FIELD Name : GetPostsContainingString || Alias : null ||  Type : FIELD Name : posts || Alias : null ||  Type : FIELD Name : id || Alias : null ||  Type : FIELD Name : title || Alias : null ||  Type : FIELD Name : filter || Value : ObjectValue{objectFields=[ObjectField{name='search', value=VariableReference{name='search'}}]} ||  Type : ARGUMENT Name : depends ||  Type : DIRECTIVEName : on || Value : StringValue{value='GetLoggedInUserName'} ||  Type : ARGUMENT",p);
    }

    @Test
    void browse_simple_with_args() throws IOException {

        String fileName = "simple_query_only_arg.txt";

        String p = runTestInternal(fileName);

        System.out.println(p);
        assertEquals("Node : Document ||  Type : DOCUMENT Name : QUERY ||  Type : OPERATION_DEFINITION Name : Instrument || Alias : null ||  Type : FIELDName : id || Value : StringValue{value='1234'} ||  Type : ARGUMENT",p);
    }

    @Test
    void browse_simple() throws IOException {

        String fileName = "simple_query.txt";

        String p = runTestInternal(fileName);

        System.out.println(p);
        assertEquals("Node : Document ||  Type : DOCUMENT Name : QUERY ||  Type : OPERATION_DEFINITION Name : Instrument || Alias : null ||  Type : FIELD Name : Reference || Alias : aliasme ||  Type : FIELD Name : Name || Alias : null ||  Type : FIELD Name : title || Alias : null ||  Type : FIELDName : id || Value : StringValue{value='1234'} ||  Type : ARGUMENT",p);
    }

    private String runTestInternal(String fileName) throws IOException {
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        URL resource = classLoader.getResource(testFolder + "/" + fileName);

        Preconditions.checkNotNull(resource);
        String resourceFile = resource.getFile();
        Preconditions.checkState(!Strings.isNullOrEmpty(resourceFile));
        File file = new File(resourceFile);

        GqlTraversal traversal = new GqlTraversal();

        StringBuilderObserver gqlStringBuilderObserver = new StringBuilderObserver();

        traversal.getGqlTraversalObservable().addObserver(gqlStringBuilderObserver);
        traversal.traverse(file);

        return gqlStringBuilderObserver.getGqlBrowsedString();//getGqlBrowsedPrintedString();//
    }
}

/*Document.QUERY
Document.QUERY.Instrument
Document.QUERY.Instrument.Reference
Document.QUERY.Instrument.Reference.Name
Document.QUERY.Instrument.Reference.Title*/
