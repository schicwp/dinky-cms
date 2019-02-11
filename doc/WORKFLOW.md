[Back](../)

# Workflow Modeling

**Workflows** defines the processes by which content is modified. The are managed by editiing YAML documents in the 
_workflows_ directory. 

A workflow is composed of a _name_, which identifies the workflow, a set of _states_, which define the valid states 
that the content is allowed to be in, and a set of _actions_, which transition content from one state to another.

The **JellyBeanWorkflow** is shown below:

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
    allowedGroups:
     - Friends
     - Family

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

This workflow has three states: _InBag_, _InHand_ and _InMouth_. There are three actions which can be used to move 
content through the workflow: _PutInBag_, _Take_ and _Eat_. 

The _PutInBag_ action is marked as an **entryPoint**. This means that _new_ content be created using that action. 
Any action can be marked as an **entryPoint**. 

Each action has a single **nextState** field. This is the state that the content will be in after the action is 
performed.

Each action can _optionally_ have a list of **sourceStates**. This is a list of states that the content may be in for
that action to be performed. If not defined, the action can be performed while the content is in _any_ state. 
So, in the above example, a jelly bean can be moved from _InHand_ to _InMouth_ using the take action, and may moved 
from the _InMouth_ state to the _InBag_ state via the _PutInBag_ action, but cannot be moved directly from _InMouth_ 
to _InHand_.

A workflow action can have a set of **allowedGroups**. These are groups that can execute that action. In the above 
example, only members of either the _Friends_ or _Family_ groups can execute the _PutInBag_ action. If not present, 
no group restrictions will be placed on the action.

## Action Hooks

_ActionHooks_ allow business actions to be attached to workflows.

These should have a **name** which identifies the hook to be executed, and a **config**, which will vary depending on \
the hook. 

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
 
### Built In Hooks

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

### Order of Hook execution

The order of hook execution will be the order declared in the YAML definition. This can have an impact if the hook
changes any values in the content.
                          | 
### Changing Content Values in ActionHooks

ActionHooks may change content values, if they wish. This opens up the potential of side effects between different
hooks that may want to work on the same values. 


