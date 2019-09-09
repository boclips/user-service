package com.boclips.users.client.spring;

import lombok.Data;

@Data
public class Oauth2CredentialProperties {
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
}
