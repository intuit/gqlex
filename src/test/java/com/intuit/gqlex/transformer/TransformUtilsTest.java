package com.intuit.gqlex.transformer;

import com.intuit.gqlex.common.GqlNode;
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.common.eXtendGqlWriter;
import com.intuit.gqlex.gxpath.selector.SelectorFacade;
import graphql.language.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;

class TransformUtilsTest {

    @Test
        // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    void add_child_field() throws IOException {

        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        Node newNode = new Field("new_name");
        Node node = TransformUtils.addChildren(select, newNode);

        String newGqlValue = eXtendGqlWriter.writeToString(node);
        assertTrue(newGqlValue.contains("new_name"));
        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("NAME"));
    }

    @Test
    // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    public void add_fields_same_level() throws IOException {


        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        Node newNode = new Field("new_name1");
        Node node = TransformUtils.addChildren(select, newNode);

        GqlNodeContext select2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name");

        Node newNode2 = new Field("new_name2");
        node = TransformUtils.addChildren(select2, newNode2);

        queryString = eXtendGqlWriter.writeToString(node);
        System.out.println(queryString);
        assertTrue(queryString.contains("new_name1"));
        assertTrue(queryString.contains("new_name2"));
        GqlNodeContext newNameSelect = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name/new_name1");
        assertTrue(newNameSelect.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) newNameSelect.getNode()).getName().equalsIgnoreCase("new_name1"));

        GqlNodeContext newNameSelect2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name/new_name2");

        assertTrue(newNameSelect2.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) newNameSelect2.getNode()).getName().equalsIgnoreCase("new_name2"));

    }

    @Test
    // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    public void add_same_field_same_level() throws IOException {


            String fileName = "duplicate_field_names.txt";
            String testFolder = "eXtendGql";

            ClassLoader classLoader = this.getClass().getClassLoader();

            File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

            SelectorFacade selectorFacade = new SelectorFacade();

            String queryString = Files.readString(file.toPath());

            // query {  Instrument(id: "1234") }
            GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

            Node newNode = new Field("new_name");
            Node node = TransformUtils.addChildren(select, newNode);

            GqlNodeContext select2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name");

            //Node newNode2 = new Field("new_name");
            node = TransformUtils.addChildren(select2, newNode);

        queryString = eXtendGqlWriter.writeToString(node);
        System.out.println(queryString);
        assertTrue(queryString.contains("new_name"));
        GqlNodeContext newNameSelect = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name/new_name");
        assertTrue(newNameSelect.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) newNameSelect.getNode()).getName().equalsIgnoreCase("new_name"));

        GqlNodeContext newNameSelect2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node), "//query/Instrument/Reference/name/new_name");

        assertTrue(newNameSelect2.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) newNameSelect2.getNode()).getName().equalsIgnoreCase("new_name"));

    }

    @Test
        // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    public void add_child_field_verify_no_context() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {

        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/name");

        Node newNode = new Field("new_name");
        Node node = TransformUtils.addChildren(select, newNode);
            select.getNodeStack().clear();
        Node newNode2 = new Field("new_name");
        node = TransformUtils.addChildren(select, newNode);

        });
    }


    @Test
    // query {  Instrument(Name: "1234") {    Reference {      Name title   }  }}
    public void add_same_child_field_nested_with_context() throws IOException {


        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        Node newNode = new Field("new_name");
        Node node = TransformUtils.addChildren(select, newNode);

        queryString = eXtendGqlWriter.writeToString(node);

        GqlNodeContext selectParent = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name/new_name");
        newNode = new Field("new_name");
        node = TransformUtils.addChildren(selectParent, newNode);
        String queryString1 = eXtendGqlWriter.writeToString(node);



        GqlNodeContext selectSon = selectorFacade.selectSingle(queryString1, "//query/Instrument/Reference/name/new_name/new_name");

        queryString = eXtendGqlWriter.writeToString(node);
        System.out.println(queryString);
        assertTrue(queryString.contains("new_name"));
        assertTrue(selectParent.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) selectParent.getNode()).getName().equalsIgnoreCase("new_name"));

        assertTrue(selectSon.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) selectSon.getNode()).getName().equalsIgnoreCase("new_name"));


    }

    @Test
    void addSiblings_mutation() throws IOException {
        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext selectStars = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview/stars");

        assertNotNull(selectStars);

        assertTrue(selectStars.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) selectStars.getNode();
        assertTrue(node.getName().equalsIgnoreCase("stars"));

        Node newNode1 = new Field("new_star_1");
        newNode1 = TransformUtils.addSibling(selectStars, newNode1);

        //System.out.println(eXtendGqlWriter.writeToString(newNode1));

        Node newNode2 = new Field("new_star_2");
        GqlNodeContext select = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(newNode1), "//mutation[name=CreateReviewForEpisode]/createReview/stars");
        newNode2 = TransformUtils.addSibling(select
                , newNode2);

        System.out.println("\nAfter:\n\n" + eXtendGqlWriter.writeToString(newNode2));

       verifyNewNodeAdded(selectorFacade, newNode2, "new_star_1","//mutation[name=CreateReviewForEpisode]/createReview/new_star_1");
        verifyNewNodeAdded(selectorFacade, newNode2, "new_star_2","//mutation[name=CreateReviewForEpisode]/createReview/new_star_2");


    }

    @Test
    void addChild_mutation() throws IOException {
        String fileName = "simple_mutation.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//mutation[name=CreateReviewForEpisode]/createReview");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("createReview"));

        Node newNode = new Field("new_name");
        newNode = TransformUtils.addChildren(select, newNode);

        verifyNewNodeAdded(selectorFacade, newNode, "new_name","//mutation[name=CreateReviewForEpisode]/createReview/new_name");
    }

    private static void verifyNewNodeAdded(SelectorFacade selectorFacade,  Node node, String newNodeName, String searchPath) {
        String queryString = eXtendGqlWriter.writeToString(node);
        System.out.println(queryString);
        assertTrue(queryString.contains(newNodeName));
        GqlNodeContext checkSelection = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(node),
                searchPath);
        assertNotNull(checkSelection);
        assertTrue(((Field) checkSelection.getNode()).getName().equalsIgnoreCase(newNodeName));
    }


    @Test
    void addChild_ToInlineFrag() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroForEpisode]/hero/Droid[type=infrag]");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.INLINE_FRAGMENT));
        InlineFragment node = (InlineFragment) select.getNode();
        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);
        assertTrue(typeCondition.getName().equalsIgnoreCase("Droid"));

        Node newNode = new Field("new_name");
        newNode = TransformUtils.addChildren(select, newNode);

        verifyNewNodeAdded(selectorFacade, newNode, "new_name","//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name");
    }


    @Test
    void add_new_child_under_new_child_ToInlineFrag() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroForEpisode]/hero/Droid[type=infrag]");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.INLINE_FRAGMENT));
        InlineFragment node = (InlineFragment) select.getNode();
        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);
        assertTrue(typeCondition.getName().equalsIgnoreCase("Droid"));

        Node newNode = new Field("new_name1");
        newNode = TransformUtils.addChildren(select, newNode);

        verifyNewNodeAdded(selectorFacade, newNode, "new_name1","//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name1");

        GqlNodeContext select2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(newNode), "//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name1");

        Node newNode2 = new Field("new_name2");
        newNode2 = TransformUtils.addChildren(select2, newNode2);

        verifyNewNodeAdded(selectorFacade, newNode2, "new_name2","//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name1/new_name2");

    }

    @Test
    void add_new_sibling_ToInlineFrag() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query[name=HeroForEpisode]/hero/Droid[type=infrag]");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.INLINE_FRAGMENT));
        InlineFragment node = (InlineFragment) select.getNode();
        TypeName typeCondition = node.getTypeCondition();
        assertNotNull(typeCondition);
        assertTrue(typeCondition.getName().equalsIgnoreCase("Droid"));

        Node newNode = new Field("new_name1");
        newNode = TransformUtils.addChildren(select, newNode);

        verifyNewNodeAdded(selectorFacade, newNode, "new_name1","//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name1");

        GqlNodeContext select2 = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(newNode), "//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name1");

        Node newNode2 = new Field("new_name2");
        newNode2 = TransformUtils.addSibling(select2, newNode2);

        verifyNewNodeAdded(selectorFacade, newNode2, "new_name2","//query[name=HeroForEpisode]/hero/Droid[type=infrag]/new_name2");

    }

    @Test
    void add_child_and_sibling_to_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("friendsConnection"));

        Node newNode = new Field("new_name1");
        newNode = TransformUtils.addChildren(select, newNode);

        verifyNewNodeAdded(selectorFacade, newNode, "new_name1","//comparisonFields[type=frag]/friendsConnection/new_name1");

        GqlNodeContext selectSibling = selectorFacade.selectSingle(eXtendGqlWriter.writeToString(newNode), "//comparisonFields[type=frag]/friendsConnection/new_name1");
        Node newNode2 = new Field("new_name2");
        newNode2 = TransformUtils.addSibling(selectSibling, newNode2);

        verifyNewNodeAdded(selectorFacade, newNode2, "new_name2","//comparisonFields[type=frag]/friendsConnection/new_name2");

    }
