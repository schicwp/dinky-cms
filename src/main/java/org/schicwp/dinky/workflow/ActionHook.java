package org.schicwp.dinky.workflow;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;

import java.util.Map;

/**
 * A workflow callback used to execute business as part of a workflow.
 */
public interface ActionHook {

    /**
     * Executes an action hook. The "actionConfig" is per execution config that can
     * be merged with the workflow scoped config as part of {@link ActionHookFactory}
     *
     *
     * @param content the content that the workflow is operating on
     * @param actionConfig the config specific to this workflow action execution
     */
    void execute(Content content, ContentMap actionConfig);
}
