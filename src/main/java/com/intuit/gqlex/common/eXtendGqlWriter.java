package com.intuit.gqlex.common;

import graphql.com.google.common.base.Preconditions;
import graphql.language.AstPrinter;
import graphql.language.Node;

import java.io.Writer;

public class eXtendGqlWriter {

    public static void writeToStream(Writer writer, Node node){
        Preconditions.checkArgument(node != null);
        Preconditions.checkArgument(writer != null);
        AstPrinter.printAst(writer, node);
    }

    public static String writeToString(Node node) {
        Preconditions.checkArgument(node != null);
        return AstPrinter.printAst( node);
    }
}
