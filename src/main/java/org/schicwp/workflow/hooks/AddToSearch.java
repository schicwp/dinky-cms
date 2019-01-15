package org.schicwp.workflow.hooks;

import org.schicwp.model.Content;
import org.schicwp.search.SearchRepository;
import org.schicwp.workflow.ActionHook;
import org.schicwp.workflow.ActionHookFactory;
import org.schicwp.workflow.ActionHookFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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
    SearchRepository searchRepository;

    @Override
    public String getName() {
        return "AddToSearch";
    }

    @Override
    public ActionHook createActionHook(Map<String, Object> config) {
        return (content, actionConfig) -> {

            logger.info(String.format("Adding [%s] to search index.", content));

            content.setSearchVersion(content.getVersion());
            searchRepository.save(content);


        };
    }
}
