package com.boclips.users.client;

import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;

import java.util.List;

public interface UserServiceClient {
    User findUser(String userId);

    List<AccessRule> getAccessRules(String userId);

    Account getAccount(String accountId);

    Boolean validateShareCode(String userId, String shareCode);
}
