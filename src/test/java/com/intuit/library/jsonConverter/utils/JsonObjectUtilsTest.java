package com.intuit.library.jsonConverter.utils;

import com.intuit.library.jsonConverter.ParserConsts;
import com.intuit.library.jsonConverter.model.ReservedInternJsonWords;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonObjectUtilsTest {

    @Test
    void addJsonObjectToParentJsonObject() {
    }

    @Test
    void testAddJsonObjectToParentJsonObject() {
    }

    //(JSONObject targetJsonObj, String key, Object value
    @Test
    void addToJsonObject_target_is_null() {

        try {
            JsonObjectUtils.addToJsonObject(null, "key", null);

            fail();
        } catch (Exception ex) {

        }
    }


    @Test
    void addToJsonObject_key_is_null() {

        try {
            JsonObjectUtils.addToJsonObject(new JSONObject(), null, null);


        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void addToJsonObject_key_is_empty() {

        try {
            JsonObjectUtils.addToJsonObject(new JSONObject(), "", null);


        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void addToJsonObject_key_is_empty_with_spaces() {

        try {
            JsonObjectUtils.addToJsonObject(new JSONObject(), "    ", null);


        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    void addToJsonObject_value_is_null() {


        JSONObject targetJsonObj = new JSONObject();
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", null);
        int size = targetJsonObj.keySet().size();

        assertTrue(size > 0);

        String key = targetJsonObj.keys().next();

        assertNotNull(key);

        Object value = targetJsonObj.get(key);

        assertEquals(JSONObject.NULL, value);


    }


    @Test
    void addToJsonObject_value_not_null() {


        JSONObject targetJsonObj = new JSONObject();
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", "valueName");
        int size = targetJsonObj.keySet().size();

        assertTrue(size > 0);

        String key = targetJsonObj.keys().next();

        assertNotNull(key);

        Object value = targetJsonObj.get(key);

        assertEquals("valueName", value.toString());


    }


    @Test
    void addToJsonObject_value_is_json_object_parent_empty() {


        JSONObject targetJsonObj = new JSONObject();
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key1", "value1");
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", valueJsonObj);
        int size = targetJsonObj.keySet().size();

        assertTrue(size > 0);

        String key = targetJsonObj.keys().next();

        assertNotNull(key);




    }

    @Test
    void addToJsonObject_value_is_json_object_target_has_same_key_as_child() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put("key1", "value_of_parent");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key1", "value1");
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"key1\":\"value_of_parent\",\"__content\":{\"keyname\":{\"key1\":\"value1\"}}}", targetJsonObj.toString());
    }

    @Test
    void addToJsonObject_value_is_json_object_target_has_not_same_key_as_child() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put("key1", "value_of_parent");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key2", "value1");
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"key1\":\"value_of_parent\",\"__content\":{\"key2\":\"value1\"}}", targetJsonObj.toString());
    }



    @Test
    void addToJsonObject_value_is_json_object_target_has_same_key_with_no_value_as_child() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put("key1", "");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key1", "value1");
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"key1\":\"\",\"__content\":{\"keyname\":{\"key1\":\"value1\"}}}", targetJsonObj.toString());
    }


    @Test
    void addToJsonObject_value_obj_has_some_childs() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put("key1", "");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key2", "value1");
        valueJsonObj.put("key3", "value2");
        JsonObjectUtils.addToJsonObject(targetJsonObj, "keyname", valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"key1\":\"\",\"keyname\":{\"key2\":\"value1\",\"key3\":\"value2\"}}", targetJsonObj.toString());
    }


    @Test
    void addToJsonObject_value_obj_has_some_childs_keyname_is_reserved() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put("key1", "");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key2", "value1");
        valueJsonObj.put("key3", "value2");
        JsonObjectUtils.addToJsonObject(targetJsonObj, ReservedInternJsonWords.KEY.name(), valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"key1\":\"\",\"KEY\":{\"key2\":\"value1\",\"key3\":\"value2\"}}", targetJsonObj.toString());
    }


    @Test
    void addToJsonObject_value_obj_has_some_childs_keyname_is_reserved_and_target_key_reserved() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put( ReservedInternJsonWords.KEY.name(), "");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key2", "value1");
        valueJsonObj.put("key3", "value2");
        JsonObjectUtils.addToJsonObject(targetJsonObj, ReservedInternJsonWords.KEY.name(), valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"KEY\":{\"key2\":\"value1\",\"key3\":\"value2\"}}", targetJsonObj.toString());
    }

    @Test
    void addToJsonObject_value_obj_has_some_childs_keyname_is_reserved_and_target_key_reserved_not_the_same() {


        JSONObject targetJsonObj = new JSONObject();
        targetJsonObj.put( ReservedInternJsonWords.CONTENT.name(), "");
        JSONObject valueJsonObj = new JSONObject();
        valueJsonObj.put("key2", "value1");
        valueJsonObj.put("key3", "value2");
        JsonObjectUtils.addToJsonObject(targetJsonObj, ReservedInternJsonWords.KEY.name(), valueJsonObj);

        /*
        {
          "key1": "value_of_parent",
          "keyname": {
            "key1": "value1"
          }
        }
         */
        assertEquals("{\"CONTENT\":\"\",\"KEY\":{\"key2\":\"value1\",\"key3\":\"value2\"}}", targetJsonObj.toString());
    }

