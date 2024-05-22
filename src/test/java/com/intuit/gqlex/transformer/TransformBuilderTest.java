package com.intuit.gqlex.transformer;

import com.intuit.gqlex.gxpath.syntax.SyntaxBuilder;
import graphql.language.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformBuilderTest {

    @Test
    void getTransformCommands() {

        TransformBuilder transformBuilder = new TransformBuilder();
        SyntaxBuilder syntaxBuilder = provideSyntaxBuilderA();

        TransformBuilder transformBuilder1 = transformBuilder.updateNodeName(syntaxBuilder, "b_new_value")
                .addChildrenNode(syntaxBuilder, new Field("new_node"))
                .addSiblingNode(syntaxBuilder, new Field("new_node"))
                .removeNode(provideSyntaxBuilderB())
                .duplicateNode(provideSyntaxBuilderA())
                .duplicateNode(provideSyntaxBuilderA(), 3);


        assertNotNull(transformBuilder1);
    }


    private SyntaxBuilder provideSyntaxBuilderA(){
        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        try {
            eXtendGqlBuilder.appendQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");

        return eXtendGqlBuilder;
    }

    private SyntaxBuilder provideSyntaxBuilderB(){
        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        try {
            eXtendGqlBuilder.appendQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");

        return eXtendGqlBuilder;
    }

}