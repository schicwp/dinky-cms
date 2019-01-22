package org.schicwp.dinky.workflow;

import org.schicwp.dinky.api.dto.ContentSubmission;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.content.PermissionService;
import org.schicwp.dinky.exceptions.OptimisticLockingException;
import org.schicwp.dinky.exceptions.PermissionException;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.schicwp.dinky.model.type.ContentType;
import org.schicwp.dinky.model.type.ContentTypeService;
import org.schicwp.dinky.exceptions.ValidationException;
import org.schicwp.dinky.model.type.ValidationResult;
import org.schicwp.dinky.content.ContentService;
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
    ContentService contentService;

    @Autowired
    ContentTypeService contentTypeService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    AuthService authService;

    @Autowired
    WorkflowService workflowService;

    @Transactional
    public Content executeAction( ContentSubmission contentSubmission) {

        String id = contentSubmission.getId();
        String workflow = contentSubmission.getWorkflow();

        ContentType contentType = contentTypeService.getContentType(contentSubmission.getType());

        if (workflow == null && contentType.getWorkflows().size() == 1)
            workflow = contentType.getWorkflows().iterator().next();


        if (!contentType.getWorkflows().contains(workflow))
            throw new RuntimeException("Invalid workflow");

        Content oldContent;

        if (id != null) {

            Optional<Content> contentOptional = contentService
                    .findById(id);

            if (contentOptional.isPresent() && !permissionService.allowWrite(contentOptional.get()))
                throw new PermissionException();

            Content oldVersion = contentOptional.orElse(
                    new Content(
                            id,
                            authService.getCurrentUser().getUsername(),
                            contentSubmission.getType())
            );

            System.out.println(oldVersion);

            oldContent = oldVersion;

        } else {
            oldContent = new Content(
                    UUID.randomUUID().toString(),
                    authService.getCurrentUser().getUsername(),
                    contentSubmission.getType()
            );
        }

        if (contentSubmission.getVersion() != null &&
                oldContent.getVersion() != contentSubmission.getVersion()){
            throw new OptimisticLockingException();
        }

        Content content = oldContent.merge(contentSubmission.getContent());

        if (contentType.getNameField() != null){
            content.setName((String)content.getContent().get(contentType.getNameField()));
        }

        contentType.sanitize(content);

        ValidationResult validate = contentType.validate(content);
        if (!validate.isValid())
            throw new ValidationException(validate);

        Optional<Action> actionOptional = workflowService
                .getWorkflow(workflow)
                .getActionFromState(
                        content.getState(),
                        contentSubmission.getAction()
                );

        if (!actionOptional.isPresent())
            throw new RuntimeException("Invalid Action");

        Action action = actionOptional.get();

        if (id != null)
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
        content.setWorkflow(workflow);

        contentType.convert(content);

        action.getActionHooks().forEach((name,hook) -> {
            hook.execute(content,
                    contentSubmission
                            .getWorkflowConfig()
                            .getOrDefault(name,new ContentMap())
            );
        });

        return contentService.save(content);



    }
}
