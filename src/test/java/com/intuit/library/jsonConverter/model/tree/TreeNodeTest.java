package com.intuit.library.jsonConverter.model.tree;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeTest {

    /*
    input: {
      aaa: {
        bbb: [
          { name: "a1", value: "2" }
          { name: "a2", value: "" }
          { b3: "a3", value: "v1" }
        ]
      }
      ccc: {
        ddd: [
          { e4: "reconcileStatus", value: "2" }
          { e5: "payeeId", value: "k9" }
          { name: "docNumOrMemo", value: "" }
        ]
      }
    }
     */

    @Test
    void toJson_מ_layers_contain_next_layer_no_value_in_elemn() {
        List<TreeNode> treeNodeList = new ArrayList<>();
        int numOfElement = 10;
        for (int i = 1; i <= numOfElement; i++) {
            treeNodeList.add(buildTreeNode(String.valueOf(i),true));
        }

        for (int i = 0; i < numOfElement; i++) {
            if( i < numOfElement-1)
                treeNodeList.get(i).addTreeNodeChild(treeNodeList.get(i+1));
        }

        JSONObject json = treeNodeList.get(0).toJson();

        System.out.println(json.toString());

        assertEquals("{\"1_key\":{\"2_key\":{\"3_key\":{\"4_key\":{\"5_key\":{\"6_key\":{\"7_key\":{\"8_key\":{\"9_key\":{\"10_key\":null}}}}}}}}}}", json.toString());
    }
    @Test
    void toJson_2_layers_with_arrays() {

        TreeNode inputNode = new TreeNode("input", null);
        TreeNode aaaNode = new TreeNode("aaa", "    ");
        TreeNode bbbNode =new TreeNode("bbb", null);
        aaaNode.addTreeNodeChild(bbbNode);

        inputNode.addTreeNodeChild(aaaNode);
        bbbNode.addTreeNodeChild(new TreeNode("a1","2"));
        bbbNode.addTreeNodeChild(new TreeNode("a2",""));
        bbbNode.addTreeNodeChild(new TreeNode("a3","v1"));

        TreeNode cccNode = new TreeNode("ccc", null);
        //bbbNode.addChildObjs(cccNode);
        inputNode.addTreeNodeChild(cccNode);

        cccNode.addTreeNodeChild(new TreeNode("reconcileStatus","2"));
        cccNode.addTreeNodeChild(new TreeNode("payeeId",""));
        cccNode.addTreeNodeChild(new TreeNode("docNumOrMemo","v1"));

        JSONObject json = inputNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"input\":{\"__elem_2\":{\"ccc\":{\"docNumOrMemo\":\"v1\",\"payeeId\":\"\",\"reconcileStatus\":\"2\"}},\"__elem_1\":{\"aaa\":\"    \",\"__content\":{\"bbb\":{\"a1\":\"2\",\"a2\":\"\",\"a3\":\"v1\"}}}}}", json.toString());
    }

    @Test
    void toJson_simple_1_layer_null() {

        TreeNode treeNode = new TreeNode(null, null);
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() ==0 );


        assertEquals("{}", json.toString());
    }

    @Test
    void toJson_simple_1_layer() {

        TreeNode treeNode = buildTreeNode("a");
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals(treeNode.getValue(), actualValue);
    }

    //{"a_key": {"b_key": "b_value"}}
    @Test
    void toJson_simple_2_layer_no_value_inline_child_instead_of_value() {

        TreeNode treeNode = buildTreeNode("a",true);
        treeNode.addTreeNodeChild( buildTreeNode("b") );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a_key\":{\"b_key\":\"b_value\"}}", json.toString());
    }

    /*
    {
    "a_key": "a_value",
    "__content": {"__elem": {"b_key": "b_value"}}
}
     */
    @Test
    void toJson_simple_2_layer() {

        TreeNode treeNode = buildTreeNode("a");
        treeNode.addTreeNodeChild( buildTreeNode("b") );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals(treeNode.getValue(), actualValue);

        assertEquals("{\"a_key\":\"a_value\",\"__content\":{\"b_key\":\"b_value\"}}",
                json.toString());
    }

    @Test
    void verify_tree_node_child_is_treenode() {

        TreeNode treeNode = new TreeNode();
        treeNode.setKey("key1");
        treeNode.setValue(new TreeNode("key_2","value_2"));

        assertNotNull(treeNode.getChildNodes());

        assertNotNull(treeNode.getFieldNameDescriptor());

    }
    @Test
    void verify_tree_node_empty() {

        TreeNode treeNode = new TreeNode();
        treeNode.setKey("key1");
        treeNode.setValue("value1");

        assertNull(treeNode.getChildNodes());

        assertNotNull(treeNode.getFieldNameDescriptor());

    }    /*
    {
    "a_key": "a_value",
    "__content": {
        "__elem_2": {"b2_key": "b2_value"},
        "__elem_1": {"b1_key": "b1_value"}
    }
}
     */
    @Test
    void toJson_simple_2_layer_2_childs() {

        TreeNode treeNode = buildTreeNode("a");
        treeNode.addTreeNodeChild( buildTreeNode("b1") );
        treeNode.addTreeNodeChild( buildTreeNode("b2") );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals(treeNode.getValue(), actualValue);

        assertNotNull(treeNode.getChildNodes() );

        TreeNode treeNode1 = new TreeNode();
        treeNode1.shallowCopy(treeNode);

        JSONObject json1 = treeNode1.toJson();
        assertNotNull(json1);
        assertEquals("{\"a_key\":\"a_value\",\"__content\":{\"b1_key\":\"b1_value\",\"b2_key\":\"b2_value\"}}", json1.toString());
    }


    /*
    {
    "a_key": "",
    "__content": {
        "__elem_2": {"b2_key": ""},
        "__elem_1": {"b1_key": ""}
    }
}
     */
    @Test
    void toJson_simple_2_layer_2_childs_all_values_are_null() {

        TreeNode treeNode = buildTreeNode("a",true);
        treeNode.addTreeNodeChild( buildTreeNode("b1",true) );
        treeNode.addTreeNodeChild( buildTreeNode("b2",true) );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a_key\":{\"b1_key\":null,\"b2_key\":null}}", json.toString());
    }


    @Test
    void toJson_simple_2_layer_2_childs_some_values_null_some_not() {

        TreeNode treeNode = buildTreeNode("a",true);
        treeNode.addTreeNodeChild( buildTreeNode("b1") );
        treeNode.addTreeNodeChild( buildTreeNode("b2",true) );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a_key\":{\"b1_key\":\"b1_value\",\"b2_key\":null}}", json.toString());
    }

