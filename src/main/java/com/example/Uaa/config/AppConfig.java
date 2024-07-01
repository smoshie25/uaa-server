package com.example.Uaa.config;


import com.example.Uaa.pojo.User;
import com.example.Uaa.security.CustomSecurityFilter;
import com.example.Uaa.security.SecurityContext;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public User server(){
        return new User();
    }

    @Bean
    public SecurityContext securityContext(User user){
        return new SecurityContext(user);
    }

    @Bean
    public FilterRegistrationBean<CustomSecurityFilter> apiKeySecurityFilterFilter(SecurityContext securityContext){
        FilterRegistrationBean<CustomSecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CustomSecurityFilter(securityContext));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }
}
