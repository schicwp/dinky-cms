[Back](../)

# Working With Content

To work with content using the REST api, there are 

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



### Optimistic Locking 

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


## Content Responses


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
 

## Binary Data (Images, Attachments)

Binary data can be handled by using a _Binary_ field type on a ContentType.

This can then be uploaded via a Multipart submission (TODO, details)


    GET /api/v1/assets/{assetId}
    
The binary asset will have the same permissions as the object that owns it. 

[Back](../)

