package org.schicwp.dinky.api;

import org.schicwp.dinky.api.dto.AuthRequest;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.schicwp.dinky.security.JWTEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

/**
 * Created by will.schick on 1/15/19.
 */
@RestController
@Order(2)
@RequestMapping("/api/v1/auth")
public class AuthResource {



    @Autowired
    AuthService authService;


    @GetMapping("current")
    User getCurrentUser(){
        return authService.getCurrentUser();
    }


    @PostMapping("token")
    String getToken(@RequestBody AuthRequest authRequest){
        return JWTEncoder.getToken(authRequest.getUsername(),authRequest.getPassword());
    }

}
