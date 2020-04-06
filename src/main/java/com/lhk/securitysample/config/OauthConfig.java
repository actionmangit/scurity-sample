package com.lhk.securitysample.config;

import java.util.UUID;

import com.lhk.securitysample.model.SocialProvider;
import com.lhk.securitysample.model.UserEntity;
import com.lhk.securitysample.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;    

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
		return request -> {
            OAuth2User user = delegate.loadUser(request);

            log.info(user.toString());

			if ("github".equals(request.getClientRegistration().getRegistrationId())) {

                log.info(String.valueOf(user.<Integer>getAttribute("id")));

                UserEntity userEntity = userRepository.findByUsernameAndSns(String.valueOf(user.<Integer>getAttribute("id")), SocialProvider.GITHUB);

                if (userEntity == null) {
                    userEntity = UserEntity.builder()
                                           .username(String.valueOf(user.<Integer>getAttribute("id")))
                                           .sns(SocialProvider.GITHUB)
                                           .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                           .build();
                }
        
                userRepository.save(userEntity);

			}
            
            return user;
		};
	}
}