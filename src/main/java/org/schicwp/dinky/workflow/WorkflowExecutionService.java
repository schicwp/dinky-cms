package org.schicwp.dinky.workflow;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by will.schick on 2/8/19.
 */
@Service
public class WorkflowExecutionService {

    private static final Logger logger = Logger.getLogger(WorkflowExecutionService.class.getCanonicalName());


    @Autowired
    WorkflowService workflowService;

    public void executeWorkflowAction(String workflow, String actionName, Content content, Map<String, ContentMap> workflowConfig) {
        Optional<Action> actionOptional = workflowService
                .getWorkflow(workflow)
                .getActionFromState(
                        content.getState(),
                        actionName
                );

        if (!actionOptional.isPresent())
            throw new RuntimeException("Invalid Action");

        Action action = actionOptional.get();

        if (content.getState() != null)
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

        action.getActionHooks().forEach(namedActionHook -> {
            namedActionHook.getActionHook().execute(content,
                    workflowConfig
                            .getOrDefault(namedActionHook.getName(),new ContentMap())
            );
        });
    }
}
