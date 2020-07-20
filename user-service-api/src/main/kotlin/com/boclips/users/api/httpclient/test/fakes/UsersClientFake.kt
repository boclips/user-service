package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import com.boclips.users.api.response.user.UserResource

class UsersClientFake : UsersClient, FakeClient<UserResource> {
    private val userDatabase: MutableMap<String, UserResource> = LinkedHashMap()
    private val accessRulesDatabase: MutableMap<String, AccessRulesResource> = LinkedHashMap()

    override fun getUser(id: String): UserResource {
        return userDatabase[id] ?: throw FakeClient.notFoundException("User not found")
    }

    override fun getAccessRulesOfUser(id: String): AccessRulesResource {
        return accessRulesDatabase[id] ?: throw FakeClient.notFoundException("User not found")
    }

    override fun getShareCode(id: String, shareCode: String) {
        if (userDatabase[id]?.teacherPlatformAttributes?.shareCode != shareCode) {
            throw FakeClient.forbiddenException("Invalid share code")
        }
    }

    override fun add(element: UserResource): UserResource {
        userDatabase[element.id] = element
        return element
    }

    override fun findAll(): List<UserResource> {
        return userDatabase.values.toList()
    }

    override fun clear() {
        userDatabase.clear()
        accessRulesDatabase.clear()
    }

    fun addAccessRules(userId: String, accessRulesResource: AccessRulesResource): AccessRulesResource {
        val currentRules = accessRulesDatabase[userId]?._embedded?.accessRules ?: emptyList()

        val mergedRules = currentRules.plus(accessRulesResource._embedded.accessRules)

        val amendedResource = AccessRulesResource(
            _embedded = AccessRulesWrapper(mergedRules)
        )

        accessRulesDatabase[userId] = amendedResource

        return amendedResource
    }
}
