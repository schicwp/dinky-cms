package org.schicwp.dinky.model.type.fields

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class StringFieldTypeSpec extends Specification{

    StringFieldType stringFieldType;

    void setup(){
        stringFieldType = new StringFieldType();
    }

    void "the name should be string"(){
        expect:
        stringFieldType.name == "String"
    }

    void "validate should accept strings"(){
        expect:
        stringFieldType.validateSubmission("arf",new ContentMap(),[])
    }

    void "validate should accept null"(){
        expect:
        stringFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should reject non strings"(){
        expect:
        !stringFieldType.validateSubmission(1,new ContentMap(),[])
    }

    void "validate should use regex if provided"(){
        expect:
        stringFieldType.validateSubmission(input,new ContentMap([regex:regex]),[]) == result

        where:
        input           |       regex           |   result
        "arf"           |   "[a-z]+"            |   true
        "Arf"           |   "[a-z]+"            |   false
    }

    void "validate should use minLength if provided"(){
        expect:
        stringFieldType.validateSubmission(input,new ContentMap([minLength:minLength]),[]) == result

        where:
        input           |       minLength           |   result
        "arf"           |   2                       |   true
        "arf"           |   3                       |   true
        "arf"           |   4                       |   false
    }

    void "validate should use maxLength if provided"(){
        expect:
        stringFieldType.validateSubmission(input,new ContentMap([maxLength:maxLength]),[]) == result

        where:
        input           |       maxLength           |   result
        "arf"           |   2                       |   false
        "arf"           |   3                       |   true
        "arf"           |   4                       |   true
    }

    void "validate should use allowedValues if provided"(){
        expect:
        stringFieldType.validateSubmission(input,new ContentMap([allowedValues:allowedValues]),[]) == result

        where:
        input           |       allowedValues           |   result
        "arf"           |   ["ruff","meow"]             |   false
        "arf"           |    ["ruff","arf"]             |   true

    }

    void "convertSubmission should return input"(){
        given:
        "an input"
        def o = new Object();

        expect:
        o.is(stringFieldType.convertSubmission(o,new ContentMap(),new Content()))
    }


}
