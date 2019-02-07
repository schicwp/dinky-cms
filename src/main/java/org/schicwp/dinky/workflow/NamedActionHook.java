package org.schicwp.dinky.workflow;

/**
 * Created by will.schick on 2/7/19.
 */
public class NamedActionHook {
    private final String name;
    private final ActionHook actionHook;

    public NamedActionHook(String name, ActionHook actionHook) {
        this.name = name;
        this.actionHook = actionHook;
    }


    public String getName() {
        return name;
    }

    public ActionHook getActionHook() {
        return actionHook;
    }
}
