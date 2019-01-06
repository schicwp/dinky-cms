package org.schicwp.model.type.fields;

import org.schicwp.model.type.FieldType;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class StringField implements FieldType{


    @Override
    public String getFieldType() {
        return "String";
    }

    @Override
    public boolean validate(Object object, Map<String, String> properties, Collection<String> errors) {

        if (!(object instanceof String)){
            errors.add("Wrong type, expected String");
            return false;
        }

        String value = (String)object;

        if (properties.containsKey("regex") && !value.matches(properties.get("regex"))) {
            errors.add("Regex failed");
            return false;

        }

        return true;
    }
}
