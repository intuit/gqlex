package com.intuit.library.eXtendGql.transformer;

import com.intuit.library.common.DocumentElementType;
import com.intuit.library.common.GqlNodeContext;
import com.intuit.library.common.RawPayload;
import com.intuit.library.eXtendGql.gxpath.selector.SelectorFacade;
import graphql.language.Field;
import graphql.language.InlineFragment;
import graphql.language.TypeName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransformExecutorTest {

    @Test
    void transform_mutation_add_child_many_twice_same_paths() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        //   SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        //  GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        //   assertNotNull(select);

        //   assertTrue(select.getType().equals(DocumentElementType.FIELD));
        //   Field node = (Field) select.getNode();
        //    assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name1"))
                .addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"))
        //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"))
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary");
        //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);
        ;
        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    commentary\n" +
                "    commentary\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "      new_name2\n" +
                "    }\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "      new_name2\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_add_child_many_twice_diff_paths() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        //   SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        //  GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        //   assertNotNull(select);

        //   assertTrue(select.getType().equals(DocumentElementType.FIELD));
        //   Field node = (Field) select.getNode();
        //    assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name1"))
                .addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary",new Field("new_name2"))
        //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"))
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary");
        //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);
        ;
        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "    }\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "    }\n" +
                "    commentary {\n" +
                "      new_name2\n" +
                "    }\n" +
                "    commentary {\n" +
                "      new_name2\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_add_child_many() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

     //   SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

      //  GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

     //   assertNotNull(select);

     //   assertTrue(select.getType().equals(DocumentElementType.FIELD));
     //   Field node = (Field) select.getNode();
    //    assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name1"))
                //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"))
                //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary");
        //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);
