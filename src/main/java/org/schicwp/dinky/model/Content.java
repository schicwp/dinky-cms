package org.schicwp.dinky.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/4/19.
 */
@org.springframework.data.mongodb.core.mapping.Document
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
    String name;
    @Indexed
    Integer searchVersion = null;

    ContentPermissions permissions = new ContentPermissions();

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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getSearchVersion() {
        return searchVersion;
    }

    public void setSearchVersion(Integer searchVersion) {
        this.searchVersion = searchVersion;
    }

    public ContentPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(ContentPermissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
