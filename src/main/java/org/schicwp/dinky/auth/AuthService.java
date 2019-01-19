package org.schicwp.dinky.auth;

import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
@ConfigurationProperties("auth")
public class AuthService {

    private Map<String,String> groupMap = new HashMap<>();

    public void setGroupMap(Map<String, String> groupMap) {
        this.groupMap = groupMap;
    }

    private final ThreadLocal<User> forcedUser = new ThreadLocal<>();

    private static final User SYSTEM_USER = new User("system", Collections.emptyList(),true);

    @Autowired
    MongoOperations mongoOperations;

    @PostConstruct
    void init(){
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

        if (forcedUser.get() != null)
            return forcedUser.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            return new User(
                    authentication.getName(),
                    authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(groupMap::get)
                        .collect(Collectors.toList()),
                    false
            );
        }

        return null;
    }

    public <T> T withSystemUser(Supplier<T> supplier){
        try {
            forcedUser.set(SYSTEM_USER);
            return supplier.get();
        }finally {
            forcedUser.remove();
        }
    }

    public void withSystemUser(Runnable supplier){
        try {
            forcedUser.set(SYSTEM_USER);
            supplier.run();
        }finally {
            forcedUser.remove();
        }
    }
}
