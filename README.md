# eXtendGql

## Summary
eXtendGql is a powerful library that offers a unique path selection solution for GraphQL, called gXPath. With this feature, developers can easily navigate the GraphQL data structure and select the information they need. In addition, the library includes an advanced transformation tool that allows for complex data manipulation and conversion. 

## Introduction

I embarked on a project that demanded the manipulation of GraphQL documents and the querying of a target service using the manipulated GraphQL.

To achieve this, I realized that I needed to use some of the techniques that I had previously used to deal with JSON and XML. 

The simplicity of JSONPath and xPath, along with complementary technologies like XSD, XSLT, and JavaScript, have contributed to the widespread use of XML and JSON over the years. These tools provide developers with extensive capabilities to select and manipulate JSON or XML documents.

GraphQL and JSON are two separate schema files that serve different purposes and are not always used for externalizing or retrieving service data. Unlike JSON, which is primarily used for data storage and exchange, GraphQL documents are used only for sending queries and mutations to the GraphQL service and not for any other purpose, such as data formats like in JSON or XML.

To modify a GraphQL document, the following steps need to be followed:

1. Traverse through the GraphQL document.
2. Identify the relevant GraphQL node or nodes.
3. Manipulate the GraphQL node or nodes as required.
4. Create a new GraphQL document with the manipulated node or nodes.
5. Pass the new GraphQL document to the GraphQL server to execute the query or mutation.

_Let's exclude point (5) from our discussion, as it can be accomplished through multiple tools and code snippets._

I found no open-source solution that could traverse the GraphQL document, select the relevant nodes, and manipulate the GraphQL document.

The article discusses the eXtendGql solution, which provides an intuitive solution for developers who need assistance with,
1. Traverse through the GraphQL document
2. Select nodes in the GraphQL document
3. Manipulate selected nodes in the GraphQL document

The article will,
- Describe the eXtendGql and abilities 
- Play with code
- Elaborate on use cases

##eXtendGql
**eXtendGql** is a Java-based library.

In order to use the library, you should mvn clean install, 

_Soon, the artifact will be available via artifactory_

