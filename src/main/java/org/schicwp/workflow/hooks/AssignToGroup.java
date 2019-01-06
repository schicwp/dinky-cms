package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.workflow.ActionHook;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class AssignToGroup implements ActionHook {


    @Override
    public String getName() {
        return "AssignToGroup";
    }

    @Override
    public void execute(Content content, Map<String,Object> actionConfig) {

        if (actionConfig.containsKey("group"))
            content.setGroup((String)actionConfig.get("group"));


    }
}
