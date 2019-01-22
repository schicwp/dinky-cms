package org.schicwp.dinky.model.type.fields

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.type.FieldType
import org.schicwp.dinky.model.type.FieldTypeService
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class CollectionFieldTypeSpec extends Specification{

    CollectionFieldType collectionFieldType;
    FieldTypeService fieldTypeService;

    void setup(){
        fieldTypeService = Mock(FieldTypeService)
        collectionFieldType = new CollectionFieldType(
                fieldTypeService: fieldTypeService
        );
    }

    void "the name should be Int"(){
        expect:
         collectionFieldType.name == "Collection"
    }

    void "validate should accept null"(){
        expect:
        collectionFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should reject non collections"(){
        expect:
        !collectionFieldType.validateSubmission("ASD",new ContentMap(),[])
    }

    void "validate should accept collections if the type is right"(){

        given:
        "a type"
        FieldType fieldType = Mock(FieldType)

        when:
        def result = collectionFieldType.validateSubmission(["ASD"],new ContentMap(
                [
                        collectionType:[
                            type:"arf",
                            config: [ ruff:"meow"]
                        ]
                ]
        ),[])

        then:
        "the field type should be fetched"
        1 * fieldTypeService.getFieldType("arf") >> fieldType

        and:
        "the collection members should be validated"
        1 * fieldType.validateSubmission("ASD",new ContentMap([ruff:"meow"]),[]) >> true


        and:
        result
    }

    void "validate should accept collections if the type is right with no config"(){

        given:
        "a type"
        FieldType fieldType = Mock(FieldType)

        when:
        def result = collectionFieldType.validateSubmission(["ASD"],new ContentMap(
                [
                        collectionType:[
                                type:"arf"
                        ]
                ]
        ),[])

        then:
        "the field type should be fetched"
        1 * fieldTypeService.getFieldType("arf") >> fieldType

        and:
        "the collection members should be validated"
        1 * fieldType.validateSubmission("ASD",new ContentMap(),[]) >> true


        and:
        result
    }

    void "validate should accept collections if the type is wrong"(){

        given:
        "a type"
        FieldType fieldType = Mock(FieldType)

        when:
        def result = collectionFieldType.validateSubmission(["ASD"],new ContentMap(
                [
                        collectionType:[
                                type:"arf",
                                config: [ ruff:"meow"]
                        ]
                ]
        ),[])

        then:
        "the field type should be fetched"
        1 * fieldTypeService.getFieldType("arf") >> fieldType

        and:
        "the collection members should be validated"
        1 * fieldType.validateSubmission("ASD",new ContentMap([ruff:"meow"]),[]) >> false


        and:
        !result
    }

    void "convert should use field type conversion"(){

        given:
        "a type"
        FieldType fieldType = Mock(FieldType)

        and:
        "some content"
        Content content = new Content();

        when:
        def result = collectionFieldType.convertSubmission(["ASD"],new ContentMap(
                [
                        collectionType:[
                                type:"arf",
                                config: [ ruff:"meow"]
                        ]
                ]
        ),content)

        then:
        "the field type should be fetched"
        1 * fieldTypeService.getFieldType("arf") >> fieldType

        and:
        "the collection members should be validated"
        1 * fieldType.convertSubmission("ASD",new ContentMap([ruff:"meow"]),content) >> "asd"


        and:
        result == ["asd"]
    }

    void "convert should accept no config"(){

        given:
        "a type"
        FieldType fieldType = Mock(FieldType)

        and:
        "some content"
        Content content = new Content();

        when:
        def result = collectionFieldType.convertSubmission(["ASD"],new ContentMap(
                [
                        collectionType:[
                                type:"arf"
                        ]
                ]
        ),content)

        then:
        "the field type should be fetched"
        1 * fieldTypeService.getFieldType("arf") >> fieldType

        and:
        "the collection members should be validated"
        1 * fieldType.convertSubmission("ASD",new ContentMap(),content) >> "asd"


        and:
        result == ["asd"]
    }

    void "convert should accept null"(){

        expect:
        collectionFieldType.convertSubmission(null,new ContentMap(
                [
                        collectionType:[
                                type:"arf",
                                config: [ ruff:"meow"]
                        ]
                ]
        ),new Content()) == null


    }







}
