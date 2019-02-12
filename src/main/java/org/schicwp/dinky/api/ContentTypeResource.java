package org.schicwp.dinky.api;

import org.schicwp.dinky.config.ContentTypeConfig;
import org.schicwp.dinky.config.WorkflowConfig;
import org.schicwp.dinky.config.loader.TypeLoader;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.ContentType;
import org.schicwp.dinky.model.type.ContentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by will.schick on 2/11/19.
 */
@RestController
@RequestMapping("/api/v1/content-type")
public class ContentTypeResource {

    @Autowired
    TypeLoader typeLoader;

    @GetMapping
    public Page<ContentTypeConfig> listContentTypes(){
        List<ContentTypeConfig> allContentTypes = typeLoader.getContentTypeConfigs();

        return PageableExecutionUtils.getPage(
                allContentTypes,
                PageRequest.of(0,allContentTypes.size()),
                allContentTypes::size
        );
    }

    @GetMapping("{name}")
    public ContentTypeConfig getContentType(@PathVariable("name") String name){
        List<ContentTypeConfig> workflowConfigs = typeLoader.getContentTypeConfigs();

        return workflowConfigs.stream().filter( w->w.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }
}
