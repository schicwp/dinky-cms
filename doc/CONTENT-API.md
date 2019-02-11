[Back](../)

# Working With Content

To work with content using the REST api, there are two overall functions:

1. Submit content as a **ContentSubmission**
2. Read content as a **ContentResponse**

## Content Submissions

Content is submitted by HTTP POST to the following endpoint:

    /api/v1/content
    
The object submitted to this endpoint should be a [ContentSubmission](../javadocs/org/schicwp/dinky/api/dto/ContentSubmission.html)
object. 

It should have the following fields:


 name           | type      |   description                                     |   notes
---             | ---       |   ---                                             |    ---
id              | string    | the id of the content if already exists           | if present, indicates an update to a document, absent for new ones
type            | string    | the [content type](./CONTENT.md)                  | required
workflow        | string    | the name of the [workflow](./WORKFLOW.md)         | required **if** the type has more than one workflow
action          | string    | the name of the [workflow action](./WORKFLOW.md)  | optional
version         | int       | the current version of the document               | if present, will check version (see optimistic locking)
workflowConfig  | map       | the per action workflow config, used by hooks     | per hook configuration
content         | map       | the content to be submitted                       | defined by [content type](CONTENT.md)

#### Example Submission

```json
{
    "id": "3e8b17a3-2c5c-4ab3-8ad6-12cf4c5839b8",
    "type":"JellyBean",
    "workflow":"JellyBeanWorkflow",
    "action":"PutInBag",
    "version":3,
    "workflowConfig":{
      "Assign":{
        "user":"Jerry"
      }
    },
    "content":{
        "flavor":"cinnamon",
        "color": "red"	
    }
}
```

### Submissions without a workflow action

If a submission is made _without_ a workflow action, then the content will only be updated, but the state and all metadata
will remain the same.

### Optimistic Locking 

Users can _optionally_ use optimistic locking to prevent versioning conflicts. 

This can be useful if there are any non-idempotent side effects (in an export ActionHook, for example).

To use the locking, the user should provide a _version_ property on submission. This will be compared to 
the latest stored version, and rejected if they do not match. The _version_ property is incremented on 
every modification. 

If the version is not provided, the locking mechanism will be ignored, and the submission will be accepted 
unconditionally.

### Submission Response

The response to a successful submission will return the modified content with all data and metadata updated.


## Content Responses

A _ContentResponse_ object will be provided any time content is read, including:

* The response object when a submission is made
* When list queries are made (contained within a Page object)
* When singlular by-id queries are made
* When search queries are made
* When history queries are made

The _ContentResponse_ contains the following fields:

 name           | type      |   description                                     |   notes
---             | ---       |   ---                                             |    ---
id              | string    | id of content                                     | 
version         | int       | current version of this content                   |   
created         | date      | the date that the content was first created       |
modified        | date      | date content was last modified (version created)  |
workflow        | string    | the name of the workflow the content is in        |
state           | string    | the workflow state of the content                 |
owner           | string    | the owner of the content                          |
assignedUser    | string    | the assigned user of the content                  | defaults to null; null indicates no assignedUser
assignedGroup   | string    | the assigned group of the content                 | defaults to null; null indicates no assignedGroup
searchVersions  | map       | a map of versions which are in search indexes     | see the [search page](SEARCH.md) for details
permissions     | map       | the current permissions for the content           |
content         | map       | the content                                       |

Example:

```json
{
    "id": "3e8b17a3-2c5c-4ab3-8ad6-12cf4c5839b8",
    "version": 3,
    "created": "2019-02-11T01:14:42.913+0000",
    "modified": "2019-02-11T16:54:34.356+0000",
    "workflow": "JellyBeanWorkflow",
    "state": "InBag",
    "type": "JellyBean",
    "owner": "joe",
    "assignedUser": "jerry",
    "assignedGroup": null,
    "searchVersions": {},
    "permissions": {
        "owner": {
            "read": true,
            "write": true
        },
        "assignee": {
            "read": true,
            "write": true
        },
        "other": {
            "read": false,
            "write": false
        },
        "group": {}
    },
    "content":{
        "flavor":"cinnamon",
        "color": "red"	
    }
}
```

### Content Queries

**TODO** more detail

### Search Queries

Search queries also return lists of content, but are backed by the elasticsearch indices.

See the [search page](SEARCH.md) for more details. 

### Version History

Every modification to a piece of content is saved. 

These versions can be retrieved by using the endpoint:

    GET /api/v1/content/item/{id}/history
    
This will give a paginated list of all versions. The permissions for the 
current version will determine access to the history. 

## Content Permissions

Permissions control who can access or modify a document, or who can execute a certain workflow action.

**TODO** - more detail. 
 

## Binary Data (Images, Attachments)

Binary data can be handled by using a _Binary_ field type on a ContentType.

This can then be uploaded via a Multipart submission (TODO, details)


    GET /api/v1/assets/{assetId}
    
The binary asset will have the same permissions as the object that owns it. 

**TODO** -more detail

[Back](../)

