# What is Transformer?
The transformer provide the ability to transform **GraphQL** document, in a simple manner.
<br>The transformer use of the abilities provided by eXtendGql such as: gXPath, SyntaxPath etc.

The following transform methods provided by the eXtendGql:
1. Add Children - Add children node to selected **GraphQL** node or nodes
2. Add Sibling - Add sibling node to selected **GraphQL** node or nodes
3. Duplicate - Duplicate selected node by duplication number, multi nodes cannot be duplicated
4. Remove Children  - Remove selected node or nodes
5. Update name - Update selected node name or nodes names, for inline fragment it will update the typeCondition name
6. Update alias value - update field alias value, fragment spread alias value.

## gXPath

# How to use Transformer in code
1. Use of TransformBuilder builder
2. Append for each operation [addSiblingNode, removeNode,updateNodeName ... ], the selection path (or the syntaxPath) <br>
3. execute the `transformExecutor.execute`
4. Retrieve the transformedRawPayload payload

```java

TransformBuilder transformBuilder = new TransformBuilder();

transformBuilder.addChildrenNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name1"))
        .addSiblingNode("//mutation[name=CreateReviewForEpisode]/createReview",new Field("new_name2"))
        .removeNode("//mutation[name=CreateReviewForEpisode]/createReview/commentary",new Field("new_name2"))
        .updateNodeName("//mutation[name=CreateReviewForEpisode]/createReview/stars","star_new_name")
        .duplicateNode("//mutation[name=CreateReviewForEpisode]/createReview/new_name1", 10);

TransformExecutor transformExecutor = new TransformExecutor(transformBuilder);
        
RawPayload rawPayload = new RawPayload();
rawPayload.setQueryValue(queryString);
RawPayload transformedRawPayload = transformExecutor.execute(rawPayload);
```

## Example

Here a GraphQL **before** the manipulation:
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
Here the manipulation code: 
1. Select path nodes '//mutation[name=CreateReviewForEpisode]/createReview/stars', Add new child "child_of_stars" under the selected nodes.
2. Select path nodes '//mutation[name=CreateReviewForEpisode]/createReview/stars', Add new sibling node "sibling_of_stars" next to the selected nodes.
3. Select path nodes '//mutation[name=CreateReviewForEpisode]/createReview/stars', Update the stars nodes name to 'star_new_name'.
4. Select path nodes '//mutation[name=CreateReviewForEpisode]/createReview/commentary', remove these nodes.
5. Select path nodes '//mutation[name=CreateReviewForEpisode]/createReview/sibling_of_stars', duplicate this node 10 times

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

System.out.print("\nBefore: \n\n" + rawPayload.getQueryValue());

RawPayload executeRawPayload = transformExecutor.execute(rawPayload);
```

Here a GraphQL **after** the manipulation:
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


# Design

## UML diagrams, use of mermaid

```mermaid
sequenceDiagram
TransformExecutor ->> TransformBuilder: getCommands 
TransformExecutor --> TransformExecutor: iterate over command
TransformBuilder ->> TransformCommand: executeCommand
TransformExecutor ->> TransformUtils : invoke unique transform operation

