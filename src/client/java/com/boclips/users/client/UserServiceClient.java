package com.boclips.users.client;

import com.boclips.users.client.model.Organisation;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.AccessRule;
import com.boclips.users.client.model.accessrule.ContentPackage;

import java.util.List;

public interface UserServiceClient {
    User findUser(String userId);

    List<AccessRule> getAccessRules(String userId);

    ContentPackage getContentPackage(String userId);

    Organisation getOrganisation(String organisationId);

    Boolean validateShareCode(String userId, String shareCode);
}