eXtendGql leans on the [graphql-java](https://github.com/graphql-java/graphql-java) under MIT license, provide GraphQL java implementation, 

POM file

```
<dependency>
   <groupId>com.graphql-java</groupId>
   <artifactId>graphql-java</artifactId>
   <version>20.4</version>
</dependency>
```

## Traverse over GraphQL document

Every element in GraphQL, such as document, query, mutation, fragment, inline fragment, directive, etc., is derived from a node with unique attributes and behavior.

eXtendGql uses the [observer pattern](https://en.wikipedia.org/wiki/Observer_pattern) pattern where the GraphQL document acts as a subject, traverses the entire document and notifies relevant observers on the node and context.

This observer pattern separates the traversal over the GraphQL document from the execution part that the observer consumer code would like to perform.

## Selection of Node

XML has [XPath](https://en.wikipedia.org/wiki/XPath).
JSON has [JSONPath](https://github.com/json-path/JsonPath).

> And ... **GraphQL document has gXPath.**

gXPath can be used to navigate through nodes in a GraphQL document.
gXPath uses path expressions to select nodes or node on the GraphQL document.

_Behind the scenes, the gXPath utilizes the traversal module 
and selects the node according to the required expression._

### gXPath syntax
gXPath uses path expressions to select nodes in GraphQL document. The node is selected by following a path or steps. 

The most useful path expressions are listed below:

| Expression | Description   |                                                                                                                                                                                                                                                                                                                    
|---------|---------------------------------------|
| //      | Path prefix: Select all nodes from the root node, and use a slash as a separator between path elements.                                                                                                                                                                                                                                |
| /       | Path prefix: Select the first node from the root node and use a slash as a separator between path elements.  The range is not supported when the first node is selected.                                                                                                                                                                             |
| {x:y}/  | Path prefix, Select path node(s), between a range of x path node and y path node (inclusion), use of slash as a separator between path elements. x and y are positive integers. All nodes are selected if no x and y are not set.                                                                                              |
| {:y}//  | Path prefix, Select path node(s), between a range of first path node result to y, using a slash as a separator between path elements. x and y are positive integers. All nodes are selected if no x and y are not set.                                                                                                          |
| {x:}/   | Path prefix, Select path node(a), between range of x path node result to the end of path nodes result, use slash as a separator between path elements. x and y are integers. if no x and y are not set, select all path nodes.                                                                                                   |
| {:}//   | Path prefix, Select node(s) from the root node, use of slash as a separator between path elements.                                                                                                                                                                                                                                  | |
| ... | Support of relative path **"any"** selection e.g. {x:y}//a/b/.../f <br>  **any** can be set anywhere in the gXPath, except at the end of the gXPath, You can set many **any** as you request, this will help you while selecting node in large GraphQL structure, so you won't be required to mention/build the entire node structure. |

The library also provides an equivalent code named SyntaxPath that provides gXPath expression abilities use of code, mainly used by automation code.

##Transformer
The transformer provides the ability to transform (Manipulate) **GraphQL** document simply.
The transformer uses the abilities provided by eXtendGql such as: gXPath, SyntaxPath etc.

The eXtendGql provides the following transform methods:
1. Add Children - Add children node to selected **GraphQL** node or nodes
2. Add Sibling - Add sibling node to selected **GraphQL** node or nodes
3. Duplicate - Duplicate selected node by duplication number, multi nodes cannot be duplicated
4. Remove Children  - Remove selected nodes or node
5. Update name - Update selected nodes names or node names, for inline fragments, it will update the typeCondition name
6. Update alias value - update field alias value and fragment spread alias value.

## Code Play

> 
Let's start with traversal over the GrqphQL document

Start with creating my observer, let's name it: StringBuilderObserver

The observer will append the GraphQL node to some StringBuilder.

This way, we achieve separation of concern: 
- Traverse over the GraphQL document nodes
- Append node values with StringBuilder ....


```java
public class StringBuilderObserver implements TraversalObserver {
    private final List<StringBuilderElem> stringBuilderElems = new ArrayList<>();

    private final boolean isIgnoreCollection = true;
    @Override
    public void updateNodeEntry(Node node, Node parentNode, Context context, ObserverAction observerAction) {

        String  message = "";
        DocumentElementType documentElementType = context.getDocumentElementType();
        switch (documentElementType) {

            case DOCUMENT:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Document", documentElementType.name());

                break;

            case DIRECTIVE:
                message = MessageFormat.format("Name : {0} ||  Type : {1}", ((Directive) node).getName(), documentElementType.name());
                break;
            case FIELD:
                Field field = (Field) node;
                message = MessageFormat.format("Name : {0} || Alias : {1} ||  Type : {2}",
                        field.getName(),
                        field.getAlias(),
                        documentElementType.name());
                break;
            case OPERATION_DEFINITION:
                message = MessageFormat.format("Name : {0} ||  Type : {1}",
                        ((OperationDefinition) node).getOperation().toString(), documentElementType.name());
                break;
            case INLINE_FRAGMENT:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "InlineFragment",
                        documentElementType.name());

                break;
            case FRAGMENT_DEFINITION:
                message = MessageFormat.format("Name : {0} ||  Type : {1}",
                        ((FragmentDefinition) node).getName(), documentElementType.name());

                break;
            case FRAGMENT_SPREAD:
                message = MessageFormat.format("Node : {0} ||  Type : {1}", ((FragmentSpread) node).getName(), documentElementType.name());
                break;
            case VARIABLE_DEFINITION:
                message = MessageFormat.format("Name : {0} || Default Value : {1} ||  Type : {2}",
                        ((VariableDefinition) node).getName(), ((VariableDefinition) node).getDefaultValue(), documentElementType.name());

                break;
            case ARGUMENT:
                message = MessageFormat.format("Name : {0} || Value : {1} ||  Type : {2}",
                        ((Argument) node).getName(),
                        ((Argument) node).getValue(),
                        documentElementType.name());
                break;
            case ARGUMENTS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Arguments", documentElementType.name());
                break;
            case SELECTION_SET:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "SelectionSet", documentElementType.name());
                break;
            case VARIABLE_DEFINITIONS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "VariableDefinitions", documentElementType.name());
                break;
            case DIRECTIVES:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Directives", documentElementType.name());
                break;
            case DEFINITIONS:
                if(isIgnoreCollection) return;
                message = MessageFormat.format("Node : {0} ||  Type : {1}", "Definitions", documentElementType.name());

                break;
        }

        if(Strings.isNullOrEmpty(message)){
            return;
        }

        stringBuilderElems.add(new StringBuilderElem(message, context.getLevel()));

        levels.add(context.getLevel());
        //spaces++;
    }
    private final List<Integer> levels = new ArrayList<>();

    public String getGqlBrowsedPrintedString() {
        return getStringAs(true);
    }

    private String getStringAs(boolean isIdent) {
        int j=0;
        StringBuilder stringBuilder = new StringBuilder();
        for (StringBuilderElem stringBuilderElem : stringBuilderElems) {
            j++;
            String spaceStr = "";
            if( isIdent) {
                for (int i = 0; i < stringBuilderElem.getDepth(); i++) {
                    spaceStr += " ";
                }
                stringBuilder.append(spaceStr + stringBuilderElem.getName() + "\n");
            }else{
                stringBuilder.append( stringBuilderElem.getName() + (j+1<stringBuilderElems.size()? " " : "") );
            }
        }
        return stringBuilder.toString();
    }

    public String getGqlBrowsedString(){
        return getStringAs(false);
    }

//    int spaces = 0;
    @Override
    public void updateNodeExit( Node node,Node parentNode, Context context, ObserverAction observerAction) {
    }
}
```

```java

GqlTraversal traversal = new GqlTraversal();

StringBuilderObserver gqlStringBuilderObserver = new StringBuilderObserver();

traversal.getGqlTraversalObservable().addObserver(gqlStringBuilderObserver);
traversal.traverse(file);

System.out.println( gqlStringBuilderObserver.getGqlBrowsedString());
```


After we saw how the traversal is working, lets digg with some example of **gXPath selection GraphQL nodes**

gXPath define an expression language as defined above, in addition the expression language contains more terms to familiar with, _Element Name_ and types _abbreviations_.
Why its important, GraphQL document is more than structure that similar to JSON, GraphQL also provide a DSL that exposed by the GraphQL server and GraphQL language.
the types and the element name will assist the gXPath to select the exact node or nodes.

<u>Element Names</u>

|element_name | Description                           |
|--------------|---------------------------------------|
| type=        | Select element by type abbreviate     |                                                                                                                                                                                                 |
| name=        | Select element by name                |                                                                                                                                                                                                 |
| alias=       | Select element by alias name          |


<u>Available types and abbreviation</u>

| Type abbreviate | Description          |
|-----------------|----------------------|
| doc             | DOCUMENT             |
| frag            | FRAGMENT_DEFINITION  |
| direc           | DIRECTIVE            |
| fld             | FIELD                |
| mutation        | MUTATION_DEFINITION  |
| query           | OPERATION_DEFINITION |
| infrag          | INLINE_FRAGMENT      |
| var             | VARIABLE_DEFINITION  |
| arg             | ARGUMENT             | 


> Let's practice the gXPath expression,

__GraphQL Document__

In this document, we have 2 node named: 'name', but of different types: argument and field.

```text
query {
    Instrument(Name: "1234") {
        Reference {
            Name
            title
        }
    }
}
```

- Select all nodes (double slash) 'name' which is an argument type `//query/Instrument/name[type=arg]`

- Select first node (single slash) 'name' which is an argument type `/query/Instrument/name[type=arg]`

- `//query/.../name[type=arg]` Same as query `//query/Instrument/name[type=arg]`  
      
- Select of 'name' node which is field under Reference, reside under Instrument, reside under query `//query/Instrument/Reference/name`

- `//query/Instrument/.../name` Same as `//query/Instrument/Reference/name`  
    
- `//.../name` Same as `//query/Instrument/Reference/name`


__GraphQl Document__
```text
query Hero($episode: Episode, $withFriends: Boolean!) {
  hero(episode: $episode) {
    name
    friends @include(if: $withFriends) {
      name
    }
    friends @include(if: $withFriends) {
      name
    }
    friends @include(if: $withFriends) {
      name
    }
    friends @include(if: $withFriends) {
      name
    }
  }
}
```

- Select all query nodes named hero `//query[name=hero]` 

- Select first query (single slash) node named hero `/query[name=hero]`     

- Select all nodes named '_name_', reside under _friends_ node `//query[name=hero]/hero/friends/name` 

- Select all nodes named '_name_' within a range of index 0 and index 2 (inclusion), reside under friends node `{0:2}//query[name=hero]/hero/friends/name`   

- Select node named '_name_', reside under any node, which reside under _hero_ node `/query[name=hero]/hero/.../name ` 

- Select _$withFriends_ variable reside directly under the root node named _hero_ `//query[name=hero]/withFriends[type=var]`

- Select _include_ directive first node reside under friends, which reside under root query node named _hero_ `/query[name=hero]/hero/friends/include[type=direc] `             

- Select the '_if_' argument node, reside under the _@include_ directive `//.../include[type=direc]/if[type=arg]`  

- Select episode variable `//.../episode[type=var]`                                         


How to use gXPath in the code:

```java

SelectorFacade selectorFacade = new SelectorFacade();

String queryString = Files.readString(file.toPath());

// query {  Instrument(id: "1234") }
GqlNodeContext select = selectorFacade.select(queryString, "//query/Instrument /   Reference  /");
```

__Use of SyntaxPath__

```java
String queryString = Files.readString(file.toPath());

SyntaxBuilder eXtendGqlBuilder = new SyntaxBuilder();

eXtendGqlBuilder.appendQuery();
eXtendGqlBuilder.appendField("Instrument");
eXtendGqlBuilder.appendField("Reference");

// query {  Instrument(id: "1234") }
GqlNodeContext select = selectorFacade.select(queryString, eXtendGqlBuilder.build());
```


And finally we will dwell on an example that will illustrate the use of gXPath node selection GraphQL manipulation (AKA transform).

> Here a mutation GraphQL document

```
mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
  createReview(episode: $ep, review: $review) {
    stars
    commentary
    stars
    commentary
  }
}
```

The following java code will select and transform mutation document,

```java
String queryString = Files.readString(sourceFile.toPath());
        
TransformBuilder transformBuilder = new TransformBuilder();
transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("child_of_stars"))
        .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview/stars",new Field("sibling_of_stars"))
        .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary")
        .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/sibling_of_stars", 10);

TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);

RawPayload rawPayload = new RawPayload();
rawPayload.setQueryValue(queryString);

RawPayload executeRawPayload = transformExecutor.execute(rawPayload);
```

> **Description**:

> 
<u>add new children node</u> named: _child_of_stars_ under gXPath: `//mutation[name=CreateReviewForEpisode]/createReview/stars`

> 
<u>Add new sibling node</u> named: _sibling_of_stars_ under gXPath selected node: `//mutation[name=CreateReviewForEpisode]/createReview/stars`

> 
<u>Set new node name</u>: _star_new_name_ value to the selected node by gXPath: 

`//mutation[name=CreateReviewForEpisode]/createReview/stars`
 

> 
<u>Remove selected node by gXPath</u> `//mutation[name=CreateReviewForEpisode]/createReview/commentary`


> 
<u>Duplicate selected gXPath node 10 times</u> `//mutation[name=CreateReviewForEpisode]/createReview/sibling_of_stars`


Use TransformBuilder to build the transform plan, with selected node use of gXPath and the command to execute.
The transform plan is load to the TransformExecutor, with the GraphQL payload.

The execution will result in a new GraphQL document,

```
mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
  createReview(episode: $ep, review: $review) {
    sibling_of_stars
    star_new_name {
      child_of_stars
    }
    star_new_name {
      child_of_stars
    }
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
    sibling_of_stars
  }
}
```


Last example, will demonstrate the manipulation of directive from _include_ to _exclude_

Here the query GraphQL document,

```
query Hero($episode: Episode, $withFriends: Boolean!) {
  hero(episode: $episode) {
    name
    friends **@include**(if: $withFriends) {
      name
    }
  }
}

```

Here the code:

```java
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
```

```

query Hero($episode: Episode, $withFriends: Boolean!) {
  hero(episode: $episode) {
    name
    friends @exclude(if: $withFriends) {
      name
    }
  }
}
```

## eXtendGql Use Cases
The eXtendGql can be used during base code while the developer required to enrich GraphQL document with more fields, while querying server for data, or during manipulation of data in server, so the code can articulate the relevant fields to manipulate in the service side.
The eXtendGql can also be utilize during integration or E2E testing, generating of synthetic GraphQL data, result with high velocity and managed solution.

I elaborate the following use cases with more details:

> Synthetic GraphQL document creation
Testing, mostly integration testing part will demands the ability to query GraphQL server with different queries and mutations.

Of course, the developer can maintain large list of example files to send to the server or to find and replace the relevant string in GraphQL document, but it is a cumbersome solution, hard to maintain etc.

The ability to manipulate the query or the mutation with ease way, use configuration that enlist the plan [gXPath, Transform Commands and Argument to execute] and execute the planץ

```
Configuration
  Plan
    steps
      step 1
         gXPath (String)
         transform_commands
           transform_command
               command
               argument_object_definition
      step n
         gXPath (String)
         transform_commands
           transform_command
               command
               argument_object_definition
  origin_file_to_manipulate

Configuration config = read_configuration_plan(plan_file);

config_verification()

build_plan -> ... use of TransformBuilder

new_graphql_document = execute_plan -> ... use of TransformExecuter
```
 
Versatility and an extremely high ability to produce synthetic GraphQL data and a high ability to verify the integrity of a GraphQL service.

> Articulate GraphQL document on-the-fly
Sometime you required to build query or mutation upon configuration on the fly or upon business logic, and send it to the GraphQL server, The **eXtendGql** library will assist you while doing it, The **eXtendGql** library does not support creation (only manipulation) of the skeleton file and will not support (as for now).
The developer can create the skeleton GraphQL file, store the file in resource folder.
_skeleton file, means file with structure but without field only._
And with relevant plan use of syntaxPath we can assemble the gXpath and set the plan strategy on the fly, use of TransformBuilder.
And then use of TransformExecutor to run the plan.








