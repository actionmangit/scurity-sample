package com.lhk.securitysample.web;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping(value = "/api/me")
    public Principal getPrincipal(Principal principal) {
        return principal;
    }
}