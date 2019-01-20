package org.schicwp.dinky.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import java.util.Arrays;

/**
 * Created by will.schick on 1/18/19.
 */
@Order(1)
@Configuration
public class ProviderConfig extends WebSecurityConfigurerAdapter {

    @Value("${ad.domain}")
    private String AD_DOMAIN;

    @Value("${ad.url}")
    private String AD_URL;


    public static final String SIGN_UP_URL = "/api/v1/auth/token";

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        System.out.println("SEC");

        JWTEncoder.setAuthenticationManager(authenticationManager());


        http
                .cors().and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager(),new JWTDecoder()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        ;


    }
    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {

        authManagerBuilder.inMemoryAuthentication().withUser(new User(
                "joe",
                passwordEncoder().encode("bob"),
                Arrays.asList(new SimpleGrantedAuthority("ARF"))
        ));



       // authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
       //         .userDetailsService(userDetailsService());

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
