package com.lhk.securitysample.config;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import com.lhk.securitysample.config.handler.CustomAccessDeniedHandler;
import com.lhk.securitysample.service.UserDetailServiceImpl;

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    public WebSecurityConfig(UserDetailServiceImpl userDetailsServiceImpl, 
            CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.userDetailsService = userDetailsServiceImpl;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
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
                .passwordParameter("pw")
                .failureUrl("/login?error=true").and()
            .logout()
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .invalidateHttpSession(true).and()
            .exceptionHandling()
                //.accessDeniedPage("/access_denied").and()
                .accessDeniedHandler(customAccessDeniedHandler).and()
            .authorizeRequests()
                .antMatchers("/private/**").hasAnyRole("USER")
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .anyRequest().permitAll().and()
            .oauth2Login
            // .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @fommatter:on
    }

    // private Filter ssoFilter() {
        
    //     OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/facebook");
    //     OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), auth2ClientContext);
    //     facebookFilter.setRestTemplate(facebookTemplate);
    //     UserInfoTokenServices tokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId());
    //     tokenServices.setRestTemplate(facebookTemplate);
    //     facebookFilter.setTokenServices(tokenServices);
    //     return facebookFilter;
    // }

    // // new
    // @Bean
    // @ConfigurationProperties("facebook.client")
    // public AuthorizationCodeResourceDetails facebook() {
    //     return new AuthorizationCodeResourceDetails();
    // }

    // // new
    // @Bean
    // @ConfigurationProperties("facebook.resource")
    // public ResourceServerProperties facebookResource() {
    //     return new ResourceServerProperties();
    // }

    // // new
    // @Bean
    // public OAuth2ClientContext auth2ClientContext() {
    //     return new DefaultOAuth2ClientContext();
    // }

    // // new
    // @Bean
    // public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
    //     FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
    //     registration.setFilter(filter);
    //     registration.setOrder(-100);
    //     return registration;
    // }    



    @Bean
    public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
        return WebClient.builder()
                .filter(oauth2).build();
    }

    @Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return request -> {
			OAuth2User user = delegate.loadUser(request);
			if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
				return user;
			}

			OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
					(request.getClientRegistration(), user.getName(), request.getAccessToken());
			String url = user.getAttribute("organizations_url");
			List<Map<String, Object>> orgs = rest
					.get().uri(url)
					.attributes(oauth2AuthorizedClient(client))
					.retrieve()
					.bodyToMono(List.class)
					.block();

			if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
				return user;
			}

			throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
		};
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}