[Back](../)

# Content Modeling

In modeling content, you define the structure of content objects that you will work with. 

For example, if you are working on a news website, you may need a _NewsArticle_ content type to represent
news items, and a _PressRelease_ content type to represent non-news information items that will appear on 
the site. 

Content types are specified in a YAML document in the _types_ directory. The YAML document declares a type
name, the [workflows](WORKFLOW.md) that govern the content, and a set of fields that comprise the content
object.

In the **JellyBean** example is below:

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

The this example, the type is name _JellyBean_, and it is subject to the rules defined in the _JellyBeanWorkflow_.
 It has two fields, a _flavor_ and a _color_, which are both **String** fields. The flavor field can have a max 
 length of 10 characters, and the color must be one of _red_, _green_ or _blue_.

## Content Field Configuration

A field must have a **name** and **type** and can optionally be **indexed** or **required**. Each type of field can
have custom options, in a yaml map called _config_. 

An **indexed** field will have a DB index built to search it. If you plan on searching based on a simple field often, it
should probably be indexed ( This does have a per-field storage and write performance penalty though - if you need to 
search _a lot_ of fields, or do full text searching, consider using the [search](SEARCH.md) feature ). 

A **required** field requires a non-null value.

```yaml
  - name: flavor
    type: String
    required: true
    indexed: true
    config:
      maxLength: 10


```

### Field Type Reference

#### String

Holds a string.

_Config options:_

| param             | description                               |
| ----              | ----                                      |
| regex             | a regex to validate the string            |
| allowedValues     | a list of values that the string may be   |
| minLength         | the min length of the string              |
| maxLength         | max length of the string                  |

#### Int

Holds an integer.

_Config options:_

| param             | description                               |
| ----              | ----                                      |
| min               | min value                                 |
| max               | max value                                 |


#### Date

Holds a date/time object.

#### Binary

Holds a binary file - this can be used for images, attachments, etc.

#### Collection

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


#### ObjectRef

A reference to another content object, by id. 

| param             | description                                                                                               |
| ----              | ----                                      |
| referencedType (optional)             | the type of object referenced. If not there, any type of object can be referenced     |


### Adding field tyoes

See the [Extending](EXTENDING.md) for information on custom field types.

[Back](../)