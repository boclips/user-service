package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.factories.OrganisationResourceFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.httpclient.ApiUsersClient
import com.boclips.users.api.request.CreateApiUserRequest
import com.boclips.users.api.response.user.UserResource
import java.util.UUID

class ApiUsersClientFake : ApiUsersClient, FakeClient<UserResource> {
    private val userDatabase: MutableMap<String, UserResource> = LinkedHashMap()

    override fun createApiUser(id: String, createApiUserRequest: CreateApiUserRequest) {
        userDatabase[id] = UserResourceFactory.sample(
            id = id,
            organisation = OrganisationResourceFactory.sampleDetails(id = createApiUserRequest.organisationId)
        )
    }

    override fun add(element: UserResource): UserResource {
        userDatabase[UUID.randomUUID().toString()] = element
        return element
    }

    override fun findAll(): List<UserResource> {
        return userDatabase.values.toList()
    }

    override fun clear() {
        userDatabase.clear()
    }
}
