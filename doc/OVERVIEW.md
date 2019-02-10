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