package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.OrganisationResourceFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.api.response.accessrule.AccessRulesResource
import com.boclips.users.api.response.accessrule.AccessRulesWrapper
import com.boclips.users.api.response.user.UserResource

class UsersClientFake : UsersClient, FakeClient<UserResource> {
    private val userDatabase: MutableMap<String, UserResource> = LinkedHashMap()
    private val accessRulesDatabase: MutableMap<String, AccessRulesResource> = LinkedHashMap()
    private var loggedUser: UserResource? = null

    override fun getUser(id: String): UserResource {
        return userDatabase[id] ?: throw FakeClient.notFoundException("User not found")
    }

    override fun getAccessRulesOfUser(id: String): AccessRulesResource {
        return accessRulesDatabase[id] ?: throw FakeClient.notFoundException("User not found")
    }

    override fun getShareCode(id: String, shareCode: String) {
        if (userDatabase[id]?.shareCode != shareCode) {
            throw FakeClient.forbiddenException("Invalid share code")
        }
    }

    override fun getLoggedInUser(): UserResource {
        return loggedUser ?: throw FakeClient.forbiddenException("Access Denied")
    }

    override fun createApiUser(createApiUserRequest: CreateUserRequest.CreateApiUserRequest) {
        userDatabase[createApiUserRequest.apiUserId] = UserResourceFactory.sample(
            id = createApiUserRequest.apiUserId,
            organisation = OrganisationResourceFactory.sampleDetails(id = createApiUserRequest.organisationId)
        )
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

    fun setLoggedInUser(user: UserResource) {
        loggedUser = user
    }
}
