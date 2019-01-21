package org.schicwp.dinky.config.loader;

import org.schicwp.dinky.config.ActionHookConfig;
import org.schicwp.dinky.config.StateConfig;
import org.schicwp.dinky.config.WorkflowConfig;
import org.schicwp.dinky.workflow.*;
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
import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class WorkflowLoader {

    private static final Logger logger = Logger.getLogger(WorkflowLoader.class.getCanonicalName());

    private String configDir = "workflows";


    @Autowired
    ActionHookFactoryService actionHookFactoryService;

    @Autowired
    WorkflowService workflowService;


    @PostConstruct
    public synchronized void init(){


        logger.info("Loading workflows");

        Map<String,Workflow> workflows = new HashMap<>();

        Arrays.asList(new File(configDir).listFiles()).forEach(file->{

            try {

                logger.info("Loading: " + file);
                WorkflowConfig workflowConfig = new Yaml(new Constructor(WorkflowConfig.class))
                        .load(new FileInputStream(file));

                Workflow workflow = convertWorkflowConfigToWorkflow(workflowConfig);

                workflows.put(workflow.getName(),workflow);


            }catch (Exception e){
                throw new RuntimeException(e);
            }

        });

        logger.info("Workflows: " + workflows);

        workflowService.setWorkflows(workflows);
    }

    public Workflow convertWorkflowConfigToWorkflow(WorkflowConfig workflowConfig){

        return new Workflow(
                workflowConfig.getName(), workflowConfig
                    .getStates()
                    .stream()
                    .map(StateConfig::getName)
                    .map(State::new)
                    .collect(Collectors.toList()),
                workflowConfig
                    .getActions()
                    .stream()
                    .map(a -> new Action(
                            a.getName(),
                            a.isEntryPoint(),
                            a.getNextState(),
                            a.getSourceStates(),
                            a.getAllowedGroups(),
                            a.getHooks()
                                .stream()
                                .collect(
                                    Collectors.toMap(
                                            ActionHookConfig::getName,
                                            h-> actionHookFactoryService
                                                    .getActionHook(h.getName())
                                                    .createActionHook(h.getConfig())
                                    )
                                )
                            )
                    ).collect(Collectors.toList()));
    }
}
