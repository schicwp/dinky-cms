package org.schicwp.dinky.config;

import org.schicwp.dinky.model.ContentMap;

/**
 * Created by will.schick on 1/21/19.
 */
public class ActionHookConfig {

    private String name;
    private ContentMap config;

    public String getName() {
        return name;
    }

    public ContentMap getConfig() {
        return config;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfig(ContentMap config) {
        this.config = config;
    }
}
