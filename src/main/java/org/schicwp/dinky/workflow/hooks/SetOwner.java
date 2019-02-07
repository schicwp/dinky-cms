package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class SetOwner implements ActionHookFactory {


    @Autowired
    AuthService authService;

    @Override
    public String getName() {
        return "SetOwner";
    }

    @Override
    public ActionHook createActionHook(ContentMap config) {

        String globalUser = config.getAsOrDefault("user",null);


        return (content, actionConfig) -> {

            String user = actionConfig.getAsOrDefault("user",globalUser);

            if (user == null)
                user = authService.getCurrentUser().getUsername();

            content.setOwner(user);

        };
    }

}
