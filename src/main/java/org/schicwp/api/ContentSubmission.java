package org.schicwp.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public class ContentSubmission {

    private String action;

    private Map<String,Map<String,Object>> workflow = new HashMap<>();

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

    public Map<String, Map<String, Object>> getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Map<String, Map<String, Object>> workflow) {
        this.workflow = workflow;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
