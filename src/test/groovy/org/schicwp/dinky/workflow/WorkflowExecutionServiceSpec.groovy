package org.schicwp.dinky.workflow

import org.schicwp.dinky.api.dto.ContentSubmission
import org.schicwp.dinky.auth.AuthService
import org.schicwp.dinky.auth.User
import org.schicwp.dinky.content.ContentService
import org.schicwp.dinky.content.PermissionService
import org.schicwp.dinky.exceptions.OptimisticLockingException
import org.schicwp.dinky.exceptions.ValidationException
import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.type.ContentType
import org.schicwp.dinky.model.type.ContentTypeService
import org.schicwp.dinky.model.type.ValidationResult
import spock.lang.Specification

/**
 * Created by will.schick on 1/21/19.
 */
class WorkflowExecutionServiceSpec extends Specification {


    WorkflowExecutionService workflowExecutionService;

    ContentTypeService contentTypeService;
    ContentService contentService;
    PermissionService permissionService;
    AuthService authService;
    WorkflowService workflowService;


    void setup(){
        contentTypeService = Mock(ContentTypeService)
        contentService = Mock(ContentService)
        permissionService = Mock(PermissionService)
        authService = Mock(AuthService)
        workflowService = Mock(WorkflowService)

        workflowExecutionService = new WorkflowExecutionService(
                contentTypeService: contentTypeService,
                contentService: contentService,
                permissionService: permissionService,
                authService: authService,
                workflowService: workflowService
        )

        _ * authService.currentUser >> new User("bob",[],false)
    }


    void "it should throw an exception if the workflow is not valid for the content type"(){

        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat"
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> new ContentType("tst",[],["wf2"],"")


        and:
        thrown(Exception)
    }

    void "an exception should be thrown if it is an existing doc and there is no write permission"(){

        given:
        "some content"
        Content content = new Content();

        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat"
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> new ContentType("tst",[],["wf2","wf1"],"")


        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> false

        and:
        thrown(Exception)
    }

    void "an exception should be thrown if it is an existing doc and there is a version mismatch"(){

        given:
        "some content"
        Content content = new Content(
                id: "old",
                version: 3
        );

        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> new ContentType("tst",[],["wf2","wf1"],"")


        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        thrown(OptimisticLockingException)
    }

    void "an exception should be thrown if it is a new doc and there is a version mismatch"(){


        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 1
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> new ContentType("tst",[],["wf2","wf1"],"")


        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.empty()

        and:
        thrown(OptimisticLockingException)
    }

    void "an exception should be thrown if the content does not validate"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]

        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2,
                        content: contentMap
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: false)

        and:
        thrown(ValidationException)




    }

    void "an exception should be thrown submitted state is not valid"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]

        and:
        "a workflow"
        Workflow workflow = Mock(Workflow)

        when:
        "a workflow is executed"
        workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2,
                        action: "PutInNewState",
                        content: contentMap
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the workflow should be fetched"
        1 * workflowService.getWorkflow("wf1") >> workflow

        and:
        "the next action should be fetched"
        1 * workflow.getActionFromState("OldState","PutInNewState") >> Optional.empty()

        and:
        thrown(RuntimeException)




    }

    void "it should save if everything is ok"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]

        and:
        "a workflow"
        Workflow workflow = Mock(Workflow)

        and:
        "an action"
        Action action = new Action(
                "test",
                false,
                 "NewState",
                [],
                [],
                 [:]
        )

        when:
        "a workflow is executed"
        def result = workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2,
                        action: "PutInNewState",
                        content: contentMap
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the workflow should be fetched"
        1 * workflowService.getWorkflow("wf1") >> workflow

        and:
        "the next action should be fetched"
        1 * workflow.getActionFromState("OldState","PutInNewState") >> Optional.of(action)

        and:
        "the state should be set on the content"
        1 * content.setState("NewState")
        1 * content.setWorkflow("wf1")

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content




    }

    void "it should execute action hooks"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]

        and:
        "a workflow"
        Workflow workflow = Mock(Workflow)

        and:
        "an action hook"
        ActionHook actionHook = Mock(ActionHook)

        and:
        "an action"
        Action action = new Action(
                "test",
                false,
                "NewState",
                [],
                [],
                [
                        testHook:actionHook
                ]
        )

        when:
        "a workflow is executed"
        def result = workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2,
                        action: "PutInNewState",
                        content: contentMap,
                        workflowConfig: [
                                testHook: new ContentMap( [
                                        ruff:"meow"
                                ])
                        ]
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the workflow should be fetched"
        1 * workflowService.getWorkflow("wf1") >> workflow

        and:
        "the next action should be fetched"
        1 * workflow.getActionFromState("OldState","PutInNewState") >> Optional.of(action)

        and:
        "the state should be set on the content"
        1 * content.setState("NewState")
        1 * content.setWorkflow("wf1")

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the hooks should be executed"
        1 * actionHook.execute(content,[ruff:"meow"])

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content




    }

    void "it should execute using implied workflow if given one is null and type has only one"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1"]

        and:
        "a workflow"
        Workflow workflow = Mock(Workflow)

        and:
        "an action hook"
        ActionHook actionHook = Mock(ActionHook)

        and:
        "an action"
        Action action = new Action(
                "test",
                false,
                "NewState",
                [],
                [],
                [
                        testHook:actionHook
                ]
        )

        when:
        "a workflow is executed"
        def result = workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        type: "cat",
                        version: 2,
                        action: "PutInNewState",
                        content: contentMap,
                        workflowConfig: [
                                testHook: new ContentMap( [
                                        ruff:"meow"
                                ])
                        ]
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the workflow should be fetched"
        1 * workflowService.getWorkflow("wf1") >> workflow

        and:
        "the next action should be fetched"
        1 * workflow.getActionFromState("OldState","PutInNewState") >> Optional.of(action)

        and:
        "the state should be set on the content"
        1 * content.setState("NewState")
        1 * content.setWorkflow("wf1")

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the hooks should be executed"
        1 * actionHook.execute(content,[ruff:"meow"])

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content




    }

    void "it should execute action hooks with empty config if not provided"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 2
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]

        and:
        "a workflow"
        Workflow workflow = Mock(Workflow)

        and:
        "an action hook"
        ActionHook actionHook = Mock(ActionHook)

        and:
        "an action"
        Action action = new Action(
                "test",
                false,
                "NewState",
                [],
                [],
                [
                        testHook:actionHook
                ]
        )

        when:
        "a workflow is executed"
        def result = workflowExecutionService.executeAction(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 2,
                        action: "PutInNewState",
                        content: contentMap,
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.of(content)

        and:
        "the write perms should be checked"
        1 * permissionService.allowWrite(content) >> true

        and:
        "the new content should be merged"
        1 * content.merge(contentMap) >> content

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the workflow should be fetched"
        1 * workflowService.getWorkflow("wf1") >> workflow

        and:
        "the next action should be fetched"
        1 * workflow.getActionFromState("OldState","PutInNewState") >> Optional.of(action)

        and:
        "the state should be set on the content"
        1 * content.setState("NewState")
        1 * content.setWorkflow("wf1")

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the hooks should be executed"
        1 * actionHook.execute(content,[:])

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content




    }
}
