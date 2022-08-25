package com.example.tattooartistbackend.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties("security")
@Configuration
@Getter
@Setter
public class SecurityProperties {

    FirebaseProperties firebaseProps;
    boolean allowCredentials;
    List<String> allowedOrigins;
    List<String> allowedHeaders;
    List<String> exposedHeaders;
    List<String> allowedMethods;
    List<String> allowedPublicApis;
    List<String> superAdmins;
    List<String> validApplicationRoles;
}
