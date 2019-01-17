package org.schicwp.dinky.api;

import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by will.schick on 1/15/19.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthResource {



    @Autowired
    AuthService authService;

    @GetMapping("current")
    User getCurrentUser(){
        return authService.getCurrentUser();
    }

}
