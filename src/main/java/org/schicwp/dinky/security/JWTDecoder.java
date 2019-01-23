package org.schicwp.dinky.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by will.schick on 1/18/19.
 */
public class JWTDecoder {

    private final String SECRET;

    public JWTDecoder(String secret) {
        SECRET = secret;
    }

    public UsernamePasswordAuthenticationToken decode(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token);


        String user = verify
                .getSubject();

        Claim roles = verify.getClaim("roles");

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (String s: roles.asArray(String.class)){

            authorities.add(new SimpleGrantedAuthority(s));
        }


        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }

        return null;
    }
}
