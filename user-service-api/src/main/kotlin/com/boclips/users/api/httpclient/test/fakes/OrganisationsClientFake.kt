package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.httpclient.OrganisationsClient
import com.boclips.users.api.response.organisation.OrganisationResource

class OrganisationsClientFake : OrganisationsClient, FakeClient<OrganisationResource> {
    private val database: MutableMap<String, OrganisationResource> = LinkedHashMap()

    override fun getOrganisation(id: String): OrganisationResource {
        return database[id] ?: throw FakeClient.notFoundException("Organisation $id not found")
    }

    override fun add(element: OrganisationResource): OrganisationResource {
        database[element.id] = element
        return element
    }

    override fun findAll(): List<OrganisationResource> {
        return database.values.toList()
    }

    override fun clear() {
        database.clear()
    }
}
