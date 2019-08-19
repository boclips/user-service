package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.User;

public class ApiUserServiceClient implements UserServiceClient {
    @Override
    public User findUser(String userId) {
        return new User();
    }
}
