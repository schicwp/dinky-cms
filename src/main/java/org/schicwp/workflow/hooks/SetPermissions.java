package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.model.type.Permission;
import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class SetPermissions implements ActionHookFactory {

    

    @Override
    public String getName() {
        return "SetPermissions";
    }

    @Override
    public ActionHook createActionHook(Map<String, String> config) {
        Permission _owner = Permission.fromString(config.get("owner"));;
        Permission _group = Permission.fromString(config.get("group"));
        Permission _other = Permission.fromString(config.get("group"));

        return (content, actionConfig) -> {
            Permission owner = _owner;
            Permission group = _group;
            Permission other = _other;

            if (actionConfig != null) {

                if (actionConfig.containsKey("owner"))
                    owner = Permission.fromString(actionConfig.get("owner").toString());
                if (actionConfig.containsKey("group"))
                    group = Permission.fromString(actionConfig.get("group").toString());
                if (actionConfig.containsKey("other"))
                    other = Permission.fromString(actionConfig.get("other").toString());
            }

            if (owner != null)
                content.setOwnerPermissions(owner);
            if (group != null)
                content.setGroupPermissions(group);
            if (other != null)
                content.setOtherPermissions(other);
        };
    }


}
