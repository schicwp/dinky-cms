package org.schicwp.model;

import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * Created by will.schick on 1/4/19.
 */
public class ContentHistory {

    @Id
    private String id;

    private Content content;

    public ContentHistory(){}

    public ContentHistory(Content content){
        this.content = content;
    }


    public String getId() {
        return id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }
}
