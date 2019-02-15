package org.schicwp.dinky.content;

import org.schicwp.dinky.api.dto.ContentSubmission;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.schicwp.dinky.exceptions.OptimisticLockingException;
import org.schicwp.dinky.exceptions.PermissionException;
import org.schicwp.dinky.exceptions.SubmissionValidationException;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.ContentType;
import org.schicwp.dinky.model.type.ContentTypeService;
import org.schicwp.dinky.exceptions.FieldValidationException;
import org.schicwp.dinky.model.type.ValidationResult;
import org.schicwp.dinky.workflow.WorkflowExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Allows processing of content submissions that will provide correct merging, permissions and workflow behavior.
 *
 */
@Service
public class ContentSubmissionService {

    private static final Logger logger = Logger.getLogger(ContentSubmissionService.class.getCanonicalName());

    @Autowired
    ContentService contentService;

    @Autowired
    ContentTypeService contentTypeService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    AuthService authService;

    @Autowired
    WorkflowExecutionService workflowExecutionService;

    @Transactional
    public Content processSubmission(ContentSubmission contentSubmission) {

        String id = contentSubmission.getId();
        String workflow = contentSubmission.getWorkflow();

        ContentType contentType = contentTypeService.getContentType(contentSubmission.getType());

        if (workflow == null && contentType.getWorkflows().size() == 1)
            workflow = contentType.getWorkflows().iterator().next();


        if (!contentType.getWorkflows().contains(workflow))
            throw new SubmissionValidationException("Invalid workflow");

        Content oldContent;

        boolean isNew;

        User currentUser = authService.getCurrentUser();

        if (id != null) {
            Optional<Content> contentOptional = contentService
                    .findById(id);

            isNew = !contentOptional.isPresent();

            if (contentOptional.isPresent() && !permissionService.allowWrite(contentOptional.get()))
                throw new PermissionException();

            oldContent = contentOptional.orElse(
                    new Content(
                            id,
                            currentUser.getUsername(),
                            contentSubmission.getType())
            );


        } else {

            isNew = true;

            oldContent = new Content(
                    UUID.randomUUID().toString(),
                    currentUser.getUsername(),
                    contentSubmission.getType()
            );
        }

        if (contentSubmission.getVersion() != null &&
                oldContent.getVersion() != contentSubmission.getVersion()){
            throw new OptimisticLockingException();
        }


        Content content = oldContent.merge(contentSubmission.getContent());
        content.setModifiedBy(currentUser.getUsername());

        if (contentType.getNameField() != null){
            content.setName((String)content.getContent().get(contentType.getNameField()));
        }

        contentType.sanitize(content);

        ValidationResult validate = contentType.validate(content);

        if (!validate.isValid())
            throw new FieldValidationException(validate);

        contentType.convert(content);



        if (contentSubmission.getAction() != null)
            workflowExecutionService.executeWorkflowAction(workflow, contentSubmission.getAction(), content, contentSubmission.getWorkflowConfig());
        else if (isNew)
            throw new SubmissionValidationException("Workflow action required for new content");
        else
            logger.info("Saving content with no workflow action [" + id + "]");


        return contentService.save(content);



    }


}
