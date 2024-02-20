/*package com.intuit.library.eXtendGql.selector;

import graphql.com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class SelectionDebugData {
    List<String> msgs = null;

    public void addMessage(String msg) {
        if (Strings.isNullOrEmpty(msg)) {
            return;
        }

        if (msgs == null) {
            msgs = new ArrayList<>();
        }

        msgs.add(msg);

    }

    public List<String> getMsgs() {
        return msgs;
    }

    @Override
    public String toString() {

        if( msgs == null ){
            return "no debug data";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (String msg : msgs) {
            builder.append(msg + "\n");
        }
        return builder.toString();
    }
}*/