;
        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    commentary\n" +
                "    commentary\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "    }\n" +
                "    stars {\n" +
                "      new_name1\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

   // @Test
    void transform_on_query_inline_frag() throws IOException {


        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString,
                "//query[name=HeroForEpisode]/hero/Droid[type=infrag]");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.INLINE_FRAGMENT));
        InlineFragment node = (InlineFragment) select.getNode();
        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);
        assertTrue(typeCondition.getName().equalsIgnoreCase("Droid"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//query[name=HeroForEpisode]/hero/Droid[type=infrag]",new Field("new_name1"));
        transformBuilder.addSiblingNode("//query[name=HeroForEpisode]/hero/Droid[type=infrag]",new Field("new_name2"));

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);
        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.println(executeRawPayload.getQueryValue());

        assertEquals("query HeroForEpisode($ep: Episode!) {\n" +
                "  hero(episode: $ep) {\n" +
                "    name\n" +
                "    ... on Human {\n" +
                "      height\n" +
                "    }\n" +
                "    ... on Droid {\n" +
                "      primaryFunction\n" +
                "      new_name1\n" +
                "    }\n" +
                "    new_name2\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());
    }

/*
    mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
        createReview(episode: $ep, review: $review) {
            stars
                    commentary
        }
    }*/
    @Test
    void transform_mutation_verify_remove_duplicate_nodes() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"))
            //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"))
                .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary");
              //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
               // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    stars\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());
    }

    @Test
    void transform_mutation_complex_construct_remove_same_field_same_path() throws IOException {
        String fileName = "multi_simple_mutation_complex.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"))
                //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"))
                .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/a/b/c/stars");
        //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    b {\n" +
                "      commentary\n" +
                "    }\n" +
                "    c {\n" +
                "      commentary\n" +
                "    }\n" +
                "    a {\n" +
                "      b {\n" +
                "        c {\n" +
                "          NULL\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "    a {\n" +
                "      b {\n" +
                "        c {\n" +
                "          NULL\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());


    }

    @Test
    void transform_mutation_complex_construct_remove_same_field_diff_path() throws IOException {
        String fileName = "multi_simple_mutation_complex.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"))
                //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"))
                .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/c/commentary");
        //  .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        // .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore:\n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter:\n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    a {\n" +
                "      b {\n" +
                "        c {\n" +
                "          stars\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "    b {\n" +
                "      commentary\n" +
                "    }\n" +
                "    a {\n" +
                "      b {\n" +
                "        c {\n" +
                "          stars\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "    c {\n" +
                "      NULL\n" + //removed node replaced with NULL TODO
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());


    }


    @Test
    void transform_mutation_add_sibling() throws IOException {
        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"));
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    commentary\n" +
                "  }\n" +
                "  new_name2\n" +
                "}\n", executeRawPayload.getQueryValue());

    }


    @Test
    void transform_mutation_add_sibling_twice_multi_1() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"));
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name1"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    commentary\n" +
                "    stars\n" +
                "    commentary\n" +
                "    new_name1\n" +
                "  }\n" +
                "  new_name2\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_twice_multi_concatenate_add_child_plus_add_sibling_plus_update_node_plus_remove_node_plus_duplicate_node() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        String queryString = Files.readString(file.toPath());

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("child_of_stars"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"))
                .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
                .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
                .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/sibling_of_stars", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    sibling_of_stars\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "    sibling_of_stars\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }


    @Test
    void transform_mutation_twice_multi_concatenate_add_child_plus_add_sibling_plus_update_node_plus_remove_node() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("child_of_stars"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"))
                .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
                .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary");
        //
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    sibling_of_stars\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_twice_multi_concatenate_add_child_plus_add_sibling_plus_update_node() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

         assertTrue(select.getType().equals(DocumentElementType.FIELD));
         Field node = (Field) select.getNode();
           assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("child_of_stars"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"))
                .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name");
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        //
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    commentary\n" +
                "    commentary\n" +
                "    sibling_of_stars\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    star_new_name {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_twice_multi_concatenate_add_child_add_sibling_2() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        //  SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        /*GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);*/

        // assertTrue(select.getType().equals(DocumentElementType.FIELD));
        //     Field node = (Field) select.getNode();
        //   assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("child_of_stars"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"));

        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    commentary\n" +
                "    commentary\n" +
                "    stars {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    stars {\n" +
                "      child_of_stars\n" +
                "    }\n" +
                "    sibling_of_stars\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_twice_multi_concatenate_add_child_add_sibling_1() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        //  SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        /*GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);*/

        // assertTrue(select.getType().equals(DocumentElementType.FIELD));
        //     Field node = (Field) select.getNode();
        //   assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("child_of_createReview"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"));

        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    commentary\n" +
                "    stars\n" +
                "    commentary\n" +
                "    child_of_createReview\n" +
                "    sibling_of_stars\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_add_sibling_twice_multi_2_same_level() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

      //  SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        /*GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);*/

       // assertTrue(select.getType().equals(DocumentElementType.FIELD));
   //     Field node = (Field) select.getNode();
     //   assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"));
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name1"))
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary",new Field("new_name2"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    commentary\n" +
                "    stars\n" +
                "    commentary\n" +
                "    new_name1\n" +
                "    new_name2\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    //multi_simple_query_with_alias.txt
    @Test
    void transform_mutation_add_child_under_alias() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//query/Instrument/Reference[alias=aliasme]");

        assertNotNull(select);

        assertEquals(3,select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertTrue(gqlNodeContext.getType().equals(DocumentElementType.FIELD));
            Field node = (Field) gqlNodeContext.getNode();
            assertEquals("aliasme",node.getAlias());
            assertTrue(node.getName().equalsIgnoreCase("Reference"));
        }


        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//query/Instrument/Reference[alias=aliasme]",new Field("new_name1"));
                //.addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("{\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    aliasfor: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "    }\n" +
                "    aliasfor: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "    }\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_name1\n" +
                "    }\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_name1\n" +
                "    }\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_name1\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_duplicate_fragment_spread() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        //GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

        // assertNotNull(select);

        //  Field node = (Field) select.getNode();
        // assertEquals("totalCount", node.getName());
        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//query[name=HeroComparison]/hero",new Field("new_child_under_friendsConnection"))
                // .removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        .duplicateNode("//query[name=HeroComparison]/hero[alias=leftComparison]", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    totalCount\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_update_query_change_alias() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());


        String queryString = Files.readString(file.toPath());


        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//query[name=HeroComparison]/hero",new Field("new_child_under_friendsConnection"))
                //.removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
                .updateNodeName("//query[name=HeroComparison]/hero[alias=leftComparison]","leftComparison_new_name")
                .updateNodeAlias("//query[name=HeroComparison]","rightComparison_new_alias");
        // .duplicateNode("//query[name=HeroComparison]/hero[alias=leftComparison]", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    totalCount\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "query HeroComparison($first: Int = 3) {\n" +
                "  rightComparison: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  leftComparison: leftComparison_new_name(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }
    @Test
    void transform_update_fragment_spread_change_alias() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        //GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

        // assertNotNull(select);

        //  Field node = (Field) select.getNode();
        // assertEquals("totalCount", node.getName());
        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//query[name=HeroComparison]/hero",new Field("new_child_under_friendsConnection"))
                //.removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
                .updateNodeName("//query[name=HeroComparison]/hero[alias=leftComparison]","leftComparison_new_name")
                .updateNodeAlias("//query[name=HeroComparison]/hero[alias=rightComparison]","rightComparison_new_alias");
        // .duplicateNode("//query[name=HeroComparison]/hero[alias=leftComparison]", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    totalCount\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: leftComparison_new_name(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison_new_alias: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_update_fragment_spread() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        //GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

        // assertNotNull(select);

        //  Field node = (Field) select.getNode();
        // assertEquals("totalCount", node.getName());
        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//query[name=HeroComparison]/hero",new Field("new_child_under_friendsConnection"))
                //.removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
                .updateNodeName("//query[name=HeroComparison]/hero[alias=leftComparison]","leftComparison_new_name")
                .updateNodeName("//query[name=HeroComparison]/hero[alias=rightComparison]","rightComparison_new_name");
               // .duplicateNode("//query[name=HeroComparison]/hero[alias=leftComparison]", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    totalCount\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: leftComparison_new_name(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison: rightComparison_new_name(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_add_child_and_remove_child_from_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        //GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

       // assertNotNull(select);

      //  Field node = (Field) select.getNode();
       // assertEquals("totalCount", node.getName());
        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//comparisonFields[type=frag]/friendsConnection",new Field("new_child_under_friendsConnection"))
                .removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "    new_child_under_friendsConnection\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void add_child_to_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection");

        assertNotNull(select);

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//comparisonFields[type=frag]/friendsConnection",new Field("new_child_under_friendsConnection"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    totalCount\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "    new_child_under_friendsConnection\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void remove_child_from_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection/totalCount");

        assertNotNull(select);

        Field node = (Field) select.getNode();
        assertEquals("totalCount", node.getName());
        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//comparisonFields[type=frag]/friendsConnection/totalCount",new Field("new_child_under_friendsConnection"));
        .removeNode("//comparisonFields[type=frag]/friendsConnection/totalCount");
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("query HeroComparison($first: Int = 3) {\n" +
                "  leftComparison: hero(episode: EMPIRE) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "  rightComparison: hero(episode: JEDI) {\n" +
                "    ...comparisonFields\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment comparisonFields on Character {\n" +
                "  name\n" +
                "  friendsConnection(first: $first) {\n" +
                "    edges {\n" +
                "      node {\n" +
                "        name\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_add_child_under_alias_no_expilict_alias_set_in_query() throws IOException {
        String fileName = "multi_simple_query_with_alias.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        List<GqlNodeContext> select = selectorFacade.selectMany(queryString, "//query/Instrument/Reference");

        assertNotNull(select);

        assertEquals(5,select.size());
        for (GqlNodeContext gqlNodeContext : select) {
            assertTrue(gqlNodeContext.getType().equals(DocumentElementType.FIELD));
            Field node = (Field) gqlNodeContext.getNode();
           // assertEquals("aliasme",node.getAlias());
            assertTrue(node.getName().equalsIgnoreCase("Reference"));
        }


        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder.addChildrenNode("//query/Instrument/Reference[alias=aliasme]",new Field("new_child_under_aliasme"))
                    .addChildrenNode("//query/Instrument/Reference[alias=aliasfor]",new Field("new_child_under_aliasfor"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("{\n" +
                "  Instrument(id: \"1234\") {\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_child_under_aliasme\n" +
                "    }\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_child_under_aliasme\n" +
                "    }\n" +
                "    aliasme: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_child_under_aliasme\n" +
                "    }\n" +
                "    aliasfor: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_child_under_aliasfor\n" +
                "    }\n" +
                "    aliasfor: Reference {\n" +
                "      Name\n" +
                "      title\n" +
                "      new_child_under_aliasfor\n" +
                "    }\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }

    @Test
    void transform_mutation_add_sibling_multi() throws IOException {
        String fileName = "multi_simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        TransformBuilder transformBuilder = new TransformBuilder();
        transformBuilder//.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"));
                .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("new_name2"));
        //.removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        // .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        //.duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

        TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        RawPayload rawPayload = new RawPayload();
        rawPayload.setQueryValue(queryString);

        System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

        RawPayload executeRawPayload = transformExecutor.execute(rawPayload);

        System.out.print("\nAfter: \n\n" + executeRawPayload.getQueryValue());

        assertEquals("mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {\n" +
                "  createReview(episode: $ep, review: $review) {\n" +
                "    stars\n" +
                "    commentary\n" +
                "    stars\n" +
                "    commentary\n" +
                "    new_name2\n" +
                "  }\n" +
                "}\n", executeRawPayload.getQueryValue());

    }
}