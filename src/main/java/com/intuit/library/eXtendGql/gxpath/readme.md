# What is gqlXPath?
A unique path selection solution for GraphQL document.

gqlXPath can be used to navigate through elements in **GraphQl** with the assist of eXtendGql Browser
for specific **GraphQl** node.

## gqlXPath

gqlXPath stands for Gql Extended Path expression Language
gqlXPath uses "path like" syntax to identify and navigate nodes in an **GraphQl** document
gqlXPath have the ability to navigate node in:
1. Query
2. Mutation
3. Fragments
4. Inline Fragment
5. Argument name
6. Directives
7. Variables Reference

The gqlXPath language is similar to other path language e.g. xpath for xml, while gqlXPath
provide unique way to navigate through **GraphQl** nodes.

## gqlXPath Syntax

### Selecting Node
> Currently support of selecting only one node only

gqlXPath uses path expressions to select node in an **GraphQl** document.
The node is selected by following a path or steps.
Path expressions are listed below:

| Expression | Description                                                                                                                                                                                                                                                                                                                         | 
|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| //      | Path prefix, Select all nodes from the root node, use of slash as a separator between path elements.                                                                                                                                                                                                                                |
| /       | Path prefix, Select first node from the root node, use of slash as a separator between path elements.  Range does not support when first node selected.                                                                                                                                                                             |
| {x:y}/  | Path prefix, Select path node(s), between range of x path node and y path node (inclusion), use of slash as a separator between path elements. x and y are positive integers. if no x and y are not set, all nodes are being selected.                                                                                              |
| {:y}//  | Path prefix, Select path node(s), between range of first path node result to y, use of slash as a separator between path elements. x and y are positive integers. if no x and y are not set, all nodes are being selected.                                                                                                          |
| {x:}/   | Path prefix, Select path node(a), between range of x path node result to the end of path nodes result, use of slash as a separator between path elements. x and y are integers. if no x and y are not set, select all path nodes.                                                                                                   |
| {:}//   | Path prefix, Select node(s) from the root node, use of slash as a separator between path elements.                                                                                                                                                                                                                                  | |
| ... | Support of relative path **"any"** selection e.g. {x:y}//a/b/.../f <br>  **any** can be set anywhere in the  gqlXPath, except at the end of the gqlXPath, You can set many **any** as you request, this will help you while selecting node in large GraphQL structure, so you wont required to mention/build the entire node structure. |

Path expression elements:

| Element Name | Description                           |
|--------------|---------------------------------------|
| type=        | Select element by type abbreviate     |                                                                                                                                                                                                 |
| name=        | Select element by name                |                                                                                                                                                                                                 |
| alias=       | Select element by alias name          |

Available types and abbreviation:

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


__GraphQl Document__
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

| gqlXPath Example | Description                                                        |
|------------------|-------------------------------------------------------------------------|
| ֿֿ//query/Instrument/name[type=arg] | select of node name which is argument                  |
| //query/Instrument/Reference/name | select of name node which is field under               |


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


| gqlXPath Example                                                  | Description                                                                                                     |
|-----------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| //query[name=hero]                                              | select all query nodes named hero                                                                               |
| /query[name=hero]                                               | select all query node named hero                                                                                |
| //query[name=hero]/hero/friends/name                            | select all nodes named name, reside under friends node                                                          |
| {0:2}//query[name=hero]/hero/friends/name                       | select all nodes named name within a range of index 0 and index 2 (inclusion), reside under friends node        |
| /query[name=hero]/hero/friends/name                             | select node named name, reside under friends node                                                               |
| /query[name=hero]/hero/.../name                                 | select node named name, reside under any node, which reside under hero node                                     |
| //query[name=hero]/withFriends[type=var]                        | select $withFriends variable reside directlly under the root node named hero   `$withFriends: Boolean!`         |
| //query[name=hero]/episode[type=var]                            | select episode variable  reside directlly under the root ndoe named hero `$episode: Episode`                    |
| //.../episode[type=var]                                         | select episode variable                                                                                         |
| //query[name=hero]/hero/episode[type=arg]                       | Select episode argument node under hero node, reside under root query node named hero                           |
| /query[name=hero]/hero/friends/include[type=direc]              | select include  directive first node reside under friends, which reside under root query node named hero        |
| //query[name=hero]hero/friends/include[type=direc]/if[type=arg] | select the if argument node, reside under <br/>the @include directive, reside under root query node named hero. |
| //.../include[type=direc]/if[type=arg]                          | select the if argument node, reside under <br/>the @include directive                                           |



__GraphQl Document__
```text
query HeroForEpisode($ep: Episode!) {
  hero(episode: $ep) {
    name
    ... on Droid {
      primaryFunction
    }
    ... on Human {
      height
    }
  }
}

```


| gqlXPath Example | Description                                      |
|------------------|--------------------------------------------------|
|//query[name=HeroForEpisode]/hero/Droid[type=infrag]| select droid inline fragment node                |


__GraphQl Document__
```text
query HeroComparison($first: Int = 3) {
    leftComparison: hero(episode: EMPIRE) {
      ...comparisonFields
    }
    rightComparison: hero(episode: JEDI) {
      ...comparisonFields
    }
}

fragment comparisonFields on Character {
    name
    friendsConnection(first: $first) {
        totalCount
        edges {
            node {
              name
            }
        }
    }
}


```


| gqlXPath Example | Description                                                          |
|------------------|----------------------------------------------------------------------|
|//comparisonFields[type=frag]/friendsConnection/totalCount| select totalCount node under comparisonFields fragment               |
|//comparisonFields[type=frag]/name| select name node under comparisonFields fragment                     |
|//query[name=HeroComparison]/hero[alias=rightComparison]| select hero noe with alias named rightComparison,<br/> under query named HeroComparison |


