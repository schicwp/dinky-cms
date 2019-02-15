package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.search.SearchContent;
import org.schicwp.dinky.search.SearchService;
import org.schicwp.dinky.workflow.ActionHook;
import org.schicwp.dinky.workflow.ActionHookFactory;
import org.schicwp.dinky.workflow.ActionHookFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/6/19.
 */
@Component
public class AddToSearch implements ActionHookFactory {

    private static final Logger logger = Logger.getLogger(AddToSearch.class.getCanonicalName());


    @Autowired
    SearchService searchService;

    @Override
    public String getName() {
        return "AddToSearch";
    }

    @Override
    public ActionHook createActionHook(ContentMap config) {

        String index = (String)config.getOrDefault("index","default");

        return (content, actionConfig) -> {

            logger.info(String.format("Adding [%s] to search index [%s]", content,index));

            content.getSearchVersions().put(index,content.getVersion());

            searchService.addToIndex(index,new SearchContent(content));
        };
    }
}
