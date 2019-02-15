package org.schicwp.dinky.content

import org.schicwp.dinky.api.dto.ContentSubmission
import org.schicwp.dinky.auth.AuthService
import org.schicwp.dinky.auth.User
import org.schicwp.dinky.exceptions.OptimisticLockingException
import org.schicwp.dinky.exceptions.FieldValidationException
import org.schicwp.dinky.exceptions.SubmissionValidationException
import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.type.ContentType
import org.schicwp.dinky.model.type.ContentTypeService
import org.schicwp.dinky.model.type.ValidationResult
import org.schicwp.dinky.workflow.Action
import org.schicwp.dinky.workflow.ActionHook
import org.schicwp.dinky.workflow.NamedActionHook
import org.schicwp.dinky.workflow.Workflow
import org.schicwp.dinky.workflow.WorkflowExecutionService
import spock.lang.Specification

/**
 * Created by will.schick on 1/21/19.
 */
class ContentSubmissionServiceSpec extends Specification {


    ContentSubmissionService contentSubmissionService;

    ContentTypeService contentTypeService;
    ContentService contentService;
    PermissionService permissionService;
    AuthService authService;
    WorkflowExecutionService workflowExecutionService;


    void setup(){
        contentTypeService = Mock(ContentTypeService)
        contentService = Mock(ContentService)
        permissionService = Mock(PermissionService)
        authService = Mock(AuthService)
        workflowExecutionService = Mock(WorkflowExecutionService)

        contentSubmissionService = new ContentSubmissionService(
                contentTypeService: contentTypeService,
                contentService: contentService,
                permissionService: permissionService,
                authService: authService,
                workflowExecutionService: workflowExecutionService
        )

        _ * authService.currentUser >> new User("bob",[],false)

        0 * _._

    }


    void "it should throw an exception if the workflow is not valid for the content type"(){

        when:
        "a workflow is executed"
        contentSubmissionService.processSubmission(
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
        contentSubmissionService.processSubmission(
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
        contentSubmissionService.processSubmission(
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
        contentSubmissionService.processSubmission(
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
        contentMap.title = "the title"

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
        _ * contentType.nameField >> "title"
        _ * content.setName("the title")

        when:
        "a workflow is executed"
        contentSubmissionService.processSubmission(
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
        "modification should be recorded"
        1 * content.setModifiedBy("bob")

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: false)

        and:
        thrown(FieldValidationException)




    }



    void "it should save and execute workflow if action given if everything is ok"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();
        contentMap.title = "the title"

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
        _ * contentType.nameField >> "title"
        _ * content.setName("the title")

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
                 []
        )

        when:
        "a workflow is executed"
        def result = contentSubmissionService.processSubmission(
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
        "modification should be recorded"
        1 * content.setModifiedBy("bob")

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the workflow should executed"
        1 * workflowExecutionService.executeWorkflowAction('wf1', 'PutInNewState',  content, [:])

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content
    }

    void "it should save if no action given if everything is ok"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();
        contentMap.title = "the title"

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
        _ * contentType.nameField >> "title"
        _ * content.setName("the title")

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
                []
        )

        when:
        "a workflow is executed"
        def result = contentSubmissionService.processSubmission(
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
        "modification should be recorded"
        1 * content.setModifiedBy("bob")

        and:
        "the content should be sanitized"
        1 * contentType.sanitize(content) >> content

        and:
        "validated"
        1 * contentType.validate(content) >> new ValidationResult( valid: true)

        and:
        "the content should be converted"
        1 * contentType.convert(content)

        and:
        "the content should be saved"
        1 * contentService.save(content) >> content

        and:
        result == content




    }


    void "it should not save if no action given and it is new"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();
        contentMap.title = "the title"

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 1
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]
        _ * contentType.nameField >> "title"
        _ * content.setName("the title")

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
                []
        )

        when:
        "a workflow is executed"
        def result = contentSubmissionService.processSubmission(
                new ContentSubmission(
                        id:"123",
                        workflow: "wf1",
                        type: "cat",
                        version: 0,
                        content: contentMap
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType

        and:
        "the content should be fetched"
        1 * contentService.findById("123") >> Optional.empty()

        and:
        "sanitzed"
        1 * contentType.sanitize(_)

        and:
        "validated"
        1 * contentType.validate(_) >> new ValidationResult( valid: true)

        and:
        "the content should be converted"
        1 * contentType.convert(_)

        and:
        "the content should be saved"
        0 * contentService.save(_)

        and:
        thrown(SubmissionValidationException)




    }

    void "it should not save if no action given and it is new (no id)"(){

        given:
        "a content map"
        ContentMap contentMap = new ContentMap();
        contentMap.title = "the title"

        and:
        "some content"
        Content content = Mock(Content)
        _*content.version >> 1
        _*content.content >> contentMap
        _*content.state >> "OldState"
        _ * content.toString() >> "test content"



        and:
        "a content type"
        ContentType contentType = Mock(ContentType)
        _ * contentType.workflows >> ["wf1","wf2"]
        _ * contentType.nameField >> "title"
        _ * content.setName("the title")

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
                []
        )

        when:
        "a workflow is executed"
        def result = contentSubmissionService.processSubmission(
                new ContentSubmission(
                        workflow: "wf1",
                        type: "cat",
                        version: 0,
                        content: contentMap
                )
        )

        then:
        "the content type should be fetched"
        1 * contentTypeService.getContentType("cat") >> contentType


        and:
        "sanitzed"
        1 * contentType.sanitize(_)

        and:
        "validated"
        1 * contentType.validate(_) >> new ValidationResult( valid: true)

        and:
        "the content should be converted"
        1 * contentType.convert(_)

        and:
        "the content should be saved"
        0 * contentService.save(_)

        and:
        thrown(SubmissionValidationException)




    }


}
