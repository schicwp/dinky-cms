package org.schicwp.dinky.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by will.schick on 1/5/19.
 */
public class User {

    private String username;
    private List<String> groups = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
