package org.schicwp.model.type;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class Field {

    private final FieldType fieldType;

    private final boolean required;

    private final Map<String,String> properites;

    private final String name;

    private final boolean indexed;

    public Field(FieldType fieldType, boolean required, Map<String, String> properites, String name, boolean indexed) {
        this.fieldType = fieldType;
        this.required = required;
        this.properites = properites;
        this.name = name;
        this.indexed = indexed;
    }

    public boolean isRequired() {
        return required;
    }

    public String getName() {
        return name;
    }

    public boolean validate(Object object){


        return fieldType.validate(object,properites, new ArrayList<>());
    }

    public boolean isIndexed() {
        return indexed;
    }
}