/*

        } else if (newNode instanceof FragmentDefinition) {
            newNode = ((FragmentDefinition)  nodeContext.getNode()).transform(a -> a.name(newName));
        } else if (newNode instanceof InlineFragment) {
            newNode = ((InlineFragment)  nodeContext.getNode()).transform(a -> a.typeCondition(new TypeName(newName)));
        } else if (newNode instanceof FragmentSpread) {
            newNode = ((FragmentSpread)  nodeContext.getNode()).transform(a -> a.name(newName));
        } else if (newNode instanceof OperationDefinition) {
            newNode = ((OperationDefinition)  nodeContext.getNode()).transform(a -> a.name(newName));
        } else if (newNode instanceof Directive) {
            newNode = ((Directive)  nodeContext.getNode()).transform(a -> a.name(newName));
        } else if (newNode instanceof Argument) {
            newNode = ((Argument)  nodeContext.getNode()).transform(a -> a.name(newName));
        } else if (newNode instanceof VariableDefinition) {
            newNode = ((VariableDefinition)  nodeContext.getNode()).transform(a -> a.name(newName));
        }
 */

    @Test
    void update_frag_field_name() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]/friendsConnection");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("friendsConnection"));

        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node2 = TransformUtils.updateNodeName(select, "friendsConnection_new_name");

        String newGqlValue = eXtendGqlWriter.writeToString(node2);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//comparisonFields[type=frag]/friendsConnection_new_name");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FIELD));
    }


    @Test
    void update_inline_frag_name() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//query[name=HeroForEpisode]/hero/Droid[type=infrag]";
        GqlNodeContext select = selectorFacade.selectSingle(queryString, searchSyntax);

        assertNotNull(select);


        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node2 = TransformUtils.updateNodeName(select, "Human");

        String newGqlValue = eXtendGqlWriter.writeToString(node2);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//query[name=HeroForEpisode]/hero/Human[type=infrag]/primaryFunction");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FIELD));
    }


    @Test
    void update_directive_name_from_include_to_exclude() throws IOException {

        String fileName = "hero_example_directives_variables.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext includeDirectiveNode = selectorFacade.selectSingle(queryString, "//query[name=hero]/hero/friends/include[type=direc]");

        assertNotNull(includeDirectiveNode);

        assertTrue(includeDirectiveNode.getType().equals(DocumentElementType.DIRECTIVE));
        System.out.println("\nBefore manipulation:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node excludeDirectiveNode = TransformUtils.updateNodeName(includeDirectiveNode, "exclude");

        String newGqlValue = eXtendGqlWriter.writeToString(excludeDirectiveNode);

        System.out.println("\nAfter manipulation:\n\n" + newGqlValue);

        GqlNodeContext excludeUpdateNode = selectorFacade.selectSingle(newGqlValue, "//query[name=hero]/hero/friends/exclude[type=direc]");

        assertTrue(excludeUpdateNode.getType().equals(DocumentElementType.DIRECTIVE));

    }

    @Test
    void update_frag_name() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//comparisonFields[type=frag]");

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FRAGMENT_DEFINITION));
        FragmentDefinition node = (FragmentDefinition) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("comparisonFields"));

        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node2 = TransformUtils.updateNodeName(select, "comparisonFields_new_name");

        String newGqlValue = eXtendGqlWriter.writeToString(node2);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//comparisonFields_new_name[type=frag]");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FRAGMENT_DEFINITION));
    }

    @Test
    void updateNode_field() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node = TransformUtils.updateNodeName(select, "new_name_set");

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//query/Instrument/Reference/new_name_set");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FIELD));
    }


    @Test
    void updateNode_parent_query_name() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument");

        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node = TransformUtils.updateNodeName(select, "Instrument_new_name_set");

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//query/Instrument_new_name_set");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FIELD));
    }


    @Test
    void updateNode_parent_field() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference");

        System.out.println("\nBefore:\n\n" + queryString);

        // Node newNode = new Field("new_name");
        Node node = TransformUtils.updateNodeName(select, "Reference_new_name_set");

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext selectUpdated = selectorFacade.selectSingle(newGqlValue, "//query/Instrument/Reference_new_name_set");

        assertTrue(selectUpdated.getType().equals(DocumentElementType.FIELD));
    }


    @Test
    void duplicateChild_simple_query() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

       // Node newNode = new Field("new_name");
        Node node = TransformUtils.duplicateChild(select, 10);

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println(newGqlValue);

        assertTrue(newGqlValue.contains("Name"));
        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("NAME"));
    }

    @Test
    void duplicateChild_simple_query_duplicate_structure() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference");

        // Node newNode = new Field("new_name");
        Node node = TransformUtils.duplicateChild(select, 3, true);

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println(newGqlValue);

        assertTrue(newGqlValue.contains("Reference_1"));
        assertTrue(newGqlValue.contains("Reference_2"));
        assertTrue(newGqlValue.contains("Reference_3"));
        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        assertTrue(((Field) select.getNode()).getName().equalsIgnoreCase("Reference"));

        for (int i = 1; i <=3; i++) {
            GqlNodeContext selectGqlNode = selectorFacade.selectSingle(newGqlValue, MessageFormat.format("//query/Instrument/Reference_{0}/Name",i));
            assertTrue(selectGqlNode.getType().equals(DocumentElementType.FIELD));
            assertTrue(((Field) selectGqlNode.getNode()).getName().equalsIgnoreCase("NAME"));

        }
    }



    @Test
    void removeChild_simple_query() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference/name");

        System.out.println("\nBefore:\n\n" + queryString);
        // Node newNode = new Field("new_name");
        Node node = TransformUtils.removeChild(select);

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext select2 = selectorFacade.selectSingle(newGqlValue, "//query/Instrument/Reference/name");
        assertNull(select2);
    }

    @Test
    void removeChild_simple_query_remove_entire_structure() throws IOException {
        String fileName = "duplicate_field_names.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryString = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        GqlNodeContext select = selectorFacade.selectSingle(queryString, "//query/Instrument/Reference");

        System.out.println("\nBefore:\n\n" + queryString);
        // Node newNode = new Field("new_name");
        Node node = TransformUtils.removeChild(select);

        String newGqlValue = eXtendGqlWriter.writeToString(node);

        System.out.println("\nAfter:\n\n" + newGqlValue);

        GqlNodeContext select2 = selectorFacade.selectSingle(newGqlValue, "//query/Instrument/Reference");
        assertNull(select2);
    }


    @Test
    void remove_child_query_select_defiend_by_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryStringBeforeRemove = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//query[name=HeroComparison]/hero[alias=leftComparison]";
        GqlNodeContext select = selectorFacade.selectSingle(queryStringBeforeRemove, searchSyntax);

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));

        System.out.println("\nBefore: \n\n" + queryStringBeforeRemove);

        Node updateNode = TransformUtils.removeChild(select);

        verifyNodeRemoved(selectorFacade, searchSyntax, updateNode);
    }

    @Test
    void remove_children_in_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryStringBeforeRemove = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//comparisonFields[type=frag]/friendsConnection/edges";
        GqlNodeContext select = selectorFacade.selectSingle(queryStringBeforeRemove, searchSyntax);

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("edges"));

        System.out.println("\nBefore: \n\n" + queryStringBeforeRemove);
        
        Node updateNode = TransformUtils.removeChild(select);

        verifyNodeRemoved(selectorFacade, searchSyntax, updateNode);

       
    }

    @Test
    void remove_inexist_children_in_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryStringBeforeRemove = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//comparisonFields[type=frag]/friendsConnection";
        GqlNodeContext select = selectorFacade.selectSingle(queryStringBeforeRemove, searchSyntax);

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("friendsConnection"));
        System.out.println("\nBefore: \n\n" + queryStringBeforeRemove);

        Node nodeNotExist = ((Field) select.getNode()).transform(a -> a.name("node_not_exist"));
        select.setNode(new GqlNode(nodeNotExist, DocumentElementType.FIELD));

        try {
            Node updateNode = TransformUtils.removeChild(select);
            fail("expected the code to fail, cause field does not exist");
        }catch (Exception ex){
            System.out.println(ex.toString());
        }



    }

    @Test
    void remove_full_children_in_fragment() throws IOException {

        String fileName = "query_with_fragments.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryStringBeforeRemove = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//comparisonFields[type=frag]/friendsConnection";
        GqlNodeContext select = selectorFacade.selectSingle(queryStringBeforeRemove, searchSyntax);

        assertNotNull(select);

        assertTrue(select.getType().equals(DocumentElementType.FIELD));
        Field node = (Field) select.getNode();
        assertTrue(node.getName().equalsIgnoreCase("friendsConnection"));

        System.out.println("\nBefore: \n\n" + queryStringBeforeRemove);

        Node updateNode = TransformUtils.removeChild(select);

        verifyNodeRemoved(selectorFacade, searchSyntax, updateNode);


    }

    private static void verifyNodeRemoved(SelectorFacade selectorFacade, String searchSyntax, Node updateNode) {
        String queryString = eXtendGqlWriter.writeToString(updateNode);
        GqlNodeContext select2 = selectorFacade.selectSingle(queryString, searchSyntax);
        assertNull(select2);
        
        System.out.println("\nAfter: \n\n" + queryString);
    }


    @Test
    void remove_child_ToInlineFrag() throws IOException {

        String fileName = "inline_frag.txt";
        String testFolder = "eXtendGql";

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(testFolder + "/" + fileName).getFile());

        SelectorFacade selectorFacade = new SelectorFacade();

        String queryStringBeforeRemove = Files.readString(file.toPath());

        // query {  Instrument(id: "1234") }
        String searchSyntax = "//query[name=HeroForEpisode]/hero/Droid[type=infrag]";
        GqlNodeContext select = selectorFacade.selectSingle(queryStringBeforeRemove, searchSyntax);

        assertNotNull(select);


        System.out.println("\nBefore: \n\n" + queryStringBeforeRemove);

        Node updateNode = TransformUtils.removeChild(select);

        verifyNodeRemoved(selectorFacade, searchSyntax, updateNode);
    }
}