/*
{
  "__content": {
    "__elem": {
      "__content": {
        "__elem": {
          "c1_key": "c1_value"
        }
      },
      "b1_key": "b1_value"
    }
  },
  "a1_key": "a1_value"
}
 */
    @Test
    void toJson_nested_layers() {

        TreeNode a1 = buildTreeNode("a1");
        TreeNode b1 = buildTreeNode("b1");
        a1.addTreeNodeChild(b1);
        b1.addTreeNodeChild( buildTreeNode("c1"));
        JSONObject json = a1.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a1_key\":\"a1_value\",\"__content\":{\"b1_key\":\"b1_value\",\"__content\":{\"c1_key\":\"c1_value\"}}}", json.toString());
    }

    /*
    {"a1_key": {"b1_key": {"c1_key": ""}}}
     */
    @Test
    void toJson_nested_layers_with_all_values_are_null() {

        TreeNode a1 = buildTreeNode("a1",true);
        TreeNode b1 = buildTreeNode("b1", true);
        a1.addTreeNodeChild(b1);
        b1.addTreeNodeChild( buildTreeNode("c1", true));
        JSONObject json = a1.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a1_key\":{\"b1_key\":{\"c1_key\":null}}}", json.toString());
    }


    /*
    {
    "__content": {
        "__elem_2": {"b2_key": ""},
        "__elem_1": {
            "__content": {
                "__elem_2": {"c2_key": ""},
                "__elem_1": {"c1_key": ""}
            },
            "b1_key": ""
        }
    },
    "a1_key": ""
}
     */
    @Test
    void toJson_nested_layers_with_all_values_are_null_2_child_in_each_layer() {

        TreeNode a1 = buildTreeNode("a1",true);
        TreeNode b1 = buildTreeNode("b1", true);
        TreeNode b2 = buildTreeNode("b2", true);
        a1.addTreeNodeChild(b1);
        a1.addTreeNodeChild(b2);
        b1.addTreeNodeChild( buildTreeNode("c1", true));
        b1.addTreeNodeChild( buildTreeNode("c2", true));
        JSONObject json = a1.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a1_key\":{\"b2_key\":null,\"__elem_1\":{\"b1_key\":{\"c1_key\":null,\"c2_key\":null}}}}", json.toString());
    }

/*
{
    "__content": {
        "__elem_2": {"b2_key": "b2_value"},
        "__elem_1": {
            "__content": {
                "__elem_2": {"c2_key": "c2_value"},
                "__elem_1": {"c1_key": "c1_value"}
            },
            "b1_key": "b1_value"
        }
    },
    "a1_key": "a1_value"
}
 */
    @Test
    void toJson_nested_layers_with_all_values_are_NOT_null_2_child_in_each_layer() {

        TreeNode a1 = buildTreeNode("a1");
        TreeNode b1 = buildTreeNode("b1");
        TreeNode b2 = buildTreeNode("b2");
        a1.addTreeNodeChild(b1);
        a1.addTreeNodeChild(b2);
        b1.addTreeNodeChild( buildTreeNode("c1"));
        b1.addTreeNodeChild( buildTreeNode("c2"));
        JSONObject json = a1.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a1_key\":\"a1_value\",\"__content\":{\"b2_key\":\"b2_value\",\"__elem_1\":{\"b1_key\":\"b1_value\",\"__content\":{\"c1_key\":\"c1_value\",\"c2_key\":\"c2_value\"}}}}", json.toString());
    }

    @Test
    void toJson_nested_layers_check_circular_prevention() {

        TreeNode a1 = buildTreeNode("a1");
        TreeNode b1 = buildTreeNode("b1");
        TreeNode b2 = buildTreeNode("b2");
        a1.addTreeNodeChild(b1);
        a1.addTreeNodeChild(b2);
        b1.addTreeNodeChild( a1);
        JSONObject json = a1.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a1_key\":\"a1_value\",\"__content\":{\"__elem_1\":{\"b1_key\":\"b1_value\",\"__content\":{\"a1_key\":\"a1_value\",\"__content\":{\"b2_key\":\"b2_value\"}}}}}", json.toString());
    }
/*
{
    "a_key": "",
    "__content": {
        "__elem_2": {"b2_key": "b2_value"},
        "__elem_1": {"b1_key": "b1_value"}
    }
}
 */
    @Test
    void toJson_simple_2_layer_2_childs_null_value_childs_should_not_inline() {

        TreeNode treeNode = buildTreeNode("a", true);
        treeNode.addTreeNodeChild( buildTreeNode("b1") );
        treeNode.addTreeNodeChild( buildTreeNode("b2") );
        JSONObject json = treeNode.toJson();

        assertNotNull(json);

        System.out.println(json.toString(4));

        assertTrue(json.keySet().size() >0 );
        String next = json.keys().next();
        assertNotNull(next);

        Object actualValue = json.get(next);
        assertNotNull(actualValue);

        assertEquals("{\"a_key\":{\"b1_key\":\"b1_value\",\"b2_key\":\"b2_value\"}}", json.toString());
    }

    private static TreeNode buildTreeNode(String prefix) {
        return new TreeNode(prefix + "_key", prefix + "_value");
    }

    private static TreeNode buildTreeNode(String prefix, boolean isValueSetToNull) {
        if(isValueSetToNull){
            return new TreeNode(prefix + "_key", null);
        }else{
            return buildTreeNode(prefix);
        }
    }
}