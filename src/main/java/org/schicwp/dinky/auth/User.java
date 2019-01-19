package org.schicwp.dinky.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by will.schick on 1/5/19.
 */
public class User {

    private String username;
    private List<String> groups = new ArrayList<>();

    private boolean systemUser = false;

    public User(String username,List<String> groups, boolean systemUser){
        this.username = username;
        this.groups = groups;
        this.systemUser = systemUser;
    }

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

    public boolean isSystemUser() {
        return systemUser;
    }
}
