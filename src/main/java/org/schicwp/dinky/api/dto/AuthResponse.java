package org.schicwp.dinky.api.dto;

/**
 * Created by will.schick on 1/18/19.
 */
public class AuthResponse {

    private final String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
