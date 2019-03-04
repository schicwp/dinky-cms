[Back](../)

# Extending the application

## Creating Content Field Types

Additional content field types can be extended by implementing [FieldType](../javadocs/org/schicwp/dinky/model/type/FieldType.html).

```java

@Component
public class MyFieldType implements FieldType {


    @Override
    public String getName() {
        //this is a unique name, used to declare fields of this type 
        return "MyType";
    }


    @Override
    public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {
      
        //TODO - validate that the submitted object is OK - if not, return false and add a message to the 'errors' array

        return true;
    }

    @Override
    public Object convertSubmission(Object input, ContentMap properties, Content content) {
        
        //TODO - convert the submission to what should be persisted
        // the "date" type, for example, converts a string data representation to a Date object
        
        return input;
    }

}

```

## Adding Action Hooks for Custom Business

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
    public ActionHook createActionHook(ContentMap config) {
        
        //"actionConfig" is provided by submission
        return (Content content,ContentMap actionConfig) -> {
            //TODO - do stuff with content
        };
    }

}
```


## Service Beans


bean                        | description                               | notes
---                         | ---                                       | ----
ContentService              | allows querying of the content database   | should not be used to write content, otherwise, validation and workflow will be ignored. Use **ContentSubmissionService** to write content
ContentSubmissionService    | allows submission of content              |
SearchService               | gives access to search indexes            | see [search](SEARCH.md) for more info on search
AuthService                 | gives access to current auth context      |

#### Bypassing Permissions In Custom Business

In some cases, it may be needed to bypass the permission system that restricts users, for example, if there are
async workflow actions which don't have a security context associated with them.

```java

    @Autowired
    AuthService authService;
    
    @Autowired
    ContentService contentService;

    public void myBusiness(){

        Object thing = authService.withSystemUser(()->{
          
	        return contentService.find(...);

        });

    }
```

[Back](../)
