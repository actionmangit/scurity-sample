package com.lhk.securitysample.web;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SecurityController {

    @GetMapping(value = "/")
    public String home() {
        return "index";
    }

    @GetMapping(value = "/private")
    public String privatgePage() {
        return "private";
    }

    @GetMapping(value = "/private")
    public String privatePage(Principal principal, Model model) {
        model.addAttribute("user", principal);
        return "private";
    }

    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping(value = "/private/context")
    public String privateContextPage(
        @AuthenticationPrincipal Authentication authentication
    ) {

        log.info(authentication.getPrincipal().toString());

        return "private";
    }

    @GetMapping(value = "/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping(value = "/access_denied")
    public String adminDeniedPage() {
        return "access_denied";
    }
}