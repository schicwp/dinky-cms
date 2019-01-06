package org.schicwp.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class AuthService {


    public User getCurrentUser(){
        User user = new User();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {



            user.setUsername(
                    authentication.getName()
            );

            authentication.getAuthorities().forEach(a -> {
                user.getGroups().add(a.getAuthority());
            });

            System.out.println(user.getGroups());
        }

        return user;
    }
}
