package org.schicwp.workflow;

import org.schicwp.workflow.hooks.AssignToGroup;
import org.schicwp.workflow.hooks.AssignToUser;
import org.schicwp.workflow.hooks.SetPermissions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class ActionHookFactory {


    public ActionHook createActionHook(Map<String,String> config){
        switch (config.get("name")){
            case "AssignToGroup":
                return new AssignToGroup();
            case "AssignToUser":
                return new AssignToUser();
            case "SetPermissions":
                return new SetPermissions(config);
        }

        throw new RuntimeException();
    }
}
