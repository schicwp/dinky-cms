package org.schicwp.dinky.content

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.type.Field
import org.schicwp.dinky.model.type.FieldType
import org.schicwp.dinky.model.type.FieldTypeService
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class FieldSpec extends Specification {


    void "validateSubmission should use the field type method to validate"(){

        given:
        "a field type"
        FieldType fieldType = Mock(FieldType)

        and:
        "a field config"
        ContentMap contentMap = new ContentMap()

       and:
       "a field"
        Field field =  new Field(
                fieldType,
                false,
                contentMap,
                "arf",
                false
        )

        and:
        "an error array"
        def errors = []

        when:
        "validated"
        def result = field.validateSubmission("test",errors)

        then:
        "the field type should be used to validate"
        1 * fieldType.validateSubmission("test",contentMap,errors) >> expected

        and:
        result == expected


        where:
        expected << [true,false]

    }

    void "convertSubmission should use the field type method to convert"(){

        given:
        "a field type"
        FieldType fieldType = Mock(FieldType)

        and:
        "a field config"
        ContentMap contentMap = new ContentMap()

        and:
        "a field"
        Field field =  new Field(
                fieldType,
                false,
                contentMap,
                "arf",
                false
        )

        and:
        "some content"
        Content content = new Content();

        when:
        "validated"
        def result = field.convertSubmission("test",content)

        then:
        "the field type should be used to validate"
        1 * fieldType.convertSubmission("test",contentMap,content) >> "meow"

        and:
        result == "meow"

    }

}
