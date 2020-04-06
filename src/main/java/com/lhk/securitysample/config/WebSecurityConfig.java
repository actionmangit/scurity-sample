package com.lhk.securitysample.config;

import com.lhk.securitysample.config.handler.CustomAccessDeniedHandler;
import com.lhk.securitysample.config.handler.LoginSuccessHandler;

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

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final LoginSuccessHandler loginSuccessHandler;

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
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/private/**").hasAnyRole("USER")
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll()
            )
            // .csrf(csrf -> csrf
            //     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // )
            .csrf(csrf -> csrf
                .disable()
            )            
            .headers(header -> header
                .frameOptions().disable()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expire=true")
            )
            .rememberMe(rememberMe -> rememberMe
                .alwaysRemember(false)
                .rememberMeParameter("remember-me")
            )
            .formLogin(fromLogin -> fromLogin
                .loginPage("/login")
                .usernameParameter("name")
                .passwordParameter("pw")
                .failureUrl("/login?error=true")
                .successHandler(loginSuccessHandler)
            )
            .logout(logout -> logout
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
            )
            .exceptionHandling(e -> e
                .accessDeniedHandler(customAccessDeniedHandler)
                //.accessDeniedPage("/access_denied")
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(loginSuccessHandler)
            );
        // @fommatter:on
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}