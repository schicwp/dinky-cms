package org.schicwp.dinky.model.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class FieldFactory {

    private static final String STRING = "string";
    private static final String INT = "int";

    @Autowired
    FieldTypeFactoryService fieldTypeFactoryService;

    public Field createField(Map<String,Object> config){


        boolean required = (Boolean) config.getOrDefault("required",false);
        boolean indexed = (Boolean)config.getOrDefault("index",false);

        FieldType fieldType = fieldTypeFactoryService
                .getFieldTypeFactory((String)config.get("type"))
                .createFieldType();

        String name = (String)config.get("name");


        return new Field(fieldType, required, config, name, indexed);
    }


}
