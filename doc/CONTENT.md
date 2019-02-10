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


