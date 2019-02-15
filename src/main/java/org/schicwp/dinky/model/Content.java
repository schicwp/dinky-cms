package org.schicwp.dinky.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/4/19.
 */
@Document
public class Content {

    @Indexed
    @Id
    private String id;
    @Indexed
    private int version = 0;
    @Indexed
    private Date created = new Date();
    @Indexed
    private Date modified = new Date();
    @Indexed
    private String workflow;
    @Indexed
    private String state;
    @Indexed
    private String type;
    @Indexed
    private String owner;
    @Indexed
    private String createdBy;
    @Indexed
    private String modifiedBy;
    @Indexed
    private String assignedUser;
    @Indexed
    private String assignedGroup;
    @Indexed
    private String name;
    @Indexed
    private Map<String,Integer> searchVersions = new HashMap<>();
    private ContentPermissions permissions = new ContentPermissions();
    private ContentMap content = new ContentMap();

    public Content(){}

    public Content(String id, String owner, String type){
        this.id = id;
        this.owner = owner;
        this.createdBy = owner;
        this.modifiedBy = owner;
        this.type = type;
    }

    public Content(Content content){
        this.id = content.id;
        this.version = content.version;
        this.created = content.created;
        this.modified = content.modified;
        this.workflow = content.workflow;
        this.state = content.state;
        this.type = content.type;
        this.owner = content.owner;
        this.createdBy = content.createdBy;
        this.modifiedBy = content.modifiedBy;
        this.assignedUser = content.assignedUser;
        this.assignedGroup = content.assignedGroup;
        this.name = content.name;
        this.searchVersions = content.searchVersions;
        this.permissions = content.permissions;
        this.content = content.content;
    }

    public Content merge(Map<String,Object> properties){

        if (properties != null)
            this.content.putAll(properties);

        this.version++;
        this.modified = new Date();

        return this;
    }

    public ContentMap getContent() {
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

    public void setContent(ContentMap content) {
        this.content = content;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }


    public Date getCreated() {
        return created;
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

    public String getName() {
        if (name == null){
            return this.id;
        }

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<String, Integer> getSearchVersions() {
        return searchVersions;
    }

    public void setSearchVersions(Map<String, Integer> searchVersions) {
        this.searchVersions = searchVersions;
    }

    public ContentPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(ContentPermissions permissions) {
        this.permissions = permissions;
    }

    public String getAssignedGroup() {
        return assignedGroup;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public void setAssignedGroup(String assignedGroup) {
        this.assignedGroup = assignedGroup;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", workflow='" + workflow + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
