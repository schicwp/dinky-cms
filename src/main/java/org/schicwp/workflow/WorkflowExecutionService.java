package org.schicwp.workflow;

import org.schicwp.api.ContentSubmission;
import org.schicwp.auth.AuthService;
import org.schicwp.model.Content;
import org.schicwp.model.ContentHistory;
import org.schicwp.model.type.ContentType;
import org.schicwp.model.type.ContentTypeService;
import org.schicwp.persistence.ContentService;
import org.schicwp.search.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class WorkflowExecutionService {

    private static final Logger logger = Logger.getLogger(WorkflowExecutionService.class.getCanonicalName());


    @Autowired
    ContentService contentRepository;

    @Autowired
    ContentTypeService contentTypeService;

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    AuthService authService;

    @Transactional
    public Content executeAction(Optional<String> id, ContentSubmission contentSubmission, String type) {

        ContentType contentType = contentTypeService.getContentType(type);


        Content content;

        if (id.isPresent()) {

            Content oldVersion = contentRepository
                    .findById(id.get()).get();

            if (contentSubmission.getVersion() != null && oldVersion.getVersion() != contentSubmission.getVersion()){
                throw new RuntimeException("Conflict");
            }

            content = oldVersion
                    .merge(contentSubmission.getContent());

        } else {
            content = new Content()
                    .merge(contentSubmission.getContent());

            content.setId(UUID.randomUUID().toString());
            content.setOwner(authService.getCurrentUser().getUsername());
            content.setType(type);
        }

        if (contentType.getNameField() != null){
            content.setName((String)content.getContent().get(contentType.getNameField()));
        }

        contentType.sanitize(content);

        if (!contentType.validate(content))
            throw new RuntimeException("Invalid content");


        Optional<Action> actionOptional = contentType.getWorkflow()
                .getActionFromState(content.getState(),contentSubmission.getAction());

        if (actionOptional.isPresent()){
            Action action = actionOptional.get();

            if (content.getId() != null)
                logger.info(
                        String.format("Performing action [%s] on [%s] to change state from [%s] to [%s]",
                            action.getName(),content.getId(),content.getState(),action.getNextState()
                        )
                );
            else
                logger.info(
                        String.format("Entering workflow with new content with action [%s] to state [%s]",
                                action.getName(),action.getNextState()
                        )
                );

            content.setState(action.getNextState());



            action.getActionHooks().forEach((name,hook) -> {
                hook.execute(content,
                                contentSubmission
                                        .getWorkflow()
                                        .get(name)
                        );
            });

            return contentRepository.save(content);


        } else
            throw new RuntimeException("Invalid Action: " + contentSubmission.getAction());
    }
}
