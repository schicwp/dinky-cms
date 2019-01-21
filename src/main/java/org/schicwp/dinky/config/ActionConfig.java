package org.schicwp.dinky.config;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by will.schick on 1/4/19.
 */
public class ActionConfig {

    private String name;
    private boolean entryPoint = false;
    private String nextState;
    private Collection<String> sourceStates = new ArrayList<>();
    private Collection<String> allowedGroups = new ArrayList<>();
    private Collection<ActionHookConfig> hooks = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(boolean entryPoint) {
        this.entryPoint = entryPoint;
    }

    public String getNextState() {
        return nextState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
    }

    public Collection<String> getSourceStates() {
        return sourceStates;
    }

    public void setSourceStates(Collection<String> sourceStates) {
        this.sourceStates = sourceStates;
    }

    public Collection<String> getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(Collection<String> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public Collection<ActionHookConfig> getHooks() {
        return hooks;
    }

    public void setHooks(Collection<ActionHookConfig> hooks) {
        this.hooks = hooks;
    }
}
