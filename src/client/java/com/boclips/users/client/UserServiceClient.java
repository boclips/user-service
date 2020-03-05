package com.boclips.users.client;

import com.boclips.users.client.model.Organisation;
import com.boclips.users.client.model.User;
import com.boclips.users.client.model.accessrule.ContentPackage;

public interface UserServiceClient {
    User findUser(String userId);

    ContentPackage getContentPackage(String userId);

    Organisation getOrganisation(String organisationId);

    Boolean validateShareCode(String userId, String shareCode);
}
