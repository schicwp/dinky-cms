package org.schicwp.dinky.workflow;

import org.schicwp.dinky.model.ContentMap;

import java.util.Map;

/**
 * Factory for a given named action hook. This will create an action hook instance that can be used for
 * a workflow
 */
public interface ActionHookFactory {

    /**
     * The name of the action hook. This will be used to uniquely indentify this hook type.
     *
     * @return the name of the action hook
     */
    String getName();

    /**
     * Creates an action hook, using the given config. The config is workflow scoped config
     * that can be merged with per instance config as part of the {@link ActionHook} execute() config
     *
     * @param config the action hook configuration
     * @return a configured action hook
     */
    ActionHook createActionHook(ContentMap config);
}