__GraphQl Document__
```text
mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
  createReview(episode: $ep, review: $review) {
    stars
    commentary
  }
}
```

| gqlXPath Example | Description                                                            |
|------------------|------------------------------------------------------------------------|
|//mutation[name=CreateReviewForEpisode]| select mutation node                                                   |
|//mutation[name=CreateReviewForEpisode]/createReview| select createReview node under mutation named CreateReviewForEpisode   |
|//mutation[name=CreateReviewForEpisode]/createReview/commentary| select  commentary node (field),<br/> mutation named CreateReviewForEpisode |
|//mutation[name=CreateReviewForEpisode]/episode[type=arg]| select episode argument, directlly under the <br/>mutation named CreateReviewForEpisode                          |


__GraphQl Document__
```text
mutation createCompany {
  createCompany_CompanySetupInfo(
    input: {
      clientMutationId: "1"
      companyCompanySetupInfo: {
        profile: {
          localization: { country: "US" }
          companyName: "Rcc Paid company"
          contactMethods: [
            {
              addresses: [
                {
                  addressComponents: [
                    { name: "CITY", value: "New York" }
                    { name: "cityOrLocality", value: "New York" }
                    { name: "STATE", value: "NY" }
                    { name: "stateOrProvince", value: "NY" }
                    { name: "ZIP", value: "10038" }
                    { name: "zipcode", value: "10038" }
                    { name: "ADDRESS_LINE_1", value: "2491 Turkey Pen Road" }
                    { name: "address1", value: "2491 Turkey Pen Road" }
                    { name: "COUNTRY", value: "US" }
                    { name: "country", value: "US" }
                  ]
                }
              ]
              emails: [{ emailAddress: "rcctestemail@gmail.com" }]
            }
          ]
          industryType: "ALL_OTHER_MISCELLANEOUS_SERVICES"
        }
        companyProfile: {}
        subscriptionSetup: {
          paymentInfo: {
            paymentMethod: { type: CreditCard }
            creditCardInfo: {
              name: "test"
              cardType: "VISA"
              number: "9732547836012241"
              expMonth: "10"
              expYear: "2029"
              cvv: "9a2009b9-92d7-4a9a-8ea3-f5e03057861e"
              address: {
                addressComponents: [
                  { name: "CITY", value: "New York" }
                  { name: "cityOrLocality", value: "New York" }
                  { name: "STATE", value: "NY" }
                  { name: "stateOrProvince", value: "NY" }
                  { name: "ZIP", value: "10038" }
                  { name: "zipcode", value: "10038" }
                  { name: "ADDRESS_LINE_1", value: "2491 Turkey Pen Road" }
                  { name: "address1", value: "2491 Turkey Pen Road" }
                  { name: "COUNTRY", value: "US" }
                  { name: "country", value: "US" }
                ]
              }
            }
          }
          offer: { offerId: "20015738", country: "US" }
          locale: ""
          region: "US"
          companyType: ""
          createSubscriptionAppData: { billingCode: "OBI-LL3", sourceCode: "" }
        }
        privacyPreference: { doNotEmail: false }
        marketingTags: [
          {
            name: "sourceCode"
            value: "cid:ppc_G_e_CA_.QBO_CA_B_Quickbooks_Products_Exact_T1_G_S_FY19._quickbooks online easystart_txt|sc:|ext:|int:INT||isQBSE"
          }
          {
            name: "semCookie"
            value: "cid:ppc_G_e_CA_.QBO_CA_B_Quickbooks_Products_Exact_T1_G_S_FY19._quickbooks online easystart_txt|sc:|ext:|int:INT|"
          }
        ]
      }
    }
  ) {
    clientMutationId
    companyCompanySetupInfo {
      id
      companyProfile {
        companyName
        region
        partner
        industryType
        language
        contactMethods {
          emails {
            emailAddress
          }
        }
      }
      subscriptionSetup {
        offer {
          offerId
        }
        createSubscriptionAppData {
          billingCode
          sourceCode
        }
      }
      privacyPreference {
        doNotEmail
        doNotPhone
      }
    }
  }
}
```

| gqlXPath Example | Description                                                |
|------------------|------------------------------------------------------------|
|//mutation[name=createCompany]/createCompany_CompanySetupInfo/input[type=arg]| select input argument node 'input: {clientMutationId: "1"' |                                                  
|/mutation[name=createCompany]/companyCompanySetupInfo/companyProfile| select companyProfile node                                 |



# How to use gqlXPath in code

gqlXPath has 2 representation options:
1. Text, string represent the path e.g. //query[name=hero]/a/b/c<br> use of textual selection commonly will occure once the developer know before runtime which node he should select, so the developer can specifiy the exact node path.
2. SyntaxPath, A SyntaxPath is a class that contains a list of SyntaxPathElements. 
<br>Each SyntaxPathElement describes the behavior of a relevant path element and is <br>
part of the SyntaxPath. <br>
You can create a SyntaxPath object by initializing the class and setting up the SyntaxPathElement list. Alternatively, you can use the SyntaxBuilder class to simplify the process of building the SyntaxPath.
<br> Use of SyntaxPath meant to provide automated runtime selection, so the selection is set during runtime, so use of code to build the selection path is much easier and right.


__Use of Text__
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


# HL Design

## UML diagrams, use of mermaid

```mermaid
sequenceDiagram
SelectorFacade ->> traversal: init 
SelectorFacade ->> SearchObserver: init(searchSyntax)
traversal ->> GqlBrowserObservable: addObserver
traversal ->> traversal : browse
traversal ->> SearchObserver: getSearchNode
SearchObserver-->> traversal :GqlNodeContext
