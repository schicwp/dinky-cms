package org.schicwp.model.type.fields;

import org.schicwp.model.Content;
import org.schicwp.model.type.FieldType;
import org.schicwp.model.type.FieldTypeFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */

@Component
public class StringFieldFactory implements FieldTypeFactory{


    @Override
    public String getName() {
        return "String";
    }

    @Override
    public FieldType createFieldType() {
        return new StringField();
    }

    public static class StringField implements FieldType {

        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {

            if (!(object instanceof String)) {
                errors.add("Wrong type, expected String");
                return false;
            }

            String value = (String) object;

            if (properties.containsKey("regex") && !value.matches(properties.get("regex").toString())) {
                errors.add("Regex failed");
                return false;

            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content content) {
            return (String) input;
        }
    }
}
