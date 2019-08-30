package com.boclips.users.client.spring;

import lombok.Data;

@Data
public class UserServiceClientProperties {
    private String apiGatewayUrl;
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
}
