package com.intuit.gqlex.gqlxpath.syntax;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.gqlxpath.selector.SelectionRange;
import graphql.com.google.common.base.Preconditions;

import java.util.Map;

public class SyntaxBuilder {

    private SyntaxPath eXtendGqlSyntaxPath;

    private boolean isStartElemDone = false;
    public SyntaxBuilder appendQuery()  {
        return appendQuery(false);
    }
    public SyntaxBuilder appendQuery(boolean isMultiSelect)  {
        if( isStartElemDone ){
            throw new RuntimeException("Start Path element already been set");
        }
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(DocumentElementType.OPERATION_DEFINITION.getShortName(),
                DocumentElementType.OPERATION_DEFINITION, isMultiSelect);
        isStartElemDone = true;
        addTogXPath(syntaxPathElement);
        return this;
    }

    private void addTogXPath(SyntaxPathElement syntaxPathElement) {
        initgXPath();
        eXtendGqlSyntaxPath.appendSyntaxPathElement(syntaxPathElement);
    }

    public SyntaxBuilder appendRangeByStart(int start){
        Preconditions.checkArgument(start >=0);
        initgXPath();
        SelectionRange selectionRange = eXtendGqlSyntaxPath.getSelectionRange();

        if( selectionRange == null ){
            selectionRange = new SelectionRange();
            eXtendGqlSyntaxPath.setSelectionRange(selectionRange);
        }

        selectionRange.setRangeStart(start);
        selectionRange.setRangeEndAll(true);

        return this;
    }
    public SyntaxBuilder appendRangeByEnd(int end){

        Preconditions.checkArgument(end >=0);
        initgXPath();
        SelectionRange selectionRange = eXtendGqlSyntaxPath.getSelectionRange();

        if( selectionRange == null ){
            selectionRange = new SelectionRange();
            eXtendGqlSyntaxPath.setSelectionRange(selectionRange);
        }

        selectionRange.setRangeStart(0);
        selectionRange.setRangeEnd(end);

        return this;
    }

    public SyntaxBuilder appendRange(int start, int end){
        Preconditions.checkArgument(start >=0);
        Preconditions.checkArgument(end >=0);
        Preconditions.checkArgument(start<=end);

        initgXPath();
        SelectionRange selectionRange = eXtendGqlSyntaxPath.getSelectionRange();

        if( selectionRange == null ){
            selectionRange = new SelectionRange();
            eXtendGqlSyntaxPath.setSelectionRange(selectionRange);
        }

        selectionRange.setRangeStart(start);
        selectionRange.setRangeEnd(end);

        return this;
    }

    private void initgXPath() {
        if( eXtendGqlSyntaxPath == null ){
            eXtendGqlSyntaxPath = new SyntaxPath();
        }
    }
    public SyntaxBuilder appendMutation()  {
        return appendMutation(false);
    }
    public SyntaxBuilder appendMutation(boolean isMultiSelect)  {
        if( isStartElemDone ){
            throw new RuntimeException("Start Path element already been set");
        }
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(DocumentElementType.MUTATION_DEFINITION.getShortName(),
                DocumentElementType.MUTATION_DEFINITION,isMultiSelect);
        isStartElemDone = true;
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendField(String name){
        Preconditions.checkNotNull(name);
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.FIELD);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendField(String name, Map<String, String> attributes){
        Preconditions.checkNotNull(name);
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.FIELD);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }


    public SyntaxBuilder append(SyntaxPathElement syntaxPathElement){
        Preconditions.checkNotNull(syntaxPathElement);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendField(String name, String attributeName, String attributeValue){
        Preconditions.checkNotNull(name);
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.FIELD);
        syntaxPathElement.addAttribute(attributeName,attributeValue);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendDirective(String name, Map<String, String> attributes){
        Preconditions.checkNotNull(name);
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.DIRECTIVE);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendArgument(String name, Map<String, String> attributes){
        Preconditions.checkNotNull(name);
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.ARGUMENT);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendFragmentSpread(String name, Map<String, String> attributes){
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.FRAGMENT_SPREAD);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendFragment(String name, Map<String, String> attributes){
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.FRAGMENT_DEFINITION);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxBuilder appendInlineFragment(String name, Map<String, String> attributes){
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement(name, DocumentElementType.INLINE_FRAGMENT);
        syntaxPathElement.addAttributes(attributes);
        addTogXPath(syntaxPathElement);
        return this;
    }

    public SyntaxPath build(){
        return eXtendGqlSyntaxPath;
    }
}
