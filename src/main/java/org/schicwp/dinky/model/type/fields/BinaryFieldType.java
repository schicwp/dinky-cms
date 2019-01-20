package org.schicwp.dinky.model.type.fields;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.FieldType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by will.schick on 1/6/19.
 */
@Component
public class BinaryFieldType implements FieldType {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Override
    public String getName() {
        return "binary";
    }





        @Override
        public boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors) {

            if (object instanceof MultipartFile)
                return true;

            if (object instanceof String){
                String id = (String)object;

                if (gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id))) == null){
                    errors.add("Value is not a file id");
                    return false;
                }
            }

            return (object instanceof MultipartFile);
        }

        @Override
        public Object convertSubmission(Object input, ContentMap properties, Content content) {

            try {

                MultipartFile multipartFile = (MultipartFile) input;

                DBObject metaData = new BasicDBObject();
                metaData.put("contentType",multipartFile.getContentType());
                metaData.put("filename",multipartFile.getOriginalFilename());
                metaData.put("contentId",content.getId());

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
