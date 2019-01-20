package org.schicwp.dinky.model.type;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;

import java.util.Collection;

/**
 * Created by will.schick on 1/5/19.
 */
public class Field {

    private final FieldType fieldType;
    private final ContentMap fieldConfiguration;

    private final boolean required;
    private final String name;
    private final boolean indexed;

    public Field(FieldType fieldType, boolean required, ContentMap fieldConfiguration, String name, boolean indexed) {
        this.fieldType = fieldType;
        this.required = required;
        this.fieldConfiguration = fieldConfiguration;
        this.name = name;
        this.indexed = indexed;
    }

    public boolean isRequired() {
        return required;
    }

    public String getName() {
        return name;
    }

    public boolean validateSubmission(Object object, Collection<String> errors){
        return fieldType.validateSubmission(object, fieldConfiguration, errors);
    }

    public boolean isIndexed() {
        return indexed;
    }

    public Object convertSubmission(Object object,  Content content){
        return fieldType.convertSubmission(object, fieldConfiguration,content);
    }

    public ContentMap getFieldConfiguration() {
        return fieldConfiguration;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
