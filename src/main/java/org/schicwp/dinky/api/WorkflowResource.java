package org.schicwp.dinky.api;

import org.schicwp.dinky.config.ContentTypeConfig;
import org.schicwp.dinky.config.WorkflowConfig;
import org.schicwp.dinky.config.loader.WorkflowLoader;
import org.schicwp.dinky.model.type.ContentTypeService;
import org.schicwp.dinky.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by will.schick on 2/11/19.
 */
@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowResource {

    @Autowired
    WorkflowLoader workflowLoader;

    @GetMapping
    public Page<WorkflowConfig> listContentTypes(){
        List<WorkflowConfig> workflowConfigs = workflowLoader.getWorkflowConfigs();

        return PageableExecutionUtils.getPage(
                workflowConfigs,
                PageRequest.of(0,workflowConfigs.size()),
                workflowConfigs::size
        );
    }

    @GetMapping("{name}")
    public WorkflowConfig getContentType(@PathVariable("name") String name){
        List<WorkflowConfig> workflowConfigs = workflowLoader.getWorkflowConfigs();

        return workflowConfigs.stream().filter( w->w.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }
}
