package org.schicwp.dinky.model.type.fields

import com.mongodb.DBObject
import com.mongodb.client.gridfs.model.GridFSFile
import com.mongodb.gridfs.GridFSDBFile
import org.bson.BsonString
import org.bson.BsonValue
import org.bson.types.ObjectId
import org.elasticsearch.cluster.metadata.MetaData
import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.type.FieldType
import org.schicwp.dinky.model.type.FieldTypeService
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class BinaryFieldTypeSpec extends Specification{

    BinaryFieldType binaryFieldType;
    GridFsTemplate gridFsTemplate;
    MultipartFile multipartFile;

    void setup(){
        gridFsTemplate = Mock(GridFsTemplate)
        multipartFile = Mock(MultipartFile)


        binaryFieldType = new BinaryFieldType(
                gridFsTemplate: gridFsTemplate
        )

    }

    void "the name should be Binary"(){
        expect:
         binaryFieldType.name == "Binary"
    }

    void "validate should accept null"(){
        expect:
        binaryFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should accept multipart files"(){
        expect:
        binaryFieldType.validateSubmission(multipartFile,new ContentMap(),[])
    }

    void "validate should accept strings if they are the id of an asset"(){
        when:
        def result = binaryFieldType.validateSubmission("ASDASD",new ContentMap(),[])

        then:
        1 * gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("ASDASD")))>>
                new GridFSFile(new BsonString("ASD"),"a",1,1,new Date(),"a",null, null)

        and:
        result
    }

    void "validate should reject strings if they are not the id of an asset"(){
        when:
        def result = binaryFieldType.validateSubmission("ASDASD",new ContentMap(),[])

        then:
        1 * gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("ASDASD")))>>
                null
        and:
        !result
    }

    void "convert should convert return null for null"(){
        expect:
        binaryFieldType.convertSubmission(null,new ContentMap(),new Content()) == null
    }

    void "convert should convert return string for string"(){
        expect:
        binaryFieldType.convertSubmission("arf",new ContentMap(),new Content()) == "arf"
    }

    void "convert should throw if unkown"(){
        when:
        binaryFieldType.convertSubmission(123L,new ContentMap(),new Content()) == "arf"

        then:
        thrown(RuntimeException)
    }


    void "convert should convert save file and return id"(){


        given:
        InputStream stream = new ByteArrayInputStream("ard".bytes)

        _ * multipartFile.contentType >> "ruff/meow"
        _ * multipartFile.originalFilename >> "thefile.txt"
        _ * multipartFile.inputStream >> stream

        Date date = new Date(123456789l)

        when:
        def result = binaryFieldType.convertSubmission(multipartFile,new ContentMap(),new Content(id: "123"))

        then:
        "the file should be saved"
        1 * gridFsTemplate.store(stream,{String s->
            UUID.fromString(s)
            return true
        },"ruff/meow",{ DBObject metaData->
            return metaData.get("contentType") == "ruff/meow" &&
                    metaData.get('filename') =="thefile.txt" &&
                    metaData.get("contentId") == "123"
        }) >> new ObjectId("0001e240f92f0f097b09faa2")

        result == "0001e240f92f0f097b09faa2"


    }







}
