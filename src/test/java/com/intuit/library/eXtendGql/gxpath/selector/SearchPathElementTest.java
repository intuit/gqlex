package com.intuit.library.eXtendGql.gxpath.selector;

import com.intuit.library.common.DocumentElementType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SearchPathElementTest {

    @Test
    void check_no_attr() {

        Map<String, String> attr = new HashMap<>();
        SearchPathElement searchPathElement = new SearchPathElement(DocumentElementType.FIELD, attr);
        assertNotNull(searchPathElement);

    }

    @Test
    void check_attr() {

        Map<String, String> attr = new HashMap<>();
        SearchPathElement searchPathElement = new SearchPathElement(DocumentElementType.FIELD, attr);
        assertNotNull(searchPathElement);

    }

    @Test
    void check_doc_type_is_null() {

        Map<String, String> attr = new HashMap<>();
        SearchPathElement searchPathElement = new SearchPathElement(null, attr);
        assertNotNull(searchPathElement);

    }

    @Test
    void check_doc_type_is_null_2() {

        SearchPathElement searchPathElement = new SearchPathElement( null);
        assertNotNull(searchPathElement);

    }


    @Test
    void check_doc_type_with_attr() {

        Map<String, String> attr = new HashMap<>();
        attr.put(SearchPathElement.NAME, "name_value");
        SearchPathElement searchPathElement = new SearchPathElement( DocumentElementType.FIELD,attr);
        assertNotNull(searchPathElement);
        assertNotNull(searchPathElement.getSelectionAttributeMap());

        assertNotNull(searchPathElement.toString());

    }

    @Test
    void check_doc_type_with_attr_add_new_attr() {

        Map<String, String> attr = new HashMap<>();
        attr.put(SearchPathElement.NAME, "name_value");
        SearchPathElement searchPathElement = new SearchPathElement( DocumentElementType.FIELD,attr);

        searchPathElement.add("a_key", "a_value");

        assertNotNull(searchPathElement);
        SelectionAttributeMap selectionAttributeMap = searchPathElement.getSelectionAttributeMap();
        assertNotNull(selectionAttributeMap);

        Map<String, String> attributes = selectionAttributeMap.getAttributes();
        assertNotNull(attributes);
        assertNotNull(attributes.get("a_key"));
        assertNotNull(attributes.get(SearchPathElement.NAME));

        assertNotNull(searchPathElement.toString());

    }

    @Test
    void check_doc_type_withno_attr_add_new_attr() {

        SearchPathElement searchPathElement = new SearchPathElement( DocumentElementType.FIELD,null);

        searchPathElement.add("a_key", "a_value");

        assertNotNull(searchPathElement);
        SelectionAttributeMap selectionAttributeMap = searchPathElement.getSelectionAttributeMap();
        assertNotNull(selectionAttributeMap);

        Map<String, String> attributes = selectionAttributeMap.getAttributes();
        assertNotNull(attributes);
        assertNotNull(attributes.get("a_key"));

        assertNotNull(searchPathElement.toString());

    }
}