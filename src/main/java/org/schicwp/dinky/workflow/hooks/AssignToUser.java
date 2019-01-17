package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class AssignToUser implements ActionHookFactory {


    @Override
    public String getName() {
        return "AssignToUser";
    }

    @Override
    public ActionHook createActionHook(Map<String, Object> config) {
        return (content, actionConfig) -> {
            if (actionConfig.containsKey("user"))
                content.setOwner((String)actionConfig.get("user"));
        };
    }

}
