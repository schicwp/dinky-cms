package org.schicwp.dinky.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by will.schick on 1/4/19.
 */
public class ActionConfig {

    private String name;
    private boolean entryPoint = false;
    private String nextState;
    private List<String> sourceStates = new ArrayList<>();
    private List<String> allowedGroups = new ArrayList<>();
    private List<ActionHookConfig> hooks = new ArrayList<>();


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

    public void setSourceStates(List<String> sourceStates) {
        this.sourceStates = sourceStates;
    }

    public Collection<String> getAllowedGroups() {
        return allowedGroups;
    }

    public void setAllowedGroups(List<String> allowedGroups) {
        this.allowedGroups = allowedGroups;
    }

    public Collection<ActionHookConfig> getHooks() {
        return hooks;
    }

    public void setHooks(List<ActionHookConfig> hooks) {
        this.hooks = hooks;
    }
}
