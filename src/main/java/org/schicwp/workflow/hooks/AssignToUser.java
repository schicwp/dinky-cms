package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
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
    public ActionHook createActionHook(Map<String, String> config) {
        return (content, actionConfig) -> {
            if (actionConfig.containsKey("user"))
                content.setOwner((String)actionConfig.get("user"));
        };
    }

}
