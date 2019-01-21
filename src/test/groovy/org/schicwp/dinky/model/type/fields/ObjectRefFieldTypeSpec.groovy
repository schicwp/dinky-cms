package org.schicwp.dinky.model.type.fields

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class ObjectRefFieldTypeSpec extends Specification{

    ObjectRefFieldType objectRefFieldType;
    MongoTemplate mongoTemplate;

    void setup(){
        mongoTemplate = Mock(MongoTemplate)
        objectRefFieldType = new ObjectRefFieldType(
                mongoTemplate: mongoTemplate
        );
    }

    void "the name should be Int"(){
        expect:
        objectRefFieldType.name == "ObjectRef"
    }

    void "validate should accept null"(){
        expect:
        objectRefFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should reject if reference can't be found"(){

        when:
        "the submission is validated"
        def result = objectRefFieldType.validateSubmission("arf",new ContentMap(),[])

        then:
        "the content should be fetched"
        1 * mongoTemplate.findById("arf",Content.class) >> null

        and:
        "the result should be false"
        !result
    }

    void "validate should accept if reference can be found"(){

        when:
        "the submission is validated"
        def result = objectRefFieldType.validateSubmission("arf",new ContentMap(),[])

        then:
        "the content should be fetched"
        1 * mongoTemplate.findById("arf",Content.class) >> new Content()

        and:
        "the result should be false"
        result
    }

    void "validate should reject if reference can be found but type is wrong"(){

        when:
        "the submission is validated"
        def result = objectRefFieldType.validateSubmission("arf",new ContentMap([referencedType:"dog"]),[])

        then:
        "the content should be fetched"
        1 * mongoTemplate.findById("arf",Content.class) >> new Content(type: "cat")

        and:
        "the result should be false"
        !result
    }

    void "validate should accept if reference can be found but type is right"(){

        when:
        "the submission is validated"
        def result = objectRefFieldType.validateSubmission("arf",new ContentMap([referencedType:"dog"]),[])

        then:
        "the content should be fetched"
        1 * mongoTemplate.findById("arf",Content.class) >> new Content(type: "dog")

        and:
        "the result should be false"
        result
    }

    void "convertSubmission should return input"(){
        given:
        "an input"
        def o = new Object();

        expect:
        o.is(objectRefFieldType.convertSubmission(o,new ContentMap(),new Content()))
    }


}
