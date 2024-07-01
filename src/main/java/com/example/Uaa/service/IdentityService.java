package com.example.Uaa.service;


import com.example.Uaa.security.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class IdentityService {
    @Value("${federated.authorization.server.url}")
    private String identityServerUrl;
    private final SecurityContext securityContext;

    private final RestTemplate restTemplate;

    public IdentityService(SecurityContext securityContext) {
        this.securityContext = securityContext;
        restTemplate = new RestTemplate();
    }

    public String authenticate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(securityContext.getUser().getName(), securityContext.getUser().getPassword());
        headers.add("X-API-KEY","your-api-key-1");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(identityServerUrl, HttpMethod.GET, request, String.class);

        return response.getBody();
    }


}
