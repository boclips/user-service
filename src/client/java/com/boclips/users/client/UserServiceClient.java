package com.boclips.users.client;

import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.contract.Contract;

import java.util.List;

public interface UserServiceClient {
    User findUser(String userId);

    List<Contract> getContracts(String userId);

    Account getAccount(String accountId);

    Boolean validateShareCode(String userId, String shareCode);
}
