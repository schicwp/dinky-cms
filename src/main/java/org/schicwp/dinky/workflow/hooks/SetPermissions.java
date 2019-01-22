package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.Permission;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    public ActionHook createActionHook(ContentMap config) {

        return (content, actionConfig) -> {

            Permission _owner = fromObject(
                    actionConfig.getOrDefault(
                            "owner",
                            config.getOrDefault("owner",null)
                    )
            );

            Permission _other = fromObject(
                    actionConfig.getOrDefault(
                            "other",
                            config.getOrDefault("other",null)
                    )
            );

            Permission _assignee = fromObject(
                    actionConfig.getOrDefault(
                            "assignee",
                            config.getOrDefault("assignee",null)
                    )
            );

            if (_owner != null)
                content.getPermissions().setOwner(_owner);
            if (_other != null)
                content.getPermissions().setOther(_other);
            if (_assignee != null)
                content.getPermissions().setAssignee(_assignee);


            Map<String,Permission> group = fromMap(  config.get("group"));
            Map<String,Permission> _group = fromMap(  actionConfig.get("group"));


            group.forEach((s, permission) -> content.getPermissions().getGroup().put(s,permission));
            _group.forEach((s, permission) -> content.getPermissions().getGroup().put(s,permission));




        };
    }

    private Map<String, Permission> fromMap( Object o) {
        Map<String,Object> groupConfig = (Map<String, Object>) o;

        Map<String,Permission> group = new HashMap<>();


        if (groupConfig != null){
            groupConfig.forEach( (k, v)-> group.put(k,fromObject(v)));
        }

        return group;


    }

    Permission fromObject(Object o){

        if (o == null)
            return null;

        Map<String, Boolean> map = (Map)o;

        return new Permission(map.get("read"),map.get("write"));
    }


}
