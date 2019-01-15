package org.schicwp.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class WorkflowService {

    private static final Logger logger = Logger.getLogger(WorkflowService.class.getCanonicalName());

    String configDir = "workflows";

    Map<String,Workflow> workflows = null;

    @Autowired
    ActionHookFactoryService actionHookFactoryService;

    public synchronized Workflow getWorkflow(String name){
        if (workflows == null)
            this.init();

        return workflows.get(name);
    }

    @PostConstruct
    public synchronized void init(){


        logger.info("Loading workflows");

        Map<String,Workflow> workflows = new HashMap<>();

        Arrays.asList(new File(configDir).listFiles()).forEach(file->{

            try {

                logger.info("Loading: " + file);
                Workflow workflow = new Yaml(new Constructor(Workflow.class))
                        .load(new FileInputStream(file));

                workflow.getActions().forEach(action -> {
                    action.getHooks().forEach( hookConfig->{

                        String name = (String)hookConfig.get("name");

                        action.getActionHooks().put(
                                name,
                                actionHookFactoryService.getActionHook(name).createActionHook(hookConfig)
                        );
                    });
                });


                workflows.put(workflow.getName(),workflow);


            }catch (Exception e){
                throw new RuntimeException(e);
            }

        });

        logger.info("Workflows: " + workflows);

        this.workflows = workflows;
    }
}
