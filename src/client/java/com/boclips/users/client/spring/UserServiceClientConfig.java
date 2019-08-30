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
    public UserServiceClient userServiceClient(UserServiceClientProperties properties) {
        requireNonBlank(properties.getApiGatewayUrl(), "user-service-client.api-gateway-url is required");
        requireNonBlank(properties.getTokenUrl(), "user-service-client.token-url is required");
        requireNonBlank(properties.getClientId(), "user-service-client.client-id is required");
        requireNonBlank(properties.getClientSecret(), "user-service-client.client-secret is required");

        ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();
        credentials.setAccessTokenUri(properties.getApiGatewayUrl() + "/v1/token");
        credentials.setClientId(properties.getClientId());
        credentials.setClientSecret(properties.getClientSecret());

        return new ApiUserServiceClient(properties.getApiGatewayUrl(), new OAuth2RestTemplate(credentials));
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException((message));
        }
    }
}
