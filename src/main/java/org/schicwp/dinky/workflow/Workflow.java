package org.schicwp.dinky.workflow;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by will.schick on 1/4/19.
 */
public class Workflow{

    private final String name;
    private final Collection<State> states;
    private final Collection<Action> actions;

    public Workflow(String name, Collection<State> states, Collection<Action> actions) {
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
                .filter(a->a.getSourceStates().contains(state))
                .filter(a->a.getName().equalsIgnoreCase(action))
                .findFirst();
    }


    public String getName() {
        return name;
    }

    public Collection<State> getStates() {
        return states;
    }

    public Collection<Action> getActions() {
        return actions;
    }

}
