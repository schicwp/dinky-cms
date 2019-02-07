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

    private final String name;
    private boolean entryPoint = false;
    private final String nextState;
    private final Collection<String> sourceStates;
    private final Collection<String> allowedGroups;
    private final List<NamedActionHook> actionHooks;

    public Action(String name,
                  boolean entryPoint,
                  String nextState,
                  Collection<String> sourceStates,
                  Collection<String> allowedGroups,
                  List<NamedActionHook> actionHooks) {
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


    public boolean isEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(boolean entryPoint) {
        this.entryPoint = entryPoint;
    }

    public String getNextState() {
        return nextState;
    }


    public Collection<String> getSourceStates() {
        return sourceStates;
    }


    public Collection<String> getAllowedGroups() {
        return allowedGroups;
    }


    public List<NamedActionHook> getActionHooks() {
        return actionHooks;
    }



}
