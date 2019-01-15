package org.schicwp.model.type.fields;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.schicwp.model.Content;
import org.schicwp.model.type.FieldType;
import org.schicwp.model.type.FieldTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

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

            if (referencedContent == null)
                return false;

            if ( properties.containsKey("referencedType") ) {
                if (!properties.get("referencedType").toString().equalsIgnoreCase(referencedContent.getType()))
                    return false;
            }

            return (object instanceof String);
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content content) {


            return input;
        }
    }
}
