package org.schicwp.workflow;

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
public class ActionHookFactoryService {

    private static final Logger logger = Logger.getLogger(ActionHookFactoryService.class.getCanonicalName());


    @Autowired
    private ApplicationContext applicationContext;

    Map<String,ActionHookFactory> actionHookFactories = new HashMap<>();

    @PostConstruct
    public void init(){
        applicationContext.getBeansOfType(ActionHookFactory.class).forEach((k,f)->{

            logger.info("Registering ActionHook: " + f.getName());

            actionHookFactories.put(f.getName(),f);
        });
    }

    public ActionHookFactory getActionHook(String name){
        return actionHookFactories.get(name);
    }
}
