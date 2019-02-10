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
### Changing Content Values in ActionHooks

ActionHooks may change content values, if they wish. This opens up the potential of side effects between different
hooks that may want to work on the same values. 