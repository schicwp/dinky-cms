package org.schicwp.dinky.model.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/6/19.
 */
@Service
public class FieldTypeService {

    private static final Logger logger = Logger.getLogger(FieldTypeService.class.getCanonicalName());


    @Autowired
    private ApplicationContext applicationContext;

    Map<String,FieldType> fieldTypeFactoryHashMap = new HashMap<>();

    @PostConstruct
    public void init(){
        applicationContext.getBeansOfType(FieldType.class).forEach((k,f)->{

            logger.info("Registering FieldType: " + f.getName());

            fieldTypeFactoryHashMap.put(f.getName().toLowerCase(),f);
        });
    }

    public FieldType getFieldType(String name){

        if (!fieldTypeFactoryHashMap.containsKey(name.toLowerCase()))
            throw new IllegalArgumentException("Unkown field type: " + name);

        return fieldTypeFactoryHashMap.get(name.toLowerCase());
    }
}
