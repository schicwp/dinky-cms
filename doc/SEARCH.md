[Back](../)

# Search

The search accomplishes two main goals:

* It allows full text searching of the content
* When used in conjunction with workflows, tt allows the creation of a _projection_ or _view_ of the content, 
which is user defined and can allow some independence of from the working state of the content. 

## Adding and removing items from a search index

Content is added to a specific search index by using the [AddToSearch](WORKFLOW.md#AddToSearch) action hook. 

Items can be removed from an index using the [RemoveFromSearch](WORKFLOW.md#RemoveFromSearch) action hook.


## Search API

This can be done via the _search_ endpoint, using the **q** parameter:

    GET /api/v1/search/myindex?q=content.color:red
    
    GET /api/v1/search/myindex?q=content.color:red AND content.flavor:cinnamon
    
This uses elasticsearch [query string](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html)
syntax.

## Search API vs Content API

The content api (**/api/v1/content**) is served by the mongoDB hosting content. This allows for simple searching, and 
includes some indexing, but is best used when looking at content metadata fields (eg, the _state_, _type_, _owner_ etc).

The search api is serviced by elasticsearch and can also search on metadata, but can 

## Using _search_ to separate _working_ from _live_ content

Say, for example, we are making a news content application, and we want to have a body of _published_ items,
 which is accessible to content consumers.

We can define a workflow with a _Published_ state and a _Publish_ action to put items in that state. They can then be
served content by querying "all content in the Published state".

This works fine, until we want to _revise_ a bit of content that has been published. Maybe we would create a _Revise_ 
action, which would send the content back to a _Draft_ state - but the content is now no longer _Published_, and will
no longer be viewable until the the content is re-published. 

The search feature can be used to separate the source of data for the client from the content in that is "in progress".
If we define a search index _published_ and add an _AddToSearch_ hook to the _Publish_ action, then the content will
persist in the _published_ index even if the source content transitions back to a draft or other state. If the consumers
only read from the search index, they will not see the content disappear, but will see changes if it is "Re Published"
later.

There are lots of possible permutations of this, but can be boiled down to this pattern:

* **working content** - content that is used by backend/editing processes, which is serviced by the content api and
directly cares about the latest content state
* **live content** - this is explicitly placed into a search index as part of a workflow process, is serviced by the
search api, and does not care/ does not see backend changes not related to the index
 
## Search versions and the _canonical data_

The data in content database (mongoDB) should be treated as the _canonical data_: all of the search indexes can be 
rebuilt from the content database.

The content contains a set of _searchVersions_ which track which data (and which versions) have been put into a given
search index, for example:

```json
{
    "id": "b1b2f9da-dc60-4723-8fff-86f436854afb",
    "version": 7,
    "created": "2019-02-11T00:49:17.184+0000",
    ...
    "searchVersions": {
        "published": 5
    },
    ...
 }
```

The working version of this item is **7**, but the _published_ index contains version **5**. 

If the search index data was lost or corrupted, it can be rebuilt using the data in the content database. 



[Back](../)