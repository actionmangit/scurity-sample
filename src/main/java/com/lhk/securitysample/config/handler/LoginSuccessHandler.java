package com.lhk.securitysample.config.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lhk.securitysample.model.UserEntity;
import com.lhk.securitysample.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Authentication authentication) throws IOException, ServletException {

        log.info("========================={}", authentication.getName());

        UserEntity userEntity = userRepository.findByUsername(authentication.getName());

        userRepository.save(
                UserEntity.builder()
                          .idx(userEntity.getIdx())
                          .password(userEntity.getPassword())
                          .username(userEntity.getUsername())
                          .sns(userEntity.getSns())
                          .lastLogin(LocalDateTime.now())
                          .build()
        );

        super.onAuthenticationSuccess(request, response, authentication);
    }
}