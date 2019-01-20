package org.schicwp.dinky.workflow.hooks;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.search.SearchRepository;
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
    SearchRepository searchRepository;

    @Override
    public String getName() {
        return "RemoveFromSearch";
    }

    @Override
    public ActionHook createActionHook(Map<String, Object> config) {

        String index = (String)config.getOrDefault("index","default");


        return (content, actionConfig) -> {
            logger.info(String.format("Removing [%s] from search index.", content));

            Optional<Content> saved =
                    searchRepository.findById(content.getId());

            saved.ifPresent(searchRepository::delete);
            content.getSearchVersions().remove(index);

        };
    }
}
