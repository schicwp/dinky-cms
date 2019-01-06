package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.model.type.Permission;
import org.schicwp.workflow.ActionHook;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class SetPermissions implements ActionHook {


    private Permission owner = null;
    private Permission group = null;
    private Permission other = null;

    public SetPermissions(Map<String, String> config){
        owner = Permission.fromString(config.get("owner"));
        group = Permission.fromString(config.get("group"));
        other = Permission.fromString(config.get("other"));
    }

    @Override
    public String getName() {
        return "SetPermissions";
    }

    @Override
    public void execute(Content content, Map<String,Object> actionConfig) {



        Permission owner = this.owner;
        Permission group = this.group;
        Permission other = this.other;

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


    }
}
