package com.intuit.library.eXtendGql.transformer;

import com.intuit.library.eXtendGql.gxpath.syntax.SyntaxBuilder;
import graphql.com.google.common.base.Preconditions;
import graphql.com.google.common.base.Strings;
import graphql.language.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformBuilder {

    public static final String DUPLICATE_TRANSFORM_ATTRIBUTE_KEY = "duplicate";
    public static final String NODE_NEW_NAME_VALUE = "NODE_NEW_NAME_VALUE";
    public static final String NODE_NEW_ALIAS_VALUE = "NODE_NEW_ALIAS_VALUE";
    //private GqlNodeContext gqlNodeContext = null;
    private List<TransformCommand> transformCommands = null;

    public List<TransformCommand> getTransformCommands() {
        return transformCommands;
    }

    public TransformBuilder() {

    }

   /* public TransformBuilder(GqlNodeContext gqlNodeContext) {
        this.gqlNodeContext = gqlNodeContext;
    }*/

    private void init(){
        if( transformCommands == null){
            transformCommands = new ArrayList<>();
        }
    }

    public TransformBuilder updateNodeName( String searchPath,String nodeNewNameValue){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nodeNewNameValue));

        init();

        Map<String, TransformCommandAttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(NODE_NEW_NAME_VALUE,
                new TransformCommandAttributeValue(nodeNewNameValue, String.class.getName()));

        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.UPDATE, searchPath);
        transformCommand.setAttributeValueMap(attributeValueMap);

        transformCommands.add(transformCommand);

        return this;
    }

    public TransformBuilder updateNodeAlias( String searchPath,String nodeNewAliasValue){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nodeNewAliasValue));

        init();

        Map<String, TransformCommandAttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(NODE_NEW_ALIAS_VALUE,
                new TransformCommandAttributeValue(nodeNewAliasValue, String.class.getName()));

        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.UPDATE, searchPath);
        transformCommand.setAttributeValueMap(attributeValueMap);

        transformCommands.add(transformCommand);

        return this;
    }

    public TransformBuilder updateNodeName( SyntaxBuilder syntaxBuilder,String nodeNewNameValue){
        Preconditions.checkArgument(syntaxBuilder!=null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nodeNewNameValue));

        init();

        Map<String, TransformCommandAttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(NODE_NEW_NAME_VALUE,
                new TransformCommandAttributeValue(nodeNewNameValue, String.class.getName()));

        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.UPDATE, syntaxBuilder);
        transformCommand.setAttributeValueMap(attributeValueMap);

        transformCommands.add(transformCommand);

        return this;
    }

    public TransformBuilder addChildrenNode(SyntaxBuilder syntaxBuilder, Node newNode){

        Preconditions.checkArgument(newNode!=null);

        init();
        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.ADD_CHILDREN, syntaxBuilder,newNode));

        return this;
    }

    public TransformBuilder addChildrenNode(String searchPath, Node newNode) {
        Preconditions.checkArgument(newNode!=null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));

        init();
        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.ADD_CHILDREN, searchPath,newNode));

        return this;
    }

    public TransformBuilder addSiblingNode(SyntaxBuilder syntaxBuilder, Node newNode){
        Preconditions.checkArgument(syntaxBuilder!=null);
        Preconditions.checkArgument(newNode!=null);

        init();
        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.ADD_SIBLING, syntaxBuilder, newNode));

        return this;
    }

    public TransformBuilder addSiblingNode(String searchPath, Node newNode) {
        Preconditions.checkArgument(newNode!=null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));

        init();
        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.ADD_SIBLING, searchPath,newNode));

        return this;
    }

    public TransformBuilder removeNode(SyntaxBuilder syntaxBuilder){
        Preconditions.checkArgument(syntaxBuilder!=null);

        init();

        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.REMOVE,syntaxBuilder));

        return this;
    }

    public TransformBuilder removeNode(String searchPath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));

        init();
        transformCommands.add(new TransformCommand(TRANSFORM_COMMAND_TYPE.REMOVE, searchPath,null));

        return this;
    }

    public TransformBuilder duplicateNode(SyntaxBuilder syntaxBuilder){
        Preconditions.checkArgument(syntaxBuilder!=null);

        init();

        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.DUPLICATE,syntaxBuilder);
        return getTransformBuilderIntern(TransformUtils.DEFAULT_DUPLICATION_NUMBER, transformCommand);
    }

    public TransformBuilder duplicateNode(String searchPath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));

        init();
        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.DUPLICATE,searchPath);
        return getTransformBuilderIntern(TransformUtils.DEFAULT_DUPLICATION_NUMBER, transformCommand);
    }

    private TransformBuilder getTransformBuilderIntern(int defaultDuplicationNumber, TransformCommand transformCommand) {
        Map<String, TransformCommandAttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(DUPLICATE_TRANSFORM_ATTRIBUTE_KEY,
                new TransformCommandAttributeValue(defaultDuplicationNumber,
                        Integer.class.getName()));
        transformCommand.setAttributeValueMap(attributeValueMap);
        transformCommands.add(transformCommand);

        return this;
    }

    public TransformBuilder duplicateNode(SyntaxBuilder syntaxBuilder, int duplicationNumber){
        Preconditions.checkArgument(syntaxBuilder!=null);

        Preconditions.checkArgument(duplicationNumber > 0 && duplicationNumber < Integer.MAX_VALUE);
        init();
        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.DUPLICATE, syntaxBuilder);
        return getTransformBuilderIntern(duplicationNumber, transformCommand);
    }

    public TransformBuilder duplicateNode(String searchPath, int duplicationNumber){
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchPath));

        Preconditions.checkArgument(duplicationNumber > 0 && duplicationNumber < Integer.MAX_VALUE);
        init();
        TransformCommand transformCommand = new TransformCommand(TRANSFORM_COMMAND_TYPE.DUPLICATE, searchPath);
        return getTransformBuilderIntern(duplicationNumber, transformCommand);
    }


}
