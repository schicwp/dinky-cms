package org.schicwp.dinky.model.type.fields

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class IntFieldTypeSpec extends Specification{

    IntFieldType intFieldType;

    void setup(){
        intFieldType = new IntFieldType();
    }

    void "the name should be Int"(){
        expect:
        intFieldType.name == "Int"
    }

    void "validate should accept ints"(){
        expect:
        intFieldType.validateSubmission(12,new ContentMap(),[])
    }

    void "validate should accept null"(){
        expect:
        intFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should reject non ints"(){
        expect:
        !intFieldType.validateSubmission(1.2f,new ContentMap(),[])
    }

    void "validate should use min if provided"(){
        expect:
        intFieldType.validateSubmission(input,new ContentMap([min:min]),[]) == result

        where:
        input           |       min           |   result
        3               |   2                       |   true
        3               |   3                       |   true
        3               |   4                       |   false
    }

    void "validate should use max if provided"(){
        expect:
        intFieldType.validateSubmission(input,new ContentMap([max:max]),[]) == result

        where:
        input           |       max           |   result
        3               |   2                       |   false
        3               |   3                       |   true
        3               |   4                       |   true
    }

    void "convertSubmission should return input"(){
        given:
        "an input"
        def o = new Object();

        expect:
        o.is(intFieldType.convertSubmission(o,new ContentMap(),new Content()))
    }



}
