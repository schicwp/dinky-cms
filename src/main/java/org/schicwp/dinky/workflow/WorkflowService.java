package org.schicwp.dinky.workflow;

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

    private Map<String,Workflow> workflows = null;

    public synchronized Workflow getWorkflow(String name){
        return workflows.get(name);
    }

    public synchronized void setWorkflows(Map<String, Workflow> workflows) {
        this.workflows = workflows;
    }


}
