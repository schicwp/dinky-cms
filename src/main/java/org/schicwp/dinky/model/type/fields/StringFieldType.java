package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by will.schick on 1/5/19.
 */

@Component
public class StringFieldType implements FieldType {


    @Override
    public String getName() {
        return "String";
    }


    @Override
    public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {

        if (object == null)
            return true;

        if (!(object instanceof String)) {
            errors.add("Wrong type, expected String");
            return false;
        }

        String value = (String) object;
        if (properties.containsKey("regex") &&
                !value.matches(properties.getAs("regex", String.class))) {
            errors.add("Regex failed");
            return false;
        }

        if (properties.containsKey("minLength") &&
                value.length()
                        < properties.getAs("minLength", Integer.class)) {
            errors.add("Does not meet minLength");
            return false;
        }

        if (properties.containsKey("maxLength") &&
                value.length()
                        > properties.getAs("maxLength", Integer.class)) {
            errors.add("Above maxLength");
            return false;
        }

        if (properties.containsKey("allowedValues") &&
                !properties
                        .getAsCollectionOf("allowedValues", String.class)
                        .contains(value)) {
            errors.add("Not in allowed values");
            return false;
        }

        return true;
    }

    @Override
    public Object convertSubmission(Object input, ContentMap properties, Content content) {
        return input;
    }

}
