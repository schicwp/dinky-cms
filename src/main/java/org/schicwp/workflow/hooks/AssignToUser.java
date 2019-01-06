package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.workflow.ActionHook;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class AssignToUser implements ActionHook {


    @Override
    public String getName() {
        return "AssignToUser";
    }

    @Override
    public void execute(Content content, Map<String,Object> actionConfig) {

        if (actionConfig.containsKey("user"))
            content.setOwner((String)actionConfig.get("user"));


    }
}
