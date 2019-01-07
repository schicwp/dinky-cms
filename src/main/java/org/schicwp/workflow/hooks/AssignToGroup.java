package org.schicwp.workflow.hooks;

import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AssignToGroup implements ActionHookFactory {


    @Override
    public String getName() {
        return "AssignToGroup";
    }


    @Override
    public ActionHook createActionHook(Map<String, String> config) {
        return (content, actionConfig) -> {
            if (actionConfig.containsKey("group"))
                content.setGroup((String)actionConfig.get("group"));
        };
    }

}
