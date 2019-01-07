package org.schicwp.workflow;

import org.schicwp.workflow.hooks.AssignToGroup;
import org.schicwp.workflow.hooks.AssignToUser;
import org.schicwp.workflow.hooks.SetPermissions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface ActionHookFactory {

    String getName();

    ActionHook createActionHook(Map<String,String> config);
}
