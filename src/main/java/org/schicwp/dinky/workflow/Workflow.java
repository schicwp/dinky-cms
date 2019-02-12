package org.schicwp.dinky.workflow;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by will.schick on 1/4/19.
 */
public class Workflow{

    private final String name;
    private final List<State> states;
    private final List<Action> actions;

    public Workflow(String name, List<State> states, List<Action> actions) {
        this.name = name;
        this.states = states;
        this.actions = actions;
    }

    public Optional<Action> getActionFromState(String state, String action){
        if (state == null)
            return actions
                    .stream()
                    .filter(a->a.isEntryPoint())
                    .filter(a->a.getName().equalsIgnoreCase(action))
                    .findFirst();




        return actions
                .stream()
                .filter(a->a.getSourceStates().contains(state) || a.getSourceStates().size() == 0)
                .filter(a->a.getName().equalsIgnoreCase(action))
                .findFirst();
    }


    public String getName() {
        return name;
    }

    public List<State> getStates() {
        return states;
    }

    public List<Action> getActions() {
        return actions;
    }

}
