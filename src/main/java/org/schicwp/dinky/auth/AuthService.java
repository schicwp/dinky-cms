package org.schicwp.dinky.auth;

import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class AuthService {



    private Map<String,String> groupMap = new HashMap<>();

    @Autowired
    MongoOperations mongoOperations;

    @PostConstruct
    void init(){
        groupMap.put("af-specification-editor","Editors");

        groupMap.values().forEach( s-> {
                    mongoOperations
                            .indexOps(Content.class)
                            .ensureIndex(
                                    new Index().on("permissions.group." +s + ".read", Sort.Direction.ASC)
                            );

                    mongoOperations
                            .indexOps(Content.class)
                            .ensureIndex(
                                    new Index().on("permissions.group." +s + ".write", Sort.Direction.ASC)
                            );
                }
        );
    }


    public User getCurrentUser(){
        User user = new User();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            user.setUsername(
                    authentication.getName()
            );

            authentication.getAuthorities().forEach(a -> {

                if (groupMap.containsKey(a.getAuthority()))
                    user.getGroups().add(groupMap.get(a.getAuthority()));
            });
        }

        return user;
    }
}
