package com.intuit.library.eXtendGql.gxpath.syntax;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class eXtendGqlBuilderTest {
    @Test
    void appendQuery_2_start_elem() {
    Exception exception = assertThrows(Exception.class, () -> {
        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendQuery();
        eXtendGqlBuilder.appendMutation();     });
    }

    @Test
    void appendQuery_query_with_field() {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        try {
            eXtendGqlBuilder.appendQuery(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");

        assertEquals("//query/a/b",eXtendGqlBuilder.build().toString());
    }

    @Test
    void appendQuery_query_with_n_field() {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        try {
            eXtendGqlBuilder.appendMutation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String expe = "";
        for (int i = 0; i < 100; i++) {
            String name = "a" + i + 1;
            eXtendGqlBuilder.appendField(name);
            expe +="/"+name;
        }

        assertEquals("/mutation"+expe,eXtendGqlBuilder.build().toString());
    }

    @Test
    void appendQuery_query_with_n_field_type() {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

        try {
            eXtendGqlBuilder.appendMutation(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String expe = "";
        for (int i = 0; i < 100; i++) {
            int j = i+1;
            String name = "a" +j;
            eXtendGqlBuilder.appendField(name, "key" +j, "value"+j);
            expe +="/"+name + "[" + "key"+j + "=" + "value"+j + "]";
        }

        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);
        assertEquals("//mutation"+expe, str);
    }

    @Test
    void appendQuery_query_field_direc_multi_select() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation(true);
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendDirective("c_dir",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("//mutation/a/b/c/c_dir[type=direc]", str);
    }

    @Test
    void appendQuery_query_field_direc_single_select() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation();
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendDirective("c_dir",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("/mutation/a/b/c/c_dir[type=direc]", str);
    }

    @Test
    void appendQuery_query_field_arg() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation();
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendArgument("c_dir",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("/mutation/a/b/c/c_dir[type=arg]", str);
    }

    @Test
    void appendQuery_query_field_frg() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation();
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("/mutation/a/b/c/c_frg[type=frag]", str);
    }

    @Test
    void appendQuery_query_field_frg_wtih_range_start_end() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendRange(2,10);
        eXtendGqlBuilder.appendMutation(true);
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("{2:10}//mutation/a/b/c/c_frg[type=frag]", str);
    }

    @Test
    void appendQuery_query_field_frg_wtih_range_start() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendRangeByStart(4);
        eXtendGqlBuilder.appendMutation(true);
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("{4:}//mutation/a/b/c/c_frg[type=frag]", str);
    }

    @Test
    void appendQuery_query_field_frg_wtih_range_end() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendRangeByEnd(6);
        eXtendGqlBuilder.appendMutation(true);
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("{0:6}//mutation/a/b/c/c_frg[type=frag]", str);
    }

    @Test
    void appendQuery_query_field_inline_frg() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation(true);
        eXtendGqlBuilder.appendField("a");
        eXtendGqlBuilder.appendField("b");
        eXtendGqlBuilder.appendField("c");
        eXtendGqlBuilder.appendInlineFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("//mutation/a/b/c/c_frg[type=infrag]", str);
    }


    @Test
    void appendQuery_query_field_inline_frg_middle_attriibute() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation();
        eXtendGqlBuilder.appendField("a");

        Map<String,String> stringMap = new HashMap<>();
        stringMap.put("att1", "attval1");
        stringMap.put("att2", "attval2");
        eXtendGqlBuilder.appendField("b",stringMap);
        eXtendGqlBuilder.appendField("c",stringMap);
        eXtendGqlBuilder.appendInlineFragment("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();
        System.out.println(str);

        assertEquals("/mutation/a/b[att2=attval2 att1=attval1]/c[att2=attval2 att1=attval1]/c_frg[type=infrag]", str);
    }

    @Test
    void check_null() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        SyntaxPath build = eXtendGqlBuilder.build();
        assertNull(build);

    }

    @Test
    void check_append() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation(true);

        Map<String,String> stringMap = new HashMap<>();
        stringMap.put("att1", "attval1");
        stringMap.put("att2", "attval2");
        eXtendGqlBuilder.append(new SyntaxPathElement("a", stringMap));
        SyntaxPathElement syntaxPathElement = new SyntaxPathElement();
        syntaxPathElement.setName("GG");
        syntaxPathElement.addAttribute("key5","value55");
        eXtendGqlBuilder.append(syntaxPathElement);
        eXtendGqlBuilder.appendField("c",stringMap);
        eXtendGqlBuilder.appendFragmentSpread("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();

        System.out.println(str);

        assertEquals("//mutation/a[att2=attval2 att1=attval1]/GG[key5=value55]/c[att2=attval2 att1=attval1]/c_frg[type=fragsprd]", str);
    }

    @Test
    void appendQuery_query_field_inline_frg_all_with_attriibute() throws Exception {

        SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();
        eXtendGqlBuilder.appendMutation();

        Map<String,String> stringMap = new HashMap<>();
        stringMap.put("att1", "attval1");
        stringMap.put("att2", "attval2");
        eXtendGqlBuilder.appendField("a",stringMap);
        eXtendGqlBuilder.appendField("b",stringMap);
        eXtendGqlBuilder.appendField("c",stringMap);
        eXtendGqlBuilder.appendFragmentSpread("c_frg",null);
        String str = eXtendGqlBuilder.build().toString();

        System.out.println(str);

        assertEquals("/mutation/a[att2=attval2 att1=attval1]/b[att2=attval2 att1=attval1]/c[att2=attval2 att1=attval1]/c_frg[type=fragsprd]", str);
    }
}