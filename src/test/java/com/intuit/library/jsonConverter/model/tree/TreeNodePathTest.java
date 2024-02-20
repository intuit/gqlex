package com.intuit.library.jsonConverter.model.tree;

import com.intuit.library.jsonConverter.model.FieldNameDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodePathTest {
    @Test
    void verify() {

        TreeNodePath treeNodePath = new TreeNodePath();
        treeNodePath.setNode(new TreeNode("key_1","value_1"));
        List<FieldNameDescriptor> fieldNameDescriptors = new ArrayList<>();
        fieldNameDescriptors.add(new FieldNameDescriptor("name_1","alias_1"));
        fieldNameDescriptors.add(new FieldNameDescriptor("name_2",null));
        fieldNameDescriptors.add(new FieldNameDescriptor("name_3","alias_3"));
        treeNodePath.setPath(fieldNameDescriptors.toArray(new FieldNameDescriptor[0]));

        String canonicalPath = treeNodePath.getCanonicalPath();

        assertNotNull(canonicalPath);

        assertTrue(!canonicalPath.isEmpty());
    }

    @Test
    void verify_path_null() {

        TreeNodePath treeNodePath = new TreeNodePath();
        treeNodePath.setNode(new TreeNode("key_1","value_1"));
        List<FieldNameDescriptor> fieldNameDescriptors = new ArrayList<>();
        treeNodePath.setPath(fieldNameDescriptors.toArray(new FieldNameDescriptor[0]));

        String canonicalPath = treeNodePath.getCanonicalPath();

        assertNull(canonicalPath);
        assertNotNull(treeNodePath.toString());
    }
}