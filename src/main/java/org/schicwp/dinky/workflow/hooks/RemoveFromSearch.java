package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.search.SearchRepository;
import org.schicwp.dinky.search.SearchService;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/6/19.
 */
@Component
public class RemoveFromSearch implements ActionHookFactory {

    private static final Logger logger = Logger.getLogger(RemoveFromSearch.class.getCanonicalName());


    @Autowired
    SearchService searchService;

    @Override
    public String getName() {
        return "RemoveFromSearch";
    }

    @Override
    public ActionHook createActionHook(ContentMap config) {

        String index = (String)config.getOrDefault("index","default");


        return (content, actionConfig) -> {
            logger.info(String.format("Removing [%s] from search index.", content));


            searchService.withIndex(index,searchRepository -> {

                searchRepository
                        .findById(content.getId())
                        .ifPresent(searchRepository::delete);

                content.getSearchVersions().remove(index);
            });


        };
    }
}
