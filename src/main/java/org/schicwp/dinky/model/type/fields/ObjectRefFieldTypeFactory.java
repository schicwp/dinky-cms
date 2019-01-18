package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.FieldType;
import org.schicwp.dinky.model.type.FieldTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/6/19.
 */
@Component
public class ObjectRefFieldTypeFactory implements FieldTypeFactory {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public String getName() {
        return "objectRef";
    }

    @Override
    public FieldType createFieldType() {
        return new ObjectRefField(mongoTemplate);
    }

    public static class ObjectRefField implements FieldType {

        private final MongoTemplate mongoTemplate;

        public ObjectRefField(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {

            Content referencedContent = mongoTemplate.findById(object,Content.class);

            if (referencedContent == null) {
                errors.add("Cannot find referenced object");
                return false;

            }

            if ( properties.containsKey("referencedType") ) {
                if (!properties.get("referencedType").toString().equalsIgnoreCase(referencedContent.getType()))
                    errors.add("Referenced object is wrong type");{
                    return false;
                }
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content content) {


            return input;
        }
    }
}
