package org.schicwp.dinky.config;

import org.schicwp.dinky.workflow.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by will.schick on 1/4/19.
 */
public class WorkflowConfig {

    private String name;
    private Collection<StateConfig> states = new ArrayList<>();
    private Collection<ActionConfig> actions = new ArrayList<>();



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<StateConfig> getStates() {
        return states;
    }

    public void setStates(Collection<StateConfig> states) {
        this.states = states;
    }


    public Collection<ActionConfig> getActions() {
        return actions;
    }

    public void setActions(Collection<ActionConfig> actions) {
        this.actions = actions;
    }
}
