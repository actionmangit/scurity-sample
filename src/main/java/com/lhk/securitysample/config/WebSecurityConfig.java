package com.lhk.securitysample.config;

import com.lhk.securitysample.service.UserDetailServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    public WebSecurityConfig(UserDetailServiceImpl userDetailsServiceImpl) {
        this.userDetailsService = userDetailsServiceImpl;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // @fommatter:off
        // auth
        //     .inMemoryAuthentication()
        //         .withUser("user")
        //         .password(passwordEncoder().encode("1234"))
        //         .roles("USER");
        // @fommatter:on

        // @fommatter:off
        auth 
            .userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
        // @fommatter:on                    
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        // @fommatter:off
        web
            .ignoring()
                .antMatchers("/css", "/js");
        // @fommatter:on
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // @fommatter:off
        http
            .formLogin()
                .loginPage("/login")
                .usernameParameter("name")
                .passwordParameter("pw").and()
            .authorizeRequests()
                .antMatchers("/private/**").hasAnyRole("USER")
                .anyRequest().permitAll();
        // @fommatter:on
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}