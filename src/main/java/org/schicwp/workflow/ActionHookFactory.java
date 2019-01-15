package org.schicwp.workflow;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface ActionHookFactory {

    String getName();

    ActionHook createActionHook(Map<String,Object> config);
}
