package com.boclips.users.client.spring;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.implementation.FakeUserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakeUserServiceClientConfig {
    @Bean
    UserServiceClient fakeVideoServiceClient() {
        return new FakeUserServiceClient();
    }
}
