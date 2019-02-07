package org.schicwp.dinky.content;

import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/18/19.
 */
@Service
public class PermissionService {


    @Autowired
    AuthService authService;

    public boolean allowRead(Content content){
        User user = authService.getCurrentUser();

        if (user.isSystemUser())
            return true;

        if (content.getPermissions().getOther().isRead())
            return true;

        if (user.getUsername().equals(content.getOwner()) &&
                content.getPermissions().getOwner().isRead())
            return true;

        if (user.getUsername().equals(content.getAssignedUser()) &&
                content.getPermissions().getAssignee().isRead())
            return true;

        for (String group: user.getGroups()){

            if (group.equals(content.getAssignedGroup()) &&
                    content.getPermissions().getAssignee().isRead())
                return true;

            if (content.getPermissions().getGroup().containsKey(group) &&
                    content.getPermissions().getGroup().get(group).isRead())
                return true;
        }

        return false;
    }

    public boolean allowWrite(Content content){
        User user = authService.getCurrentUser();

        if (user.isSystemUser())
            return true;

        if (content.getPermissions().getOther().isWrite())
            return true;

        if (user.getUsername().equals(content.getOwner()) &&
                content.getPermissions().getOwner().isWrite())
            return true;

        if (user.getUsername().equals(content.getAssignedUser()) &&
                content.getPermissions().getAssignee().isWrite())
            return true;

        for (String group: user.getGroups()){

            if (group.equals(content.getAssignedGroup()) &&
                    content.getPermissions().getAssignee().isWrite())
                return true;

            if (content.getPermissions().getGroup().containsKey(group) &&
                    content.getPermissions().getGroup().get(group).isWrite())
                return true;
        }

        return false;
    }


    public Criteria getPermissionFilter() {

        User user = authService.getCurrentUser();

        if (user.isSystemUser())
            return new Criteria();

        List<Criteria> criteria = user.getGroups().stream().map(s->
                Criteria.where("permissions.group." + s + ".read").is(true)
        ).collect(Collectors.toList());

        criteria.add(
                Criteria.where("owner").is(user.getUsername()).and("permissions.owner.read").is(true)
        );

        criteria.add(
                Criteria.where("permissions.assignee.read").is(true)
                .orOperator(
                        Criteria.where("assignedUser").is(user.getUsername()),
                        Criteria.where("assignedGroup").in(user.getGroups())
                )
        );

        criteria.add(
                Criteria.where("permissions.other.read").is(true)
        );


        return new Criteria()
                .orOperator(
                        criteria.toArray(new Criteria[0])
                );
    }


}
