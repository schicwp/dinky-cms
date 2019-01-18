package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.FieldType;
import org.schicwp.dinky.model.type.FieldTypeFactory;
import org.schicwp.dinky.model.type.FieldTypeFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/14/19.
 */
@Component
public class CollectionFieldTypeFactory implements FieldTypeFactory {

    @Override
    public String getName() {
        return "collection";
    }

    @Override
    public FieldType createFieldType() {
        return new CollectionFieldType(fieldTypeFactoryService);
    }

    @Autowired
    FieldTypeFactoryService fieldTypeFactoryService;

    public static class CollectionFieldType implements FieldType{


        private  final FieldTypeFactoryService fieldTypeFactoryService;

        public CollectionFieldType(FieldTypeFactoryService fieldTypeFactoryService) {
            this.fieldTypeFactoryService = fieldTypeFactoryService;
        }


        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {

            if (! Collection.class.isAssignableFrom(object.getClass())) {
                errors.add("Must be collection type");
                return false;

            }

            Map<String,Object> collectionType = (Map<String,Object>)properties.get("collectionType");

            FieldType fieldType = fieldTypeFactoryService
                    .getFieldTypeFactory((String)collectionType.get("type"))
                    .createFieldType();

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
        public Object convertSubmission(Object input, Map<String, Object> properties, Content owner) {
            return input;
        }
    }
}
