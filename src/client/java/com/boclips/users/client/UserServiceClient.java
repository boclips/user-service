package com.boclips.users.client;

import com.boclips.users.client.model.contract.Contract;
import com.boclips.users.client.model.User;

import java.util.List;

public interface UserServiceClient {
    User findUser(String userId);
    List<Contract> getContracts(String userId);
}
