package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.Account;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;

import java.util.ArrayList;
import java.util.List;

public class FakeUserServiceClient implements UserServiceClient {
    private User user = null;
    private List<AccessRule> accessRules = new ArrayList<>();
    private Account account = null;

    @Override
    public User findUser(String userId) {
        return user;
    }

    public User addUser(User user) {
        this.user = user;
        return user;
    }

    @Override
    public List<AccessRule> getAccessRules(String userId) {
        return accessRules;
    }

    @Override
    public Account getAccount(String accountId) {
        return account;
    }

    @Override
    public Boolean validateShareCode(String userId, String shareCode) {
        if (user == null) {
            return false;
        }
        if (user.getId().equals(userId)) {
            return user.getTeacherPlatformAttributes().getShareCode().equals(shareCode);
        }
        return false;
    }

    public void addAccessRule(AccessRule accessRule) {
        accessRules.add(accessRule);
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public void clearUser() {
        this.user = null;
    }

    public void clearAccount() {
        this.account = null;
    }

    public void clearAccessRules() {
        accessRules.clear();
    }
}
