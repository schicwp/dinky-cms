package org.schicwp.dinky.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/18/19.
 */
public class JWTEncoder {


    private static String secret;



    private static AuthenticationManager authenticationManager;



    public static String getToken(String username, String password){



        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return JWT.create()
                .withSubject(authentication.getName())
                .withArrayClaim("roles",authentication
                        .getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                        .toArray(new String[0]))

                .sign(Algorithm.HMAC512(secret.getBytes()));

    }

    public static void setAuthenticationManager(AuthenticationManager authenticationManager) {
        JWTEncoder.authenticationManager = authenticationManager;
    }

    public static void setSecret(String secret) {
        JWTEncoder.secret = secret;
    }
}
