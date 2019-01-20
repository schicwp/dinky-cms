package org.schicwp.dinky.config;

import org.schicwp.dinky.model.type.Field;

import java.util.Collection;
import java.util.List;

/**
 * Created by will.schick on 1/20/19.
 */
public class ContentTypeConfig {

    private String name;
    private Collection<String> workflows;
    private String nameField;
    private List<FieldConfig> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Collection<String> workflows) {
        this.workflows = workflows;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public List<FieldConfig> getFields() {
        return fields;
    }

    public void setFields(List<FieldConfig> fields) {
        this.fields = fields;
    }
}
