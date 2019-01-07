package org.schicwp.model.type.fields;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.schicwp.model.Content;
import org.schicwp.model.type.FieldType;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/6/19.
 */
public class ImageField implements FieldType {


    private final GridFsTemplate gridFsTemplate;

    public ImageField(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }


    @Override
    public String getFieldType() {
        return null;
    }

    @Override
    public boolean validateSubmission(Object object, Map<String, String> properties, Collection<String> errors) {

        return (object instanceof MultipartFile);
    }

    @Override
    public Object convertSubmission(Object input, Map<String, String> properties, Content content) {

        try {

            MultipartFile multipartFile = (MultipartFile) input;

            DBObject metaData = new BasicDBObject();
            metaData.put("contentType", multipartFile.getContentType());
            metaData.put("name", multipartFile.getName());
            metaData.put("filename", multipartFile.getOriginalFilename());
            metaData.put("size", multipartFile.getSize());


            return  gridFsTemplate.store(
                    multipartFile.getInputStream(),
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    metaData
            );

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
