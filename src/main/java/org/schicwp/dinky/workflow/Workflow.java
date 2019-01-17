package org.schicwp.dinky.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by will.schick on 1/4/19.
 */
public class Workflow{

    String name;
    Collection<State> states = new ArrayList<>();
    Collection<Action> actions = new ArrayList<>();

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

    public void setName(String name) {
        this.name = name;
    }

    public Collection<State> getStates() {
        return states;
    }

    public void setStates(Collection<State> states) {
        this.states = states;
    }


    public Collection<Action> getActions() {
        return actions;
    }

    public void setActions(Collection<Action> actions) {
        this.actions = actions;
    }
}
