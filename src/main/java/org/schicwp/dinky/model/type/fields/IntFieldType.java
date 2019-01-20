package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class IntFieldType implements FieldType {


    @Override
    public String getName() {
        return "int";
    }


        @Override
        public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {
            if (!Integer.class.isAssignableFrom(object.getClass())) {
                errors.add("Should be integer");
                return false;
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, ContentMap properties, Content content) {
            return (Integer) input;
        }

}
