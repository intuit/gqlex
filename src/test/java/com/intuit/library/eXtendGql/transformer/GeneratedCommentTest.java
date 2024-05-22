package com.intuit.library.eXtendGql.transformer;

import graphql.language.Comment;
import graphql.language.Field;
import graphql.language.SourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedCommentTest {

    @Test
    void generateKey_OldName() {

        GeneratedComment generatedComment = new GeneratedComment();
        Comment oldNameValueComment = generatedComment.generateKey_OldName("old_name_value", SourceLocation.EMPTY);

        assertNotNull(oldNameValueComment);
    }

    @Test
    void generateKey_OldName_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GeneratedComment generatedComment = new GeneratedComment();
            Comment oldNameValueComment = generatedComment.generateKey_OldName("", SourceLocation.EMPTY);

            assertNotNull(oldNameValueComment);
        });
    }

    @Test
    void getOldName() {

        Field field = new Field("name");

        List<Comment> commentList = new ArrayList<>();
        GeneratedComment generatedComment = new GeneratedComment();
        Comment oldNameValueComment = generatedComment.generateKey_OldName("old_name_value", SourceLocation.EMPTY);
        commentList.add(oldNameValueComment);

        Field transformField = field.transform(a -> a.comments(commentList));

        String oldNameValue = GeneratedComment.getOldName(transformField);

        assertEquals("old_name_value", oldNameValue);
    }


    @Test
    void getOldName_manyComment() {

        Field field = new Field("name");

        List<Comment> commentList = new ArrayList<>();
        GeneratedComment generatedComment = new GeneratedComment();

        commentList.add(new Comment("bla_bla_1", SourceLocation.EMPTY));
        commentList.add( generatedComment.generateKey_OldName("old_name_value_1", SourceLocation.EMPTY));
        commentList.add( generatedComment.generateKey_OldName("old_name_value_2", SourceLocation.EMPTY));

        commentList.add(new Comment("bla_bla_2", SourceLocation.EMPTY));

        Field transformField = field.transform(a -> a.comments(commentList));

        String oldNameValue = GeneratedComment.getOldName(transformField);

        assertEquals("old_name_value_1", oldNameValue);
    }


    @Test
    void getOldName_inexist() {

        Field field = new Field("name");

        List<Comment> commentList = new ArrayList<>();
        GeneratedComment generatedComment = new GeneratedComment();
       //Comment oldNameValueComment = generatedComment.generateKey_OldName("old_name_value", SourceLocation.EMPTY);

        Comment comment =new Comment("bla_bla", SourceLocation.EMPTY);
        commentList.add(comment);

        Field transformField = field.transform(a -> a.comments(commentList));

        String oldNameValue = GeneratedComment.getOldName(transformField);

        assertNull( oldNameValue);
    }
}