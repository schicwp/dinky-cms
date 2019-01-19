package org.schicwp.dinky.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/14/19.
 */
@Document
public class ContentPermissions {


    private Permission owner = new Permission(true, true);
    private Permission assignee = new Permission(true,true);
    private Permission other = new Permission(false, false);
    private Map<String,Permission> group = new HashMap<>();

    public Permission getOwner() {
        return owner;
    }

    public void setOwner(Permission owner) {
        this.owner = owner;
    }

    public Map<String, Permission> getGroup() {
        return group;
    }

    public Permission getOther() {
        return other;
    }

    public void setOther(Permission other) {
        this.other = other;
    }

    public void setGroup(Map<String, Permission> group) {
        this.group = group;
    }

    public Permission getAssignee() {
        return assignee;
    }

    public void setAssignee(Permission assignee) {
        this.assignee = assignee;
    }
}
