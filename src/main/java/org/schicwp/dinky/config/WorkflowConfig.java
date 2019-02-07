package org.schicwp.dinky.config;

import org.schicwp.dinky.workflow.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by will.schick on 1/4/19.
 */
public class WorkflowConfig {

    private String name;
    private List<StateConfig> states = new ArrayList<>();
    private List<ActionConfig> actions = new ArrayList<>();



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<StateConfig> getStates() {
        return states;
    }

    public void setStates(List<StateConfig> states) {
        this.states = states;
    }


    public Collection<ActionConfig> getActions() {
        return actions;
    }

    public void setActions(List<ActionConfig> actions) {
        this.actions = actions;
    }
}
