package org.schicwp.dinky.model.type;

import org.schicwp.dinky.config.ContentTypeConfig;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by will.schick on 1/4/19.
 */
@Service
public class ContentTypeService {

    private Map<String,ContentType> contentTypes = new HashMap<>();

    public synchronized ContentType getContentType(String name){
        return contentTypes.get(name);
    }

    public synchronized void setContentTypes(Map<String, ContentType> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
