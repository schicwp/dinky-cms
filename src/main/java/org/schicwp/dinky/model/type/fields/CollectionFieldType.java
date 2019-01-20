package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.schicwp.dinky.model.type.FieldTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by will.schick on 1/14/19.
 */
@Component
public class CollectionFieldType implements FieldType {

    @Override
    public String getName() {
        return "collection";
    }



    @Autowired
    FieldTypeService fieldTypeService;




        @Override
        public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {

            if (! Collection.class.isAssignableFrom(object.getClass())) {
                errors.add("Must be collection type");
                return false;

            }

            ContentMap collectionType = properties.getAsMap("collectionType");

            FieldType fieldType = fieldTypeService
                    .getFieldType(collectionType.getAs("type",String.class));

            Collection<?> collection = (Collection)object;

            for (Object o:collection){
                if (!fieldType.validateSubmission(o,collectionType,errors)) {
                    errors.add("One or more of collection is invalid");
                    return false;
                }
            }


            return true;
        }

        @Override
        public Object convertSubmission(Object input, ContentMap properties, Content owner) {
            return input;
        }

}
