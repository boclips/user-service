package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.httpclient.IntegrationsClient
import com.boclips.users.api.request.SynchroniseIntegrationUserRequest
import com.boclips.users.api.response.integration.SynchUserResource

class IntegrationsClientFake : IntegrationsClient, FakeClient<Pair<String,String>> {
    private val database: MutableMap<String, MutableSet<String>> = LinkedHashMap()

    override fun synchroniseUser(request: SynchroniseIntegrationUserRequest): SynchUserResource {
        add(request.deploymentId to request.externalUserId)
        return SynchUserResource(request.deploymentId + request.externalUserId)
    }

    override fun clear() {
        database.clear()
    }

    override fun add(element: Pair<String,String>): Pair<String,String> {
        val externalUserIds = database.getOrDefault(element.first, mutableSetOf())
        externalUserIds.add(element.second)
        database[element.first] = externalUserIds
        return element
    }

    override fun findAll(): List<Pair<String,String>> {
        //findAll won't be used to check if userId and deploymentId is added
        return emptyList()
    }
}
