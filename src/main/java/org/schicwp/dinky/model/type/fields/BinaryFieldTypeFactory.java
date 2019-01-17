package org.schicwp.dinky.model.type.fields;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.FieldType;
import org.schicwp.dinky.model.type.FieldTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BinaryFieldTypeFactory implements FieldTypeFactory {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Override
    public String getName() {
        return "binary";
    }

    @Override
    public FieldType createFieldType() {
        return new BinaryField(gridFsTemplate);
    }

    public static class BinaryField implements FieldType {

        private final GridFsTemplate gridFsTemplate;

        public BinaryField(GridFsTemplate gridFsTemplate) {
            this.gridFsTemplate = gridFsTemplate;
        }

        @Override
        public boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors) {

            return (object instanceof MultipartFile);
        }

        @Override
        public Object convertSubmission(Object input, Map<String, Object> properties, Content content) {

            try {

                MultipartFile multipartFile = (MultipartFile) input;

                DBObject metaData = new BasicDBObject();
                metaData.put("contentType",multipartFile.getContentType());
                metaData.put("filename",multipartFile.getOriginalFilename());

                return gridFsTemplate.store(
                        multipartFile.getInputStream(),
                        UUID.randomUUID().toString(),
                        multipartFile.getContentType(),
                        metaData
                ).toString();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
