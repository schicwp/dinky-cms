package org.schicwp.dinky.workflow.hooks

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.search.SearchRepository
import org.schicwp.dinky.search.SearchService
import org.schicwp.dinky.workflow.ActionHook
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Consumer

/**
 * Created by will.schick on 1/22/19.
 */
class AssignSpec extends Specification{


    Assign assign;


    void setup(){
        assign = new Assign()
    }

    void "the name should be correct"(){
        expect:
        assign.name == "Assign"
    }

    @Unroll
    void "it should us the submission config if provided, or else the hook config"(){
        given:
        "a hook instatnce"
        ActionHook hook = assign.createActionHook(new ContentMap(
                hookConfig
        ))

        and:
        "some content"
        Content content = new Content(
                version: 13,
                assignedUser: user,
                assignedGroup: group
        )

        when:
        "the hook is called"
        hook.execute(content,new ContentMap(subConfig))

        then:
        content.assignedUser == finalUser
        content.assignedGroup == finalGroup


        where:
        hookConfig      | subConfig        | user       | group     | finalUser     | finalGroup
        [:]             |  [:]             | null       | null      | null          | null
        [:]             |  [:]             | "joe"      | "bob"     | "joe"         | "bob"
        [user:"bill"]   |  [:]             | "joe"      | "bob"     | "bill"        | "bob"
        [user:"bill"]   |  [user:"jim"]    | "joe"      | "bob"     | "jim"         | "bob"
        [group:"bill"]   |  [:]            | "joe"      | "bob"     | "joe"         | "bill"
        [group:"bill"]   |  [group:"jim"]  | "joe"      | "bob"     | "joe"         | "jim"

    }


}
