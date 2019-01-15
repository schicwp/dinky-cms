package org.schicwp.model.type;

import org.schicwp.workflow.ActionHookFactory;
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
public class FieldTypeFactoryService {

    private static final Logger logger = Logger.getLogger(FieldTypeFactoryService.class.getCanonicalName());


    @Autowired
    private ApplicationContext applicationContext;

    Map<String,FieldTypeFactory> fieldTypeFactoryHashMap = new HashMap<>();

    @PostConstruct
    public void init(){
        applicationContext.getBeansOfType(FieldTypeFactory.class).forEach((k,f)->{

            logger.info("Registering FieldType: " + f.getName());

            fieldTypeFactoryHashMap.put(f.getName().toLowerCase(),f);
        });
    }

    public FieldTypeFactory getFieldTypeFactory(String name){

        if (!fieldTypeFactoryHashMap.containsKey(name.toLowerCase()))
            throw new IllegalArgumentException("Unkown field type: " + name);

        return fieldTypeFactoryHashMap.get(name.toLowerCase());
    }
}
