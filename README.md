# TeenyCMS


## Basics

### Create Workflows


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
	"content":{
		"flavor":"cinnamon",
		"color": "red"	
	}
}
```

### Get Content

Can now be found:

    GET /api/v1/content/type/JellyBean
    
OR

    GET /api/v1/content/type/JellyBean/InBag
    
OR

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
    "group": null,
    "ownerPermissions": "RW",
    "groupPermissions": "R",
    "otherPermissions": "NONE",
    "content": {
        "flavor":"cinnamon",
        "color": "red"	
    }
},
```

## Action Hooks

_ActionHooks_ allow business actions to be attached to workflows.
 
 
|| Name         || Description                          || Config Parameters || User Parameters || 
| AssignToGroup |  Assigns the group of the content     | group              | group            |
| AssignToUser  |  Assigns the owner of the content     | user               | user             |
| AddToSearch   |  Adds the content to the search index | owner
group
other | owner
group
other |


## Search
