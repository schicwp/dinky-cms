package org.schicwp.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    allowedRoles:
     - Editor
     */

    private String name;
    private boolean entryPoint = false;
    private String nextState;
    private Collection<String> sourceStates = new ArrayList<>();
    private Collection<String> allowedRoles = new ArrayList<>();
    private Collection<Map<String,String>> hooks = new ArrayList<>();
    private Collection<ActionHook> actionHooks = new ArrayList<>();


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

    public Collection<String> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(Collection<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public Collection<ActionHook> getActionHooks() {
        return actionHooks;
    }

    public void setActionHooks(Collection<ActionHook> actionHooks) {
        this.actionHooks = actionHooks;
    }

    public Collection<Map<String, String>> getHooks() {
        return hooks;
    }

    public void setHooks(Collection<Map<String, String>> hooks) {
        this.hooks = hooks;
    }
}
