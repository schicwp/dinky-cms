package org.schicwp.dinky.workflow;

import org.schicwp.dinky.model.Content;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface ActionHook {

    public void execute(Content content, Map<String,Object> actionConfig);
}
