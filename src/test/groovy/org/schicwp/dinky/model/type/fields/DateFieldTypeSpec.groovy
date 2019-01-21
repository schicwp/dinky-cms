package org.schicwp.dinky.model.type.fields

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import spock.lang.Specification

/**
 * Created by will.schick on 1/20/19.
 */
class DateFieldTypeSpec extends Specification{

    DateFieldType dateFieldType;

    void setup(){
        dateFieldType = new DateFieldType();
    }

    void "the name should be Date"(){
        expect:
        dateFieldType.name == "Date"
    }

    void "validate should accept Dates"(){
        expect:
        dateFieldType.validateSubmission(new Date(),new ContentMap(),[])
    }

    void "validate should accept null"(){
        expect:
        dateFieldType.validateSubmission(null,new ContentMap(),[])
    }

    void "validate should accept proper dates"(){
        expect:
        dateFieldType.validateSubmission("2018-06-15T19:37:21.123-0000",new ContentMap(),[])
    }

    void "validate should reject improper dates"(){
        expect:
        !dateFieldType.validateSubmission("2018-we-15T19:37:21-0000",new ContentMap(),[])
    }



    void "convert should accept Dates"(){
        given:
        "date"
        Date date = new Date()

        expect:
        dateFieldType.convertSubmission(date,new ContentMap(),new Content()) == date
    }

    void "convert should accept null"(){
        expect:
        dateFieldType.convertSubmission(null,new ContentMap(),new Content()) == null
    }

    void "convert should accept proper dates"(){
        when:
        Date date = dateFieldType.convertSubmission("2018-06-15T19:37:21.123-0000",new ContentMap(),new Content())

        then:
        date.time == 1529091441123L


    }

    void "convert should reject improper dates"(){
        when:
        dateFieldType.convertSubmission("2018-we-15T19:37:21-0000",new ContentMap(),new Content())

        then:
        thrown(RuntimeException)
    }




}
