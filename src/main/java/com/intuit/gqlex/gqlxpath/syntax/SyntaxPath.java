package com.intuit.gqlex.gqlxpath.syntax;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.gqlxpath.selector.GqlSelectionSyntaxException;
import com.intuit.gqlex.gqlxpath.selector.SelectionRange;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.com.google.common.collect.ImmutableList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxPath {
    public static final String REGEX_ALLOWED_CHAR_SEARCH_PATH_ELEMENT = "^[A-Za-z][\\[\\]\\=_A-Za-z0-9]*$";

    private SelectionRange selectionRange;

    public SelectionRange getSelectionRange() {
        return selectionRange;
    }

    public void setSelectionRange(SelectionRange selectionRange) {
        this.selectionRange = selectionRange;
    }

    private List<SyntaxPathElement> pathElements;

    public List<SyntaxPathElement> getPathElements() {
        return pathElements;
    }

    public boolean isValid(){
        if(pathElements == null ){
            return false;
        }

        if(pathElements.size() == 0){
            return false;
        }

        for (SyntaxPathElement pathElement : pathElements) {
            if( ! pathElement.isValid() ){
                return false;
            }
        }

        return  true;
    }
    public SyntaxPath() {
    }

    //private boolean isStartElemAlreadyAppend  =false;

    public ImmutableList<SyntaxPathElement> appendFieldNameElement(String name){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        if (pathElements == null) pathElements = new ArrayList<>();

        final Pattern pattern = Pattern.compile(REGEX_ALLOWED_CHAR_SEARCH_PATH_ELEMENT, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(name);

        if (!matcher.find()) {
            throw new GqlSelectionSyntaxException("Search path values can contains only string (numbers, letters and -, =,_)");
        }

        appendPathElement(new SyntaxPathElement(name, DocumentElementType.FIELD));

        return ImmutableList.copyOf(pathElements);
    }

    public ImmutableList<SyntaxPathElement> appendAnyFieldElement(){

        if (pathElements == null) pathElements = new ArrayList<>();

        SyntaxPathElement syntaxPathElement = new SyntaxPathElement();
        syntaxPathElement.setAnyField(true);
        appendPathElement(syntaxPathElement);

        return ImmutableList.copyOf(pathElements);
    }

    public ImmutableList<SyntaxPathElement> appendSyntaxPathElement(SyntaxPathElement syntaxPathElement) {
        Preconditions.checkNotNull(syntaxPathElement);

        /*if( syntaxPathElement.isStartElem() ){
            if( isStartElemAlreadyAppend ){
                throw new IllegalArgumentException( "Only one syntax path element point to start an path <query> or <mutation> is allowed.");
            }else{
                isStartElemAlreadyAppend = true;
            }
        }*/

        if (pathElements == null) pathElements = new ArrayList<>();

        appendPathElement(syntaxPathElement);

        return ImmutableList.copyOf(pathElements);
    }

    private void appendPathElement(SyntaxPathElement syntaxPathElement) {

        Preconditions.checkArgument(syntaxPathElement!= null && syntaxPathElement.isValid());

        if( pathElements.isEmpty()){
           if( !syntaxPathElement.isStartElem()){
               throw new GqlSelectionSyntaxException("Prefix element must be query or mutation //");
           }
        }

        pathElements.add(syntaxPathElement);
    }



    @Override
    public String toString() {
        if (pathElements == null || pathElements.size() == 0) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if( selectionRange != null){
            if( ! selectionRange.isRangeEndAll()) {
                stringBuilder.append(MessageFormat.format("'{'{0}:{1}'}'", selectionRange.getRangeStart(), selectionRange.getRangeEnd()));
            }else{
                stringBuilder.append(MessageFormat.format("'{'{0}:'}'", selectionRange.getRangeStart()));
            }
        }

        int i=0;
        for (SyntaxPathElement pathElement : pathElements) {
            stringBuilder.append(pathElement.toString());
            if( i+1< pathElements.size()) {
                stringBuilder.append("/");
            }

            i++;
        }
        return stringBuilder.toString();
    }
}
