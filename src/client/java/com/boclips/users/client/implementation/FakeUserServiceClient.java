package com.boclips.users.client.implementation;

import com.boclips.users.client.UserServiceClient;
import com.boclips.users.client.model.Organisation;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;
import com.boclips.users.client.model.accessrule.ContentPackage;

import java.util.Collections;

public class FakeUserServiceClient implements UserServiceClient {
    private User user = null;
    private Organisation organisation = null;
    private ContentPackage contentPackage = null;

    @Override
    public User findUser(String userId) {
        return user;
    }

    public User addUser(User user) {
        this.user = user;
        return user;
    }

    @Override
    public ContentPackage getContentPackage(String userId) {
        return contentPackage;
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

    public void addContentPackage(ContentPackage contentPackage) {
        this.contentPackage = contentPackage;
    }

    public void addAccessRules(AccessRule accessRule) {
        if (contentPackage == null) {
            this.contentPackage = new ContentPackage("content-package-id", "the content package", Collections.emptyList());
        }

        this.contentPackage.getAccessRules().add(accessRule);
    }

    public void clearUser() {
        this.user = null;
    }

    public void clearOrganisation() {
        this.organisation = null;
    }

    public void clearContentPackage() {
        this.contentPackage = null;
    }
}
