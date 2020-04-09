package com.lhk.securitysample.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthController {

    @GetMapping(value = "/introspection")
    public String getIntrospection(HttpServletRequest req) {

        log.info("GET");
        log.info(req.toString());

        return "";
    }

    @PostMapping(value = "/introspection")
    public String postIntrospection(HttpServletRequest req) {

        log.info("POST");
        log.info(req.toString());

        return "";
    }
}