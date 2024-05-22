package com.intuit.gqlex.gxpath.selector;

import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.TuneableSearchData;
import com.intuit.gqlex.gxpath.syntax.SyntaxPath;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*import java.util.regex.Matcher;
import java.util.regex.Pattern;*/

public class SearchPathBuilder {

    private boolean isSelectByRange = false;
    private boolean isMultiSelect = false;

    public boolean isSelectByRange() {
        return isSelectByRange;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public SelectionRange getSelectionRange() {
        return selectionRange;
    }

    private SelectionRange selectionRange;

    private TuneableSearchData tuneableSearchData = null;
    private final List<String> pathElementsToSearch;
    private SearchPathElementList pathElements = null;

    private boolean isAnySet = false;

    public boolean isAnySet() {
        return isAnySet;
    }

    public SearchPathBuilder(String searchSyntax) {
        Preconditions.checkNotNull(searchSyntax);

        if( searchSyntax.isEmpty()){
            throw new GqlSelectionSyntaxException("gXPath must start with // ");
        }

        if( searchSyntax.length() <= 0){
            throw new GqlSelectionSyntaxException("gXPath must start with // ");
        }

        if(     ! searchSyntax.startsWith(SearchElementConstants.DOUBLE_SLASH) &&
                ! searchSyntax.startsWith(SearchElementConstants.SINGLE_SLASH)  &&
                !searchSyntax.startsWith("{") ){

            throw new GqlSelectionSyntaxException("gXPath must start with // or /, gXPath Range can be set  before // or /.");
        }

        if( searchSyntax.startsWith("{")) {
            selectionRange = new SelectionRange();
            int indEndRange = searchSyntax.indexOf("}");
            if (indEndRange == -1) {
                throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:y}");
            }
            String rangeString = searchSyntax.substring(searchSyntax.indexOf("{") + 1, indEndRange);
            String[] rangeSplit = rangeString.split(":");

            String startRange = "";
            String endRange = "";
            if (rangeSplit.length == 1) {
                String rangeValue = rangeSplit[0];
                int indexSemiColon = rangeString.indexOf(":");
                if( indexSemiColon == -1){
                    throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:y}");
                }
                int indexValue = rangeString.indexOf(rangeValue);
                if (indexSemiColon > indexValue) {
                    startRange = rangeSplit[0];
                } else {
                    endRange = rangeSplit[0];
                }
            } else if (rangeSplit.length == 2) {
                startRange = rangeSplit[0];
                endRange = rangeSplit[1];
            } else if (rangeSplit.length > 2) {
                {
                    throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:y}");
                }
            }
            selectionRange = new SelectionRange();
            isSelectByRange = true;
            if (Strings.isNullOrEmpty(startRange)) {
                startRange = "0";
            }
            int rangeStart = 0;
            try {
                rangeStart = Integer.parseInt(startRange);
            }catch (Exception ex){
                throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:y}, x and y must be integer");
            }
            if (rangeStart < 0) {
                throw new GqlSelectionSyntaxException("gXPath Range pattern should be {rangeStart:y}, startRange must be equal or greater to zero");
            }

            selectionRange.setRangeStart(rangeStart);

            if (Strings.isNullOrEmpty(endRange)) {
                selectionRange.setRangeEndAll(true);
            }else{
                int rangeEnd = 0;
                try {
                    rangeEnd = Integer.parseInt(endRange);
                }catch (Exception ex){
                    throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:y}, x and y must be integer");
                }

                if (rangeEnd < 0) {
                    throw new GqlSelectionSyntaxException("gXPath Range pattern should be {x:rangeEnd}, rangeEnd must be equal or greater to zero");
                }
                selectionRange.setRangeEnd(rangeEnd);
            }

            if( selectionRange.getRangeStart() > selectionRange.getRangeEnd()){
                throw new GqlSelectionSyntaxException("gXPath Range start value must be less or equal to end value {x:y}");
            }

            searchSyntax = searchSyntax.substring(indEndRange + 1);
        }

        // remove the first 2 slashs '//'
        if( searchSyntax.startsWith(SearchElementConstants.DOUBLE_SLASH)) {
            searchSyntax = searchSyntax.substring(searchSyntax.indexOf(SearchElementConstants.DOUBLE_SLASH) + 2);
            isMultiSelect = true;
        } else if( searchSyntax.startsWith(SearchElementConstants.SINGLE_SLASH)) {
            if (selectionRange != null) {
                throw new GqlSelectionSyntaxException("gXPath Range cannot be set with /.");
            }
            searchSyntax = searchSyntax.substring(searchSyntax.indexOf(SearchElementConstants.SINGLE_SLASH)+1);
            isMultiSelect = false;
        }

        pathElementsToSearch = List.of(searchSyntax.split("\\/"));

        tuneableSearchData = new TuneableSearchData(pathElementsToSearch);

        int length = pathElementsToSearch.size();
        Preconditions.checkArgument(length != 0);

        boolean isContainsFieldName = false;
        int i = 0;
        if (length >= 1) {

           // List<String> reduceArray = new ArrayList<>();
            for (String element : pathElementsToSearch) {
                String pathElementValue = element.trim();

                if( Strings.isNullOrEmpty(pathElementValue) ){
                    throw new GqlSelectionSyntaxException("gXPath contains empty names.");

                }

                if (i <= length - 1) {

                    boolean containsAny = pathElementValue.contains(SearchElementConstants.ANY_SEARCH_VALUE);
                    if( containsAny) {
                        if (i < length - 1) {
                            continue;
                        } else {
                            throw new GqlSelectionSyntaxException("Last gXPath element cannot contains of ... [any]");
                        }
                    }

                    final Pattern pattern = Pattern.compile(SyntaxPath.REGEX_ALLOWED_CHAR_SEARCH_PATH_ELEMENT, Pattern.MULTILINE);
                    final Matcher matcher = pattern.matcher(pathElementValue);
                    isContainsFieldName = true;
                    if (!matcher.find()) {
                        throw new GqlSelectionSyntaxException("gXPath can contains only string (numbers, letters and -, =,_ , ... )");
                    }

                  //  reduceArray.add(element);
                    i++;
                }
            }
        }

        if( ! isContainsFieldName){
            throw new GqlSelectionSyntaxException("gXPath contains only any [...], please set valid path with targeted node name.");
        }

        buildStrategy();
    }

    public TuneableSearchData getTuneableSearchData() {
        return tuneableSearchData;
    }

    //private String searchPhrase;

    /*public List<String> getPathElementsToSearch() {
        return pathElementsToSearch;
    }

    public void addElementByOrder(DocumentElementType documentElementType, Map<String, String> attributes) {
        addElementByOrder(new SearchPathElement(documentElementType, attributes));
    }*/

    public void addElementByOrder(Map<String, String> attributes) {
        addElementByOrder(new SearchPathElement(attributes));
    }

    public void addElementByOrder(SearchPathElement searchPathElement) {
        Preconditions.checkArgument(searchPathElement != null && searchPathElement.getSelectionAttributeMap() != null);
        if (pathElements == null) {
            pathElements = new SearchPathElementList();
        }
        pathElements.add(searchPathElement);
    }

    public SearchPathElementList getPathElements() {
        return pathElements;
    }

    private void buildStrategy() {

        ListIterator<String> stringListIterator = pathElementsToSearch.listIterator();

        boolean isAnySelected = false;
        while (stringListIterator.hasNext()) {

            String elem = stringListIterator.next();
            elem = elem.trim();

            Map<String, String> attributes = new HashMap<>();

            if( elem.equals(SearchElementConstants.ANY_SEARCH_VALUE)){
                if( isAnySelected ){
                    continue;
                }
                attributes.put(SearchPathElement.ANY_VALUE, Boolean.TRUE.toString());
                isAnySelected = true;
            }else {
                isAnySelected = false;
                boolean isTypeSet = false;
                if (elem.contains(SearchElementConstants.FIELD_LABELS_START_SIGN) && elem.contains(SearchElementConstants.FIELD_LABELS_END_SIGN)) {

                    int indexOfLeftRect = elem.indexOf(SearchElementConstants.FIELD_LABELS_START_SIGN);
                    int indexOfRightRect = elem.indexOf(SearchElementConstants.FIELD_LABELS_END_SIGN);
                    String nameValueLabelsStr = elem.substring(indexOfLeftRect + 1, indexOfRightRect);

                    String[] labels = nameValueLabelsStr.split(" ");

                    elem = elem.substring(0, indexOfLeftRect);

                    for (String label : labels) {
                        int beginIndex = label.indexOf(SearchElementConstants.LABEL_COMPARER);
                        if (beginIndex != -1) {
                            String key = label.substring(0, beginIndex);
                            String value = label.substring(beginIndex + 1);

                            if (key.equalsIgnoreCase(SearchElementConstants.TYPE)) {
                                isTypeSet = true;
                                attributes.put(SearchPathElement.DOC_ELEM_TYPE,
                                        DocumentElementType.getByShortName(value).getShortName());
                            } else {
                                if ((elem.equalsIgnoreCase("query") || elem.equalsIgnoreCase("mutation")) &&
                                        key.equalsIgnoreCase("name")) {
                                    attributes.put(SearchPathElement.OPERATION_NAME, value);
                                } else {
                                    attributes.put(key, value);
                                }
                            }
                        } else {
                            throw new IllegalArgumentException(MessageFormat.format("{0} contains illegal phrase, label is: {1}", elem, label));
                        }
                    }


                }

                if (!isTypeSet) {
                    if (elem.equalsIgnoreCase(DocumentElementType.OPERATION_DEFINITION.getShortName())) { //TODO -  FIX ME

                        attributes.put(SearchPathElement.DOC_ELEM_TYPE,
                                DocumentElementType.OPERATION_DEFINITION.getShortName());
                        //attributes.put(SearchPathElement.OPERATION_NAME, elem);

                    } else if (elem.equalsIgnoreCase(DocumentElementType.MUTATION_DEFINITION.getShortName())) {

                        attributes.put(SearchPathElement.DOC_ELEM_TYPE,
                                DocumentElementType.MUTATION_DEFINITION.getShortName());

                        // attributes.put(SearchPathElement.NAME, elem);

                    } else {
                        attributes.put(SearchPathElement.DOC_ELEM_TYPE,
                                DocumentElementType.FIELD.getShortName());
                    }
                }

                attributes.put(SearchPathElement.NAME, elem);
            }
            addElementByOrder(attributes);
        }
    }
}
