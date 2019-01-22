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
 
 
## Basic Setup

For setting up a basic content model, two bits of configuration need to be put in place. These are configured via YAML files that the app reads:

 * Workflow definitions - these contain the process which governs the content - the workflows move the content through a series of states, and can perform various actions on the content. 
 * Content Definitions - these define the structure of content types used by the app. 

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
    hooks:
     - name: AddToSearch 
       index: bagindex

  - name: Take
    nextState: InHand
    sourceStates:
     - InBag
    hooks:
     - name: RemoveFromSearch
       index: bagindex

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
workflows:
  - JellyBeanWorkflow
fields:
  - name: flavor
    type: String
    config:
      maxLength: 10

  - name: color
    type: String
    config:
      allowedValues:
        - red
        - green
        - blue

```

### Create Content

```
POST /api/v1/content
```

```json
{
    "workflow":"JellyBeanWorkflow",
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

    GET /api/v1/content?type=JellyBean&workflow=JellyBeanWorkflowstate=InBag
    
OR id:

    GET /api/v1/content/{id}
    
    
```json
{
    "id": "3085b75a-44bf-4b1e-8286-1af288a036e0",
    "version": 1,
    "created": "2019-01-21T01:37:12.357+0000",
    "modified": "2019-01-21T01:37:12.357+0000",
    "workflow": "JellyBeanWorkflow",
    "state": "InBag",
    "type": "JellyBean",
    "owner": "joe",
    "assignedUser": null,
    "assignedGroup": null,
    "name": null,
    "searchVersions": {
        "bagIndex": 1
    },
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
    "content": {
        "flavor": "cherryred",
        "color": "red"
    }
}
```
## Content Fields

Content types are composed of fields.

A field needs a "name" and "type" and can be "indexed" or "required". Each type of field can
have custom options, in a yaml map called _config_. 

```yaml
  - name: flavor
    type: String
    required: true
    indexed: true
    config:
      maxLength: 10


```

### String

Holds a string.

_Config options:_

| param             | description                               |
| ----              | ----                                      |
| regex             | a regex to validate the string            |
| allowedValues     | a list of values that the string may be   |
| minLength         | the min length of the string              |
| maxLength         | max length of the string                  |

### Int

Holds an integer.

_Config options:_

| param             | description                               |
| ----              | ----                                      |
| min               | min value                                 |
| max               | max value                                 |


### Date

Holds a date/time object.

### Binary

Holds a binary file - this can be used for images, attachments, etc.

### Collection

This contains a collection of other items. The items it contains can be any other field type.

_Config options:_

| param             | description                               |
| ----              | ----                                      |
| collectionType (required)             | a field configuration which defines the type of items in the collection         |

_Example:_

```yaml
  - name: collectionOfStuff
    type: Collection
    config:
      collectionType:
        type: String
        config:
          maxLength: 49
          minLength: 20
          regex: "[a-z]*"
          
```


### ObjectRef

A reference to another content object, by id. 

| param             | description                                                                                               |
| ----              | ----                                      |
| referencedType (optional)             | the type of object referenced. If not there, any type of object can be referenced     |



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
     - name: Assign
       config:
        group: friends
     - name: SetPermissions
       config:
        group: 
          read: true
          write: true
```
 
### Built In Hook Reference

#### Assign

This assigns the content to a user and/or group by setting the _assignedUser_ and _assignedGroup_ fields. 

```yaml
- name: Assign
  config:
    group: friends
    user: joe
```

#### SetPermissions

This sets the permissions of the content. 

Permissions can be set for the _owner_, _assignee_, _other (anyone else)_ or a set of _groups_

```yaml
- name: SetPermissions
  config:
    owner:
      read: true
      write: true
    assignee:
      read: true
      write: true
    other:
      read: true
      write: false
    group:
      friends:
        read: true
        write: false
```

#### AddToSearch

Adds the content to a search index. The search index can be specified in the _index_ parameter. If not specified, it 
will be added to an index called "default".

```yaml
- name: AddToSearch
  config:
    index: myindex
```

#### RemoveFromSearch

Removes the content from a search index. The search index can be specified in the _index_ parameter. If not specified, it 
will be removed from an index called "default".

```yaml
- name: RemoveFromSearch
  config:
    index: myindex
```
                          | 
 
### Adding Action Hooks for Custom Business

To Implement custom behavior, additional hooks can be added. 

They must be registered Spring Beans, and they will automatically be made available. 

```java
package my.stuff;

import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
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

#### Bypassing Permissions In Custom Business

In some cases, it may be needed to bypass the permission system that restricts users, for example, if there are
async workflow actions which don't have a security context associated with them.

```java

    @Autowired
    AuthService authService;
    
    @Autowired
    ContentService contentService;

    public void myBusiness(){

        Objcet thing = authService.withSystemUser(()->{
          
	        return contentService.find(...);

        });

    }
```

### Changing Content Values in ActionHooks

ActionHooks may change content values, if they wish. This opens up the potential of side effects between different
hooks that may want to work on the same values. 

## Search

Search is provided by [elasticsearch]().

This can be done via the _search_ endpoint, using the **q** parameter:

    GET /api/v1/search/myindex?q=content.color:red
    
    GET /api/v1/search/myindex?q=content.color:red AND content.flavor:cinnamon
    
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

 

