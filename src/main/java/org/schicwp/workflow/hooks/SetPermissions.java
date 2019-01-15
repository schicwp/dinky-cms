package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.model.Permission;
import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
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
    public ActionHook createActionHook(Map<String, Object> config) {


        Permission owner = fromObject(config.get("owner"));

        Map<String,Permission> group = fromMap(  config.get("group"));

        return (content, actionConfig) -> {

            Permission _owner = fromObject(config.get("owner"));

            Map<String,Permission> _group = fromMap(  config.get("group"));

            if (_owner != null)
                content.getPermissions().setOwner(_owner);
            else if (owner != null)
                content.getPermissions().setOwner(owner);

            group.forEach((s, permission) -> content.getPermissions().getGroup().put(s,permission));
            _group.forEach((s, permission) -> content.getPermissions().getGroup().put(s,permission));




        };
    }

    private Map<String, Permission> fromMap( Object o) {
        Map<String,Object> groupConfig = (Map<String, Object>) o;

        Map<String,Permission> group = new HashMap<>();

        if (group != null){
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
