package org.schicwp.dinky.config;

import org.schicwp.dinky.model.ContentMap;

import java.util.List;

/**
 * Created by will.schick on 1/20/19.
 */
public class FieldConfig {

    private String name;
    private String type;
    private boolean required = false;
    private boolean indexed = false;
    private ContentMap config = new ContentMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public ContentMap getConfig() {
        return config;
    }

    public void setConfig(ContentMap config) {
        this.config = config;
    }
}
