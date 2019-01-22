package org.schicwp.dinky.api.dto;

import org.schicwp.dinky.model.ContentMap;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class ContentSubmission {

    private String id;
    @NotNull
    private String type;
    @NotNull
    private String action;
    @NotNull
    private String workflow;

    private Map<String,ContentMap> workflowConfig = new HashMap<>();
    private Map<String, Object> content = new HashMap<>();
    private Integer version;

    public String getAction() {
        return action;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWorkflow() {
        return workflow;
    }

    public Map<String, ContentMap> getWorkflowConfig() {
        return workflowConfig;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public void setWorkflowConfig(Map<String, ContentMap> workflowConfig) {
        this.workflowConfig = workflowConfig;
    }
}
