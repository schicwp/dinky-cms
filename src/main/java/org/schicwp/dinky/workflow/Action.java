package org.schicwp.dinky.workflow;

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
    private Map<String,ActionHook> actionHooks = new HashMap<>();

    public Action(String name,
                  boolean entryPoint,
                  String nextState,
                  Collection<String> sourceStates,
                  Collection<String> allowedGroups,
                  Map<String, ActionHook> actionHooks) {
        this.name = name;
        this.entryPoint = entryPoint;
        this.nextState = nextState;
        this.sourceStates = sourceStates;
        this.allowedGroups = allowedGroups;
        this.actionHooks = actionHooks;
    }

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

}
