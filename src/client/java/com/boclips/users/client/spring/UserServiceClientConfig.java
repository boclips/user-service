package com.boclips.users.client.spring;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.ApiUserServiceClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Configuration
public class UserServiceClientConfig {
    @Bean
    @ConfigurationProperties(prefix = "user-service")
    public UserServiceClientProperties userServiceClientProperties() {
        return new UserServiceClientProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "oauth2")
    public Oauth2CredentialProperties oauth2CredentialProperties() { return new Oauth2CredentialProperties(); }

    @Bean
    public UserServiceClient userServiceClient(UserServiceClientProperties userServiceClientProperties, Oauth2CredentialProperties oauth2CredentialProperties) {
        requireNonBlank(userServiceClientProperties.getBaseUrl(), "user-service.base-url is required");
        requireNonBlank(oauth2CredentialProperties.getTokenUrl(), "user-service.token-url is required");
        requireNonBlank(oauth2CredentialProperties.getClientId(), "user-service.client-id is required");
        requireNonBlank(oauth2CredentialProperties.getClientSecret(), "user-service.client-secret is required");

        ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();
        credentials.setAccessTokenUri(oauth2CredentialProperties.getTokenUrl());
        credentials.setClientId(oauth2CredentialProperties.getClientId());
        credentials.setClientSecret(oauth2CredentialProperties.getClientSecret());

        return new ApiUserServiceClient(userServiceClientProperties.getBaseUrl(), new OAuth2RestTemplate(credentials));
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException((message));
        }
    }
}