/*
    --parent is null
    --addme is null
    key is null or empty
    addme keys is null
    addme keys is empty
    size>1
    size==2
    name and value
    only name
    only value
        else
    value is json

            size = 1
    keu is reserved
    key is not reserved and parent has already the same key
    key is unique , add the key with value to the parent
     */

    @Test
    void addJsonObjectToParentJsonObject_parent_is_null() {
        try {
            JsonObjectUtils.addJsonObjectToParentJsonObject(null, new JSONObject(), "key");
            assertTrue(false);
        }catch(Exception ex){

        }
    }

    @Test
    void addJsonObjectToParentJsonObject_addme_is_null() {
        try {
            JsonObjectUtils.addJsonObjectToParentJsonObject(new JSONObject(), null, "key");
            assertTrue(false);
        }catch(Exception ex){

        }
    }

    @Test
    void addJsonObjectToParentJsonObject_key_is_null() {
        JSONObject parent = new JSONObject();
        parent.put("parent_key", "");
        JSONObject addme = new JSONObject();
        addme.put("addme_key_1", "");
        JsonObjectUtils.addJsonObjectToParentJsonObject(parent, addme, null);

        assertEquals("{\"parent_key\":\"\",\"__content\":{\"addme_key_1\":null}}", parent.toString());
    }
    @Test
    void addJsonObjectToParentJsonObject_key_is_null_parent_reserved_key_parent_is_replaceable() {
        JSONObject parent = new JSONObject();
        parent.put(ReservedInternJsonWords.VAR.getLabel(), "");
        JSONObject addme = new JSONObject();
        addme.put("addme_key_1", "");
        JsonObjectUtils.addJsonObjectToParentJsonObject(parent, addme, null);

        assertEquals("{\"addme_key_1\":null}", parent.toString());
    }

    @Test
    void addJsonObjectToParentJsonObject_key_is_null_parent_reserved_key_parent_is_not_replaceable() {
        JSONObject parent = new JSONObject();
        parent.put(ReservedInternJsonWords.CONTENT.getLabel(), "");
        JSONObject addme = new JSONObject();
        addme.put("addme_key_1", "");
        JsonObjectUtils.addJsonObjectToParentJsonObject(parent, addme, null);

        assertEquals("{\"__content\":{\"addme_key_1\":null}}", parent.toString());
    }

    /*
    use cases:

    parent has reserved name and only one value , this sone has name of reserved with json beneath    ->
                reduce :
                    content -> single
                    single->single
                    content->content
    parent has reserved name and only one value , this value has name of non-reserved with json beneath
                reduce:
                    removed reserved , replace by non-reserved
    parent has non-reserved name and only one value , this value has name of reserved with json beneath
                reduce:
                     name wil be the   non-reserved name and value will be the json beneath
    parent has non-reserved name and only one value , this value has name of non-reserved with json beneath
                reduce:
                    only if the non-reserved names are the equal

    parent has name and > 1 values , this son has name of reserved with json beneath
                do nothing
    parent has name and > 1 values , this sone has name of non-reserved with json beneath
                do nothing

    */


    /*
     parent has reserved name and only one value , this sone has name of reserved with json beneath    ->
                reduce :
                    content -> single
                    single->single
                    content->content
     */
    @Test
    void reduceAdd_parent_one_value_reserved_name_sone_reserved_name_json_content_key() {
        JSONObject parent = new JSONObject();
        parent.put(ParserConsts.CONTENT, JSONObject.NULL);
        JSONObject son = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("a_key", "a_value");
        son.put(ParserConsts.KEY, value);
        JsonObjectUtils.reduceAdd(parent, son);
        String p = parent.toString();
        assertEquals("{\"__content\":{\"a_key\":\"a_value\"}}", parent.toString());

    }

    @Test
    void reduceAdd_parent_one_value_reserved_name_sone_reserved_name_json_key_content() {
        JSONObject parent = new JSONObject();
        parent.put(ParserConsts.KEY, JSONObject.NULL);
        JSONObject son = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("a_key", "a_value");
        son.put(ParserConsts.KEY, value);
        JsonObjectUtils.reduceAdd(parent, son);
        String p = parent.toString();
        assertEquals("{\"__key\":{\"a_key\":\"a_value\"}}", parent.toString());

    }

    @Test
    void reduceAdd_parent_one_value_reserved_name_sone_reserved_name_json_content_content() {
        JSONObject parent = new JSONObject();
        parent.put("parent_a", JSONObject.NULL);
        JSONObject son = new JSONObject();
        JSONObject value = new JSONObject();
        value.put("a_key", "a_value");
        son.put("parent_b", value);
        JsonObjectUtils.reduceAdd(parent, son);
        String p = parent.toString();
        System.out.println(p);
        assertEquals("{\"parent_a\":{\"a_key\":\"a_value\"}}", parent.toString());

    }
}