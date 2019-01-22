package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class Assign implements ActionHookFactory {


    @Override
    public String getName() {
        return "Assign";
    }

    @Override
    public ActionHook createActionHook(ContentMap config) {

        String globalUser = config.getAsOrDefault("user",null);
        String globalGroup = config.getAsOrDefault("group",null);


        return (content, actionConfig) -> {


            String user = actionConfig.getAsOrDefault("user",globalUser);
            String group = actionConfig.getAsOrDefault("group",globalGroup);

            if (user != null)
                content.setAssignedUser(user);
            if (group != null)
                content.setAssignedGroup(group);

        };
    }

}
