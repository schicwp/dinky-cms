package org.schicwp.workflow;

import java.util.*;

/**
 * Created by will.schick on 1/4/19.
 */
public class Action {


    /*
    name: Publish
    entryPoint: true
    nextState: Published
    sourceStates:
     - Draft
     - Published
    allowedGroups:
     - Editor
     */

    private String name;
    private boolean entryPoint = false;
    private String nextState;
    private Collection<String> sourceStates = new ArrayList<>();
    private Collection<String> allowedGroups = new ArrayList<>();
    private Collection<Map<String,String>> hooks = new ArrayList<>();
    private Map<String,ActionHook> actionHooks = new HashMap<>();


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

    public Map<String, ActionHook> getActionHooks() {
        return actionHooks;
    }

    public void setActionHooks(Map<String, ActionHook> actionHooks) {
        this.actionHooks = actionHooks;
    }

    public Collection<Map<String, String>> getHooks() {
        return hooks;
    }

    public void setHooks(Collection<Map<String, String>> hooks) {
        this.hooks = hooks;
    }
}
