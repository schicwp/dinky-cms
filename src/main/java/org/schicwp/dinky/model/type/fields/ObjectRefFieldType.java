package org.schicwp.dinky.model.type.fields;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/6/19.
 */
@Component
public class ObjectRefFieldType implements FieldType {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public String getName() {
        return "objectRef";
    }


        @Override
        public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {

            Content referencedContent = mongoTemplate.findById(object,Content.class);

            if (referencedContent == null) {
                errors.add("Cannot find referenced object");
                return false;

            }

            if ( properties.containsKey("referencedType") ) {
                if (!properties.get("referencedType").toString().equalsIgnoreCase(referencedContent.getType())){
                    errors.add("Referenced object is wrong type");
                    return false;
                }
            }

            return true;
        }

        @Override
        public Object convertSubmission(Object input, ContentMap properties, Content content) {


            return input;
        }

}
