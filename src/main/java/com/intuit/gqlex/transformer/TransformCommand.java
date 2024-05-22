package com.intuit.gqlex.transformer;

import com.intuit.gqlex.gqlxpath.syntax.SyntaxBuilder;
import graphql.language.Node;

import java.util.Map;

public class TransformCommand {
    private String searchPath;

    private TRANSFORM_COMMAND_TYPE transformCommandType;
    private Node targetNode;

    private SyntaxBuilder syntaxBuilder;



    public String getSearchPath() {
        return searchPath;
    }

    private Map<String, TransformCommandAttributeValue> attributeValueMap;

    public Map<String, TransformCommandAttributeValue> getAttributeValueMap() {
        return attributeValueMap;
    }

    public void setAttributeValueMap(Map<String, TransformCommandAttributeValue> attributeValueMap) {
        this.attributeValueMap = attributeValueMap;
    }

    public TRANSFORM_COMMAND_TYPE getTransformCommandType() {
        return transformCommandType;
    }

    public void setTransformCommandType(TRANSFORM_COMMAND_TYPE transformCommandType) {
        this.transformCommandType = transformCommandType;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
    public TransformCommand(TRANSFORM_COMMAND_TYPE transformCommandType,
                            SyntaxBuilder syntaxBuilder) {
        this(transformCommandType, syntaxBuilder, null);

    }

    public TransformCommand(TRANSFORM_COMMAND_TYPE transformCommandType, String searchPath) {
        this.transformCommandType = transformCommandType;
        this.searchPath = searchPath;
    }
    public TransformCommand(TRANSFORM_COMMAND_TYPE transformCommandType,
                            SyntaxBuilder syntaxBuilder,
                            Node targetNode) {
        this.transformCommandType = transformCommandType;
        this.syntaxBuilder = syntaxBuilder;
        this.targetNode = targetNode;
    }

    public TransformCommand(TRANSFORM_COMMAND_TYPE transformCommandType,
                            String searchPath,
                            Node targetNode) {
        this.transformCommandType = transformCommandType;
        this.searchPath = searchPath;
        this.targetNode = targetNode;
    }

    public SyntaxBuilder getSyntaxBuilder() {
        return syntaxBuilder;
    }

    public void setSyntaxBuilder(SyntaxBuilder syntaxBuilder) {
        this.syntaxBuilder = syntaxBuilder;
    }
}
