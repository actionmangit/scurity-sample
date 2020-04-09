package com.lhk.securitysample.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Override
	protected void configure(HttpSecurity http) throws Exception {

		// @formatter:off
        http
            .antMatcher("/api/**") // /api/** 경로 관리
            .authorizeRequests(auth -> auth
                .antMatchers("/api/**").hasAuthority("CLIENT")
            )
            .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                .opaqueToken(opaqueToken -> opaqueToken
                    .introspectionUri("http://localhost:8080/introspection")
                    .introspectionClientCredentials("client", "secret")
                )
            );
		// @formatter:on
	}
}