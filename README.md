# Dinky CMS

This is a small, web based content repository service.

Features:
 - User defined content types
 - User defined workflows
 - Content and workflow permissioning
 - Robust content indexing and searching
 - Content concurrency control using optimistic locking
 - Content version history
 
NOT Features:
 - This app is not a website platform - it does not have any sitebuilder features
 - The app is configuration and api driven, there is no management UI

Tech Stack:
 - MongoDB
 - Elasticsearch
 - Spring Boot
 
 
## Basics Setup

### Create Workflows

Workflows are configured by adding a yaml file to the _workflows_ configuration directory. 

They contain a list of _states_ and _actions_ that transition the content from state to state.

```yaml
name: JellyBeanWorkflow

states:
  - name: InBag
  - name: InHand
  - name: InMouth

actions:

  - name: PutInBag
    entryPoint: true
    nextState: InBag
    
  - name: Take
    nextState: InHand
    sourceStates:
     - InBag
    
  - name: Eat
    nextState: InMouth
    sourceStates:
      - InHand

```

### Create Content Types

Content Types are configured by adding a yaml file to the _content types_ configuration directory. 

These provide the structure of content, along with validation constraints, and say which workflow the content
will use.

```yaml
name: JellyBean
workflow: JellyBeanWorkflow
fields:
  - name: flavor
    type: String

  - name: color
    type: String

```

### Create Content

```
POST /api/v1/content/JellyBean
```

```json
{

	"action":"PutInBag",
	"type":"JellyBean",
	"content":{
		"flavor":"cinnamon",
		"color": "red"	
	}
}
```

### Get Content

Can now be found by type listing:

    GET /api/v1/content?type=JellyBean
    
OR by type and state listing:

    GET /api/v1/content?type=JellyBean&state=InBag
    
OR id:

    GET /api/v1/content/item/{id}
    
    
```json
{
    "id": "AWgkUMg4ugnMqnnx65us",
    "version": 1,
    "created": "2019-01-06T17:58:20.470+0000",
    "modified": "2019-01-06T17:58:20.470+0000",
    "state": "InBag",
    "type": "JellyBean",
    "owner": "bob",
    "permissions": {
    	"owner":{
		"read":true,
		"write": true
	}
	"group":{
	}
    }
    "groupPermissions": "R",
    "otherPermissions": "NONE",
    "content": {
        "flavor":"cinnamon",
        "color": "red"	
    }
},
```
## Content Fields

Content types are composed of fields.

### String

Holds a string.

### Int

Holds an integer.

### Date

Holds a date/time object.

### Binary

Holds a binary file - this can be used for images, attachments, etc.

### Collection

This contains a collection of other items. The items it contains can be any other field type.

### ObjectRef

A reference to another content object, by id. 


## Action Hooks

_ActionHooks_ allow business actions to be attached to workflows.

These are configured in the workflow yaml:

```yaml
actions:

  - name: PutInBag
    entryPoint: true
    nextState: InBag
    #use hooks to let friends have beans as well
    hooks:
     - name: AssignToGroup
       group: friends
     - name: SetPermissions
       group: rw
```
 
Standard hooks below:
 
  Name              |  Description                          | Config Parameters              | User Parameters  
 ---                | ---                                   | ---                            | ---                     
 AssignToUser       |  Assigns the owner of the content     | user                           | user             
 SetPermissions     |  Sets the permissions of the content  | owner <br/> group <br/> other  | owner <br/> group <br/> other 
 AddToSearch        |  Adds the content to the search       |                                | 
 RemoveFromSearch   |  Removes the content from the search  |                                | 
 
### Adding Action Hooks for Custom Business

To Implement custom behavior, additional hooks can be added. 

They must be registered Spring Beans, and they will automatically be made available. 

```java
package my.stuff;

import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyHookFactory implements ActionHookFactory {

    // name will be used in workflow yaml
    @Override
    public String getName() {
        return "MyHook";
    }

    //"config" are settings from the workflow yaml
    @Override
    public ActionHook createActionHook(Map<String, String> config) {
        
        //"actionConfig" is provided by submission
        return (content, actionConfig) -> {
            //TODO - do stuff with content
        };
    }

}
```

### Changing Content Values in ActionHooks

ActionHooks may change content values, if they wish. This opens up the potential of side effects between different
hooks that may want to work on the same values. 

## Search

Search is provided by [elasticsearch]().

This can be done via the _search_ endpoint, using the **q** parameter:

    GET /api/v1/search?q=content.color:red
    
    GET /api/v1/search?q=content.color:red AND content.flavor:cinnamon
    
This uses elasticsearch [query string](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html)
syntax.


### Adding to the search index

The _AddToSearch_ action hook can be used to add items to the search index. 

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

Each content resource has an _owner_ and a _group_ field, and permissions for the _owner_, _group_ and 
_other_ (everybody else) in the fields _ownerPermissions_, _groupPermissions_ and _otherPermissions_.

The _owner_ field represents a user, and the _group_ field represents a group or role. 

The permissions fields may have the following values:

 - RW - when this is granted, the item may be read and writen to
 - R - the item may be read
 - W - the item may be written to (but not read)(!)
 - NONE - the item cannot be read or written to
 
 
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

 

