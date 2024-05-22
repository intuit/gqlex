package com.intuit.gqlex.gqlxpath.selector;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SearchPathBuilderTest {

    @Test
    void isAnySet() {
    }

    @Test
    void getTuneableSearchData() {
    }

    @Test
    void getPathElementsToSearch() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("//query/a/b/c");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();

        assertNotNull(pathElements);
    }

    @Test
    void getPathElementsToSearch_invalid_path() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("//query/a/b/c{key=value}");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();

        });
    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("//query/a/b/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_single_selection() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("/query/a/b/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

        assertEquals(4, pathElements.size());
        //assertEquals("query", pathElements.get(0));
        SearchPathElement searchPathElement = pathElements.get(0);

        assertEquals("query", searchPathElement.getByKey("name"));


    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_verify_range_empty_start() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{:100}//query/a/b/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

        assertEquals(4, pathElements.size());
        SearchPathElement searchPathElement = pathElements.get(0);

        assertEquals("query", searchPathElement.getByKey("name"));

        SelectionRange selectionRange = searchPathBuilder.getSelectionRange();

        assertNotNull(selectionRange);

        assertEquals(0,selectionRange.getRangeStart());
        assertEquals(100,selectionRange.getRangeEnd());
        assertEquals(false,selectionRange.isRangeEndAll());
    }




    @Test
    void getPathElementsToSearch_valid_path_check_rect_verify_range_empty_end() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:}//query/a/b/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

        assertEquals(4, pathElements.size());
        SearchPathElement searchPathElement = pathElements.get(0);

        assertEquals("query", searchPathElement.getByKey("name"));

        SelectionRange selectionRange = searchPathBuilder.getSelectionRange();

        assertNotNull(selectionRange);

        assertEquals(7,selectionRange.getRangeStart());
        assertEquals(true,selectionRange.isRangeEndAll());
    }

    @Test
    void verify_any_at_the_end() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
                    SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:100}//query/a/b/c/...");
                });
    }

    @Test
    void verify_some_any_only() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:100}//.../.../...");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        });
    }

    @Test
    void verify_any_only() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:100}//...");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        });
    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_verify_range_with_any() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:100}//.../a/.../c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

        assertEquals(4, pathElements.size());
        SearchPathElement searchPathElement = pathElements.get(0);

        assertEquals(null, searchPathElement.getByKey("name"));
        assertTrue(searchPathElement.isAnyValue());

        searchPathElement = pathElements.get(1);

        assertEquals("a", searchPathElement.getByKey("name"));
        assertTrue(!searchPathElement.isAnyValue());


        searchPathElement = pathElements.get(2);

        assertEquals(null, searchPathElement.getByKey("name"));
        assertTrue(searchPathElement.isAnyValue());

        searchPathElement = pathElements.get(3);

        assertEquals("c", searchPathElement.getByKey("name"));
        assertEquals("value", searchPathElement.getByKey("key"));
        assertEquals("fld", searchPathElement.getByKey("doc_elem_type"));
        assertTrue(!searchPathElement.isAnyValue());

        SelectionRange selectionRange = searchPathBuilder.getSelectionRange();

        assertNotNull(selectionRange);

        assertEquals(7,selectionRange.getRangeStart());
        assertEquals(100,selectionRange.getRangeEnd());
    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_verify_range() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{7:100}//query/a/b/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

        assertEquals(4, pathElements.size());
        SearchPathElement searchPathElement = pathElements.get(0);

        assertEquals("query", searchPathElement.getByKey("name"));

        SelectionRange selectionRange = searchPathBuilder.getSelectionRange();

        assertNotNull(selectionRange);

        assertEquals(7,selectionRange.getRangeStart());
        assertEquals(100,selectionRange.getRangeEnd());
    }
    @Test
    void getPathElementsToSearch_single_path_range_invalid_start_greater_to_end() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {

            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{3:1}/query/a/b/c[key=value]");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        });

        System.out.println(exception);
    }
    @Test
    void getPathElementsToSearch_single_path_wtih_range_invalid() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {

            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{-10:-1000}/query/a/b/c[key=value]");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();


        });
    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_verify_invalid_range() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {

            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("{-10:-1000}//query/a/b/c[key=value]");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();



        });
    }

    @Test
    void getPathElementsToSearch_valid_path_check_rect_all() {

        SearchPathBuilder searchPathBuilder = new SearchPathBuilder("//query[key=value]/a[key=value]/b[key=value]/c[key=value]");
        LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        assertNotNull(pathElements);

    }

    @Test
    void getPathElementsToSearch_invalid_path_empty() {

        Exception exception = assertThrows(GqlSelectionSyntaxException.class, () -> {
            SearchPathBuilder searchPathBuilder = new SearchPathBuilder("//query[key=value]/a[key=value]/b[key=value]/c[key=value]/  ");
            LinkedList<SearchPathElement> pathElements = searchPathBuilder.getPathElements();
        });

    }

    @Test
    void addElementByOrder() {
    }

    @Test
    void testAddElementByOrder() {
    }

    @Test
    void testAddElementByOrder1() {
    }

    @Test
    void getPathElements() {
    }
}