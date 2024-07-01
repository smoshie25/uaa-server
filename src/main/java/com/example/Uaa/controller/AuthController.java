package com.example.Uaa.controller;

import com.example.Uaa.service.IdentityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final IdentityService identityService;

    public AuthController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping("/auth/token")
    public String getToken() {


        return identityService.authenticate();
    }
}
