package org.schicwp.model.type.fields;

import org.schicwp.model.Content;
import org.schicwp.model.type.FieldType;
import org.schicwp.model.type.FieldTypeFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/14/19.
 */
@Component
public class DateFieldTypeFactory implements FieldTypeFactory {

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public FieldType createFieldType() {
        return new DateFieldType();
    }

    public static class DateFieldType implements FieldType{

        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                dateFormat.parse((String) object);
            }catch (Exception e){
                return false;
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content owner) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                return dateFormat.parse((String) input);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
