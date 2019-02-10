
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
