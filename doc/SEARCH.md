## Search

The Search feature provides a full text content serach. Content is added to a specific search index by using the _AddToSearch_ action hook. 

As specific search index can be thought of a view or snapshot of data. This can be used to create a user-defined body of content seperate from the workflows governing the content. For example, to create a "published" view of content, a "publish" action can add items to a "published" index, which will represent published content. 

Items can be removed from an index using the _RemoveFromSearch_ action hook.

### Search API

This can be done via the _search_ endpoint, using the **q** parameter:

    GET /api/v1/search/myindex?q=content.color:red
    
    GET /api/v1/search/myindex?q=content.color:red AND content.flavor:cinnamon
    
This uses elasticsearch [query string](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html)
syntax.