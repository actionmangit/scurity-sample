package com.lhk.securitysample.config;

import com.lhk.securitysample.config.handler.CustomAccessDeniedHandler;
import com.lhk.securitysample.config.handler.LoginSuccessHandler;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class RootSecurityConfig {

    @Order
    @Configuration
    public static class FormSecurityConfigAdapter extends WebSecurityConfig {

        public FormSecurityConfigAdapter(
                final UserDetailsService userDetailsService,
                final CustomAccessDeniedHandler customAccessDeniedHandler,
                final LoginSuccessHandler loginSuccessHandler) {
            super(userDetailsService, customAccessDeniedHandler, loginSuccessHandler);
        }
    }

    @Order(1)
    @Configuration
    public static class ApiSecurityConfigAdapter extends ResourceServerConfig {} 
}