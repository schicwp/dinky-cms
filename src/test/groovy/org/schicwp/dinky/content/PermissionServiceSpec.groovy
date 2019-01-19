package org.schicwp.dinky.content

import org.schicwp.dinky.auth.AuthService
import org.schicwp.dinky.auth.User
import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.Permission
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Specification

/**
 * Created by will.schick on 1/19/19.
 */
class PermissionServiceSpec extends Specification {

    PermissionService permissionService;
    AuthService authService;
    User user;
    Content content;

    void setup() {
        authService = Mock(AuthService)
        permissionService = new PermissionService(
                authService: authService
        )

        user = new User("test", [], false);

        _ * authService.currentUser >> user

        content = new Content()
        content.permissions.owner.read = false
        content.permissions.other.read = false
        content.permissions.assignee.read = false

        content.permissions.owner.write = false
        content.permissions.other.write = false
        content.permissions.assignee.write = false
    }

    void "the system user should be allowed to read anything"() {

        given:
        "a system user"
        user.systemUser = true


        expect:
        permissionService.allowRead(content)

    }

    void "the system user should be allowed to write anything"() {

        given:
        "a system user"
        user.systemUser = true


        expect:
        permissionService.allowWrite(content)

    }

    void "a  user should not be allowed to read anything"() {


        expect:
        !permissionService.allowRead(content)

    }

    void "a user should be not allowed to write anything"() {


        expect:
        !permissionService.allowWrite(content)

    }


    void "any user should be able to read if other is true"() {

        given:
        "content with no other perms"
        content.permissions.other.read = true

        expect:
        permissionService.allowRead(content)

    }

    void "any user should be able to write if other is true"() {

        given:
        "content with no other perms"
        content.permissions.other.write = true

        expect:
        permissionService.allowWrite(content)

    }

    void "the owner should be able to read"() {

        given:
        "content with owner perms"
        content.owner = user.username
        content.permissions.owner.read = true

        expect:
        permissionService.allowRead(content)

    }

    void "the owner should be able to write"() {

        given:
        "content with owner perms"
        content.owner = user.username
        content.permissions.owner.write = true

        expect:
        permissionService.allowWrite(content)

    }

    void "the assignedUser should be able to read"() {

        given:
        "content with owner perms"
        content.assignedUser = user.username
        content.permissions.assignee.read = true

        expect:
        permissionService.allowRead(content)

    }

    void "the assignedUser should be able to write"() {

        given:
        "content with owner perms"
        content.assignedUser = user.username
        content.permissions.assignee.write = true

        expect:
        permissionService.allowWrite(content)

    }

    void "the assignedGroup should be able to read"() {

        given:
        "content with owner perms"
        user.groups += "users"
        user.groups += "other"
        content.assignedGroup = "users"
        content.permissions.assignee.read = true

        expect:
        permissionService.allowRead(content)

    }

    void "the assignedGroup should be able to write"() {

        given:
        "content with owner perms"
        user.groups += "users"
        user.groups += "other"
        content.assignedGroup = "users"
        content.permissions.assignee.write = true

        expect:
        permissionService.allowWrite(content)

    }

    void "the for group perms should be able to read"() {

        given:
        "content with owner perms"
        user.groups += "users"
        user.groups += "other"
        content.permissions.group.other = new Permission(true, false)

        expect:
        permissionService.allowRead(content)

    }

    void "the for group perms should be able to write"() {

        given:
        "content with owner perms"
        user.groups += "users"
        user.groups += "other"
        content.permissions.group.other = new Permission(false, true)


        expect:
        permissionService.allowWrite(content)

    }

    void "mongo criteria should have user ownership"() {
        expect:
        Query.query(permissionService.getPermissionFilter()).toString() == 'Query: { "$or" : [{ "owner" : "test", "permissions.owner.read" : true }, { "permissions.assignee.read" : true, "$or" : [{ "assignedUser" : "test" }, { "assignedGroup" : { "$in" : [] } }] }, { "permissions.other.read" : true }] }, Fields: { }, Sort: { }'
    }

    void "mongo criteria should have assign ownership"() {

        given:
        user.groups += "users"
        user.groups += "other"


        expect:
        Query.query(permissionService.getPermissionFilter()).toString() == 'Query: { "$or" : [{ "permissions.group.users.read" : true }, { "permissions.group.other.read" : true }, { "owner" : "test", "permissions.owner.read" : true }, { "permissions.assignee.read" : true, "$or" : [{ "assignedUser" : "test" }, { "assignedGroup" : { "$in" : ["users", "other"] } }] }, { "permissions.other.read" : true }] }, Fields: { }, Sort: { }'
    }

    void "mongo criteria should be empty for system user"(){
        given:
        user.systemUser = true

        expect:
        Query.query(permissionService.getPermissionFilter()).toString() == 'Query: { }, Fields: { }, Sort: { }'
    }
}