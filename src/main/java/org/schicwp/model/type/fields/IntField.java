package org.schicwp.model.type.fields;

import org.schicwp.model.type.FieldType;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class IntField implements FieldType {
    @Override
    public String getFieldType() {
        return "Int";
    }

    @Override
    public boolean validate(Object object, Map<String, String> properties, Collection<String> errors) {
        if  (!Integer.class.isAssignableFrom(object.getClass())){
            errors.add("Should be integer");
            return false;
        }

        return true;
    }
}
