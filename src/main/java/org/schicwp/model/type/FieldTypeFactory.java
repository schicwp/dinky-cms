package org.schicwp.model.type;

import org.schicwp.model.type.fields.IntField;
import org.schicwp.model.type.fields.StringField;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class FieldTypeFactory {

    private static final String STRING = "string";
    private static final String INT = "int";

    public Field createField(Map<String,Object> config){


        boolean required = (Boolean) config.getOrDefault("required",false);
        boolean indexed = (Boolean)config.getOrDefault("index",false);

        FieldType fieldType = createFieldType((String)config.get("type"));

        String name = (String)config.get("name");

        Map<String,String> properites = new HashMap<>();

        config.forEach((k,v)->{
            properites.put(k,v.toString());
        });


        return new Field(fieldType, required, properites, name, indexed);
    }


    FieldType createFieldType(String name){

        switch ( name.toLowerCase() ){
            case STRING:
                return new StringField();
            case INT:
                return new IntField();

        }

        return null;

    }
}
