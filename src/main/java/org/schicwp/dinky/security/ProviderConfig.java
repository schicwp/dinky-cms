package org.schicwp.dinky.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/18/19.
 */
@Order(1)
@Configuration
public class ProviderConfig extends WebSecurityConfigurerAdapter {


    private static final Logger logger = Logger.getLogger(ProviderConfig.class.getCanonicalName());


    @Value("${dinky.security.key}")
    private String key;


    public static final String SIGN_UP_URL = "/api/v1/auth/token";

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        logger.severe("USING DUMMY SECURITY CONFIG, SHOULD NOT HAPPEN IN REAL WORLD");

        JWTEncoder.setAuthenticationManager(authenticationManager());
        JWTEncoder.setSecret(key);

        http
                .cors().and()
                .csrf()
                .disable();

        http
                .cors().and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers("/admin/*").permitAll()
                .antMatchers("/admin").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager(),new JWTDecoder(key)))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        ;


    }
    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {

        authManagerBuilder.inMemoryAuthentication().withUser(new User(
                "joe",
                passwordEncoder().encode("bob"),
                Arrays.asList(new SimpleGrantedAuthority("dev"))
        ));

    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        return authenticationManagerBuilder.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
