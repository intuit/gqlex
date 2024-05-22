package com.intuit.library.eXtendGql.transformer;

import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratedComment {

    public static final String INTERNAL_GENERATED = "INTERNAL_GENERATED";
    public static final String OLD_NAME_KEY = "old_name_key";
    public static final String OLD_ALIAS_KEY = "old_alias_key";

    public static final String TRANSFORM_TRX_ID = "transform_trx_id";
    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";
    public static final String PATTERN = INTERNAL_GENERATED + "->[" + KEY + "={0}:" + VALUE + "={1}]";

    public static Node addComments(Node node, Map<String, String> keyValuemMap){
        Preconditions.checkNotNull(node);

        for (Map.Entry<String, String> keyValueEntry : keyValuemMap.entrySet()) {
            node = addComment(node, keyValueEntry.getKey(), keyValueEntry.getValue());
        }

        return node;
    }
    public static Node addComment(Node node, String key, String value){

        // replace
        List<Comment> comments = new ArrayList<>(node.getComments());

        int ind = -1;
        for (int i = 0; i < comments.size(); i++) {
            String valueFromContent = getValueFromContent(comments.get(i), key);
            if( valueFromContent != null ){
                ind = i;
            }
        }

        if( ind != -1)
            comments.remove(ind);


        GeneratedComment generatedComment = new GeneratedComment();
        Comment comment = null;
        if( key.equals(GeneratedComment.OLD_NAME_KEY)) {
            comment = generatedComment.generateKey_OldName(value, node.getSourceLocation());
        }else{
            comment = new Comment(MessageFormat.format(PATTERN, key,
                    value),
                    node.getSourceLocation());
        }

        // replace


        comments.add(comment);

        if( node instanceof  Field)
            node = ((Field) node).transform(a -> a.comments(comments));
        else if( node instanceof SelectionSet)
            node = ((SelectionSet) node).transform(a -> a.comments(comments));
        else if( node instanceof  Directive)
            node = ((Directive) node).transform(a -> a.comments(comments));
        else if( node instanceof  Argument)
            node = ((Argument) node).transform(a -> a.comments(comments));
        else if( node instanceof  InlineFragment)
            node = ((InlineFragment) node).transform(a -> a.comments(comments));
        else if( node instanceof  FragmentDefinition)
            node = ((FragmentDefinition) node).transform(a -> a.comments(comments));
        else if( node instanceof  OperationDefinition)
            node = ((OperationDefinition) node).transform(a -> a.comments(comments));

        return node;

    }

   /* public static Node addComment(GqlNodeContext node, String key, String value){
        Comment comment = null;
        GeneratedComment generatedComment = new GeneratedComment();
       // List<Comment> comments = new ArrayList<>(node.getNode().getComments());
        if( key.equals(GeneratedComment.OLD_NAME_KEY)) {
            comment = generatedComment.generateKey_OldName(value, node.getNode().getSourceLocation());
        }else{
            comment = new Comment(MessageFormat.format(PATTERN, key,
                    value),
                    node.getNode().getSourceLocation());
        }

        return TransformUtils.addComments(node, comment);

    }*/
    public Comment generateKey_OldName(String oldNameValue, SourceLocation sourceLocation){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(oldNameValue));
        Preconditions.checkArgument(sourceLocation != null );
       return new Comment(MessageFormat.format(PATTERN, OLD_NAME_KEY,
               oldNameValue),
               sourceLocation);
    }

    public static String getTransactionIdValue(Node node){

        return getCommentValue(node, TRANSFORM_TRX_ID);
    }

    private static String getValueFromContent(Comment comment, String keyName){
        if( comment == null ){
            return null;
        }
        String content = comment.getContent();

        if (content.startsWith(INTERNAL_GENERATED)) {
            String substringValue = content.substring(content.indexOf("[") + 1, content.indexOf("]"));

            String[] strings = substringValue.split(":");
            if (strings.length < 2) {
                return null;
            }

            String[] splitKeyValue = strings[0].split("=");

            String key = splitKeyValue[0];
            String value = splitKeyValue[1];

            if (key.equals(KEY) && value.equals(keyName)) {

                splitKeyValue = strings[1].split("=");
                if (splitKeyValue[0].equals(VALUE)) {
                    return splitKeyValue[1];
                }
            }
        }

        return null;
    }

    private static String getCommentValue(Node node, String keyName) {
        Preconditions.checkArgument(node != null);
        List<Comment> comments = node.getComments();

        if (comments == null || comments.size() == 0) {
            return null;
        }

        for (Comment comment : comments) {
            String valueFromContent = getValueFromContent(comment, keyName);
            if( valueFromContent != null ){
                return valueFromContent;
            }
        }

        return null;
    }

    public static String getOldName(Node node){

        return getCommentValue(node, OLD_NAME_KEY);
    }
}
