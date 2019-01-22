package org.schicwp.dinky.workflow.hooks

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.model.Permission
import org.schicwp.dinky.workflow.ActionHook
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by will.schick on 1/22/19.
 */
class SetPermissionsSpec extends Specification{


    SetPermissions setPermissions;


    void setup(){
        setPermissions = new SetPermissions()
    }

    void "the name should be correct"(){
        expect:
        setPermissions.name == "SetPermissions"
    }

    @Unroll
    void "it should set owner config"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                hookConfig
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        content.getPermissions().owner = new Permission(read,write)

        when:
        "the hook is called"
        hook.execute(content,new ContentMap(subConfig))

        then:
        content.permissions.owner.read == finalRead
        content.permissions.owner.write == finalWrite


        where:
        hookConfig                      | subConfig                         | read       | write     | finalRead     |   finalWrite
        [:]                             |  [:]                              | false      | false     | false         | false
        [:]                             |  [:]                              | true       | false     | true          | false
        [:]                             |  [:]                              | false      | true      | false         | true
        [owner:[read:true,write:true]]  |  [:]                              | false      | false     | true          | true
        [:]                             |  [owner:[read:true,write:true]]   | false      | false     | true          | true
        [owner:[read:false,write:true]] |  [owner:[read:true,write:false]]  | false      | false     | true          | false
        [owner:[read:false,write:true]] |  [:]                              | false      | false     | false         | true

    }

    @Unroll
    void "it should set other config"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                hookConfig
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        content.getPermissions().other = new Permission(read,write)

        when:
        "the hook is called"
        hook.execute(content,new ContentMap(subConfig))

        then:
        content.permissions.other.read == finalRead
        content.permissions.other.write == finalWrite


        where:
        hookConfig                      | subConfig                         | read       | write     | finalRead     |   finalWrite
        [:]                             |  [:]                              | false      | false     | false         | false
        [:]                             |  [:]                              | true       | false     | true          | false
        [:]                             |  [:]                              | false      | true      | false         | true
        [other:[read:true,write:true]]  |  [:]                              | false      | false     | true          | true
        [:]                             |  [other:[read:true,write:true]]   | false      | false     | true          | true
        [other:[read:false,write:true]] |  [other:[read:true,write:false]]  | false      | false     | true          | false
        [other:[read:false,write:true]] |  [:]                              | false      | false     | false         | true

    }

    @Unroll
    void "it should set assignee config"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                hookConfig
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        content.getPermissions().assignee = new Permission(read,write)

        when:
        "the hook is called"
        hook.execute(content,new ContentMap(subConfig))

        then:
        content.permissions.assignee.read == finalRead
        content.permissions.assignee.write == finalWrite


        where:
        hookConfig                      | subConfig                         | read       | write     | finalRead     |   finalWrite
        [:]                             |  [:]                              | false      | false     | false         | false
        [:]                             |  [:]                              | true       | false     | true          | false
        [:]                             |  [:]                              | false      | true      | false         | true
        [assignee:[read:true,write:true]]  |  [:]                              | false      | false     | true          | true
        [:]                             |  [assignee:[read:true,write:true]]   | false      | false     | true          | true
        [assignee:[read:false,write:true]] |  [assignee:[read:true,write:false]]  | false      | false     | true          | false
        [assignee:[read:false,write:true]] |  [:]                              | false      | false     | false         | true

    }

    @Unroll
    void "it should set group permissions from config"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                [
                        group:[
                                cats:[
                                        read:true,
                                        write:false
                                ]
                        ]
                ]
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        when:
        "the hook is called"
        hook.execute(content,new ContentMap([:]))

        then:
        content.permissions.group.cats.read == true
        content.permissions.group.cats.write == false


    }

    @Unroll
    void "it should override group permissions from submission"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                [
                        group:[
                                cats:[
                                        read:true,
                                        write:false
                                ]
                        ]
                ]
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        when:
        "the hook is called"
        hook.execute(content,new ContentMap([
                group:[
                        cats:[
                                read:false,
                                write:true
                        ]
                ]
        ]))

        then:
        content.permissions.group.cats.read == false
        content.permissions.group.cats.write == true


    }

    @Unroll
    void "it should merge group permissions from submission"(){
        given:
        "a hook instatnce"
        ActionHook hook = setPermissions.createActionHook(new ContentMap(
                [
                        group:[
                                cats:[
                                        read:true,
                                        write:false
                                ]
                        ]
                ]
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
        )

        when:
        "the hook is called"
        hook.execute(content,new ContentMap([
                group:[
                        dogs:[
                                read:false,
                                write:true
                        ]
                ]
        ]))

        then:
        content.permissions.group.cats.read == true
        content.permissions.group.cats.write == false
        content.permissions.group.dogs.read == false
        content.permissions.group.dogs.write == true


    }


}
