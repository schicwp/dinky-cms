package org.schicwp.model;

//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;

import org.schicwp.model.type.Permission;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/4/19.
 */
@Document
@org.springframework.data.elasticsearch.annotations.Document(indexName = "content", type = "contentType")
public class Content {

    @Indexed
    @Id
    String id;
    int version = 0;
    @Indexed
    Date created = new Date();
    @Indexed
    Date modified = new Date();
    @Indexed
    String state;
    @Indexed
    String type;
    @Indexed
    String owner;
    @Indexed
    String group;

    @Indexed
    Permission ownerPermissions = Permission.RW;
    @Indexed
    Permission groupPermissions = Permission.R;
    @Indexed
    Permission otherPermissions = Permission.NONE;








    Map<String,Object> content = new HashMap<>();

    public Map<String, Object> getContent() {
        return content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Content merge(Map<String,Object> properties){

        this.content.putAll(properties);
        this.version++;
        this.modified = new Date();

        return this;
    }

    public String getId() {
        return id;
    }


    public Date getCreated() {
        return created;
    }

    public String getGroup() {
        return group;
    }

    public String getOwner() {
        return owner;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Permission getGroupPermissions() {
        return groupPermissions;
    }

    public Permission getOtherPermissions() {
        return otherPermissions;
    }

    public Permission getOwnerPermissions() {
        return ownerPermissions;
    }

    public void setGroupPermissions(Permission groupPermissions) {
        this.groupPermissions = groupPermissions;
    }

    public void setOtherPermissions(Permission otherPermissions) {
        this.otherPermissions = otherPermissions;
    }

    public void setOwnerPermissions(Permission ownerPermissions) {
        this.ownerPermissions = ownerPermissions;
    }
}
