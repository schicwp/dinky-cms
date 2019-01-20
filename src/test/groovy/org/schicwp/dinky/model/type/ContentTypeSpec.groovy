package org.schicwp.dinky.model.type

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.workflow.Workflow
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class ContentTypeSpec extends Specification{

    ContentType contentType

    Field field1
    Field field2

    Content content;

    void setup(){

        field1 = Mock(Field)
        field2 = Mock(Field)

        contentType = new ContentType("test",[field1,field2],["wf1"],"field1")

        content = new Content()
    }

    void "sanitize() should remove unknown fields"(){

        given:
        "some unkown fields"
        content.content.unkown = "test"
        content.content.field1 = "also"

        _*field1.name>> "field1"
        _*field2.name>> "field2"

        when:
        "the content is sanitized"
        def result = contentType.sanitize(content)

        then:
        result.is(content)
        content.content as Map == [
                field1:"also"
        ]
    }

    void "convert() should convert fields"(){

        given:
        "some unkown fields"
        content.content.unkown = "test"
        content.content.field1 = "also"

        _*field1.name>> "field1"
        _*field2.name>> "field2"

        when:
        "the content is sanitized"
        def result = contentType.convert(content)

        then:
        "the input should be converted"
        1 * field1.convertSubmission("also",content) >> "ALSO"

        result.is(content)
        content.content as Map == [
                field1:"ALSO",
                unkown:"test"
        ]
    }

    void "validate() should validate fields"(){

        given:
        "some unkown fields"
        content.content.unkown = "test"
        content.content.field1 = "also"

        _*field1.name>> "field1"
        _*field2.name>> "field2"

        when:
        "the content is sanitized"
        def result = contentType.validate(content)

        then:
        "the input should be converted"
        1 * field1.required >> false
        1 * field2.required >> true

        1 * field1.validateSubmission("also", []) >> { s,a->
            a.add "other"
            return false
        }

        and:
        !result.valid
        result.fieldErrors == [
                field1 : ["other"],
                field2 : ["Required"]
        ]
    }

    void "validate() should ignore if not required"(){

        given:
        "some unkown fields"
        content.content.unkown = "test"
        content.content.field1 = "also"

        _*field1.name>> "field1"
        _*field2.name>> "field2"

        when:
        "the content is sanitized"
        def result = contentType.validate(content)

        then:
        "the input should be converted"
        1 * field1.required >> false
        1 * field2.required >> false

        1 * field1.validateSubmission("also", []) >> true

        and:
        result.valid
        result.fieldErrors == [:]
    }
}
