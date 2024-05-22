package com.intuit.gqlex.gxpath.selector;

import java.text.MessageFormat;
import java.util.LinkedList;

public class SearchPathElementList extends LinkedList<SearchPathElement> {

    @Override
    public String toString() {

        return toString(0);
    }


    public String toString(int tabNumber) {
        if( tabNumber<0 || tabNumber>10) tabNumber = 0;

        StringBuilder stringBuilder = new StringBuilder();
        int i=1;
        String tab = "";
        for (int j = 0; j < tabNumber; j++) {
            tab+="\t";
        }
        for (SearchPathElement searchPathElement : this) {
            stringBuilder.append(MessageFormat.format("{0}{1}: {2}\n", tab,i, searchPathElement));
            i++;
        }
        return stringBuilder.toString();
    }
}
