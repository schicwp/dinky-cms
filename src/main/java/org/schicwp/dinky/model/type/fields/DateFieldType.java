package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by will.schick on 1/14/19.
 */
@Component
public class DateFieldType implements FieldType {

    @Override
    public String getName() {
        return "date";
    }



        @Override
        public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {

            if (object == null)
                return true;

            if (object instanceof Date)
                return true;


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                dateFormat.parse((String) object);
            }catch (Exception e){
                errors.add("Cannot parse date");
                return false;
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, ContentMap properties, Content owner) {

            if (input == null)
                return null;

            if (input instanceof Date)
                return input;

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                return dateFormat.parse((String) input);
            } catch (Exception e){
                throw new RuntimeException(e);
            }

    }
}
