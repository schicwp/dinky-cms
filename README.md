# Dinky CMS

This is a small, web based content repository service.

Features:
 - User defined content types
 - User defined workflows
 - Content and workflow permissioning
 - Full text content indexing and searching
 - Content concurrency control using optimistic locking
 - Content version history


Tech Stack:
 - MongoDB
 - Elasticsearch
 - Spring Boot
 

## Topics
 
* [Basic Setup](doc/OVERVIEW.md)
* [Content Modeling](doc/CONTENT.md)
* [Workflow Modeling](doc/WORKFLOW.md)
* [Search](doc/SEARCH.md)
* [Extending](doc/EXTENDING.md)




## Optimistic Locking 

Users can _optionally_ use optimistic locking to prevent versioning conflicts. 

This can be useful if there are any non-idempotent side effects (in an export ActionHook, for example).

To use the locking, the user should provide a _version_ property on submission. This will be compared to 
the latest stored version, and rejected if they do not match. The _version_ property is incremented on 
every modification. 

```json
{

	"action":"PutInBag",
	"version":3,
	"content":{
		"flavor":"cinnamon",
		"color": "red"	
	}
}
```

If the version is not provided, the locking mechanism will be ignored.

## Version History

Every modification to a piece of content is saved. 

These versions can be retrieved by using the endpoint:

    GET /api/v1/content/item/{id}/history
    
This will give a paginated list of all versions. The permissions for the 
current version will determine access to the history. 

## Permissions

Permissions control who can access or modify a document, or who can execute a certain workflow action.

### Content Permissions

TODO - update this
 
 
### Workflow Permissions

A workflow action can have a set of _allowedGroups_. These are groups that can execute that action.

In the following example, only members of either the _Friends_ or _Family_ groups can execute the _PutInBag_ 
action:

```yaml
actions:

  - name: PutInBag
    entryPoint: true
    nextState: InBag
    allowedGroups:
     - Friends
     - Family
```

### Binary Data (Images, Attachments)

Binary data can be handled by using a _Binary_ field type on a ContentType.

This can then be uploaded via a Multipart submission (TODO, details)


    GET /api/v1/assets/{assetId}
    
The binary asset will have the same permissions as the object that owns it. 

 

