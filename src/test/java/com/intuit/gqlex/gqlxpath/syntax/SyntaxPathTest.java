package com.intuit.gqlex.gqlxpath.syntax;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.gqlxpath.selector.GqlSelectionSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SyntaxPathTest {

    @Test
    void getPathElements() {
        SyntaxPath syntaxPath = new SyntaxPath();
        syntaxPath.appendSyntaxPathElement(new SyntaxPathElement("query", DocumentElementType.OPERATION_DEFINITION));
        syntaxPath.appendFieldNameElement("b");
        syntaxPath.appendFieldNameElement("c");

        assertNotNull(syntaxPath.getPathElements());
    }

    @Test
    void getPathElementsWithAny() {
        SyntaxPath syntaxPath = new SyntaxPath();
        syntaxPath.appendSyntaxPathElement(new SyntaxPathElement("query", DocumentElementType.OPERATION_DEFINITION));
        syntaxPath.appendFieldNameElement("b");
        syntaxPath.appendAnyFieldElement();
        syntaxPath.appendFieldNameElement("c");

        assertNotNull(syntaxPath.getPathElements());
        assertEquals("/query/b/.../c", syntaxPath.toString());
    }

    @Test
    void check_exception_throw_illegal_name() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SyntaxPath syntaxPath = new SyntaxPath();
            syntaxPath.appendSyntaxPathElement(new SyntaxPathElement("query", DocumentElementType.OPERATION_DEFINITION));
            syntaxPath.appendFieldNameElement("b");
            syntaxPath.appendFieldNameElement("b:o");
        });
    }

    @Test
    void check_exception_throw_illegal_name_question_mark() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SyntaxPath syntaxPath = new SyntaxPath();
            syntaxPath.appendSyntaxPathElement(new SyntaxPathElement("query", DocumentElementType.OPERATION_DEFINITION));
            syntaxPath.appendFieldNameElement("b?");
            syntaxPath.appendFieldNameElement("bo");
        });
    }

    @Test
    void check_exception_throw_illegal_no_start_elem() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SyntaxPath syntaxPath = new SyntaxPath();
            syntaxPath.appendSyntaxPathElement(new SyntaxPathElement("query", DocumentElementType.FIELD));
            syntaxPath.appendFieldNameElement("b");
            syntaxPath.appendFieldNameElement("bo");
        });
    }
}