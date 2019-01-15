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
public class IntFieldFactory implements FieldTypeFactory {


    @Override
    public String getName() {
        return "int";
    }

    @Override
    public FieldType createFieldType() {
        return new IntField();
    }

    public static class IntField implements FieldType {
        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {
            if (!Integer.class.isAssignableFrom(object.getClass())) {
                errors.add("Should be integer");
                return false;
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content content) {
            return (Integer) input;
        }
    }
}
