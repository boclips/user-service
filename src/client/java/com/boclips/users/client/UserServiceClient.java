package com.boclips.users.client;

import com.boclips.users.client.model.User;

public interface UserServiceClient {
    User findUser(String userId);
}
