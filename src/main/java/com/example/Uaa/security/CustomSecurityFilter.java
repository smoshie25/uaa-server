package com.example.Uaa.security;


import com.example.Uaa.helper.RequestEnricher;
import com.example.Uaa.helper.Tuple;
import com.example.Uaa.pojo.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

public class CustomSecurityFilter implements Filter {


    private final SecurityContext securityContext;




    public CustomSecurityFilter(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (securityContext.getUser().getPassword()!=null) {
            chain.doFilter(request, response);
            return;
        }

        final String authorization = ((HttpServletRequest) request).getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();

            Tuple<String, String> credentials = decodeCredentials(base64Credentials);

            User user = new User(UUID.randomUUID().toString(), credentials.getFirst(), credentials.getSecond());

            doFilterInContextOf(user, request, response, chain, securityContext);

        }else{
            addPrompting(request);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization request type");
            return;
        }

    }



    private Tuple<String, String> decodeCredentials(String base64Credentials) {
        String[] credentials = new String(Base64.getDecoder().decode(base64Credentials)).split(":");
        return new Tuple(credentials[0], credentials.length > 1 ? credentials[1] : "");
    }

    private void addPrompting(ServletRequest request) {
        new RequestEnricher(request).addOnUnauthorizedAction(
                resp -> resp.addHeader("WWW-Authenticate", "Basic"));
    }

    public static void doFilterInContextOf(User subject, ServletRequest request, ServletResponse response,
                                           FilterChain chain, SecurityContext securityContext) throws IOException, ServletException {
        try {
            securityContext.runInContextOf(subject, () -> {
                try {
                    chain.doFilter(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else if (e.getCause() instanceof ServletException) {
                throw (ServletException) e.getCause();
            } else {
                throw e;
            }
        }
    }

}
