package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.Organisation;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;

import java.util.ArrayList;
import java.util.List;

public class FakeUserServiceClient implements UserServiceClient {
    private User user = null;
    private Organisation organisation = null;
    private List<AccessRule> accessRules = new ArrayList<>();

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
    public Organisation getOrganisation(String organisationId) {
        return organisation;
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

    public void addOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void addAccessRule(AccessRule accessRule) {
        accessRules.add(accessRule);
    }

    public void clearUser() {
        this.user = null;
    }

    public void clearOrganisation() {
        this.organisation = null;
    }


    public void clearAccessRules() {
        accessRules.clear();
    }
}
