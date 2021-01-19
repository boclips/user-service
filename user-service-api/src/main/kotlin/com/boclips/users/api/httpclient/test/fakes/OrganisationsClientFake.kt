package com.boclips.users.api.httpclient.test.fakes

import com.boclips.users.api.httpclient.OrganisationsClient
import com.boclips.users.api.request.OrganisationFilterRequest
import com.boclips.users.api.response.organisation.OrganisationResource
import com.boclips.users.api.response.organisation.OrganisationsResource
import com.boclips.users.api.response.organisation.OrganisationsWrapper
import org.springframework.hateoas.PagedModel

class OrganisationsClientFake : OrganisationsClient, FakeClient<OrganisationResource> {
    private val database: MutableMap<String, OrganisationResource> = LinkedHashMap()

    override fun getOrganisation(id: String): OrganisationResource {
        return database[id] ?: throw FakeClient.notFoundException("Organisation $id not found")
    }

    override fun getOrganisations(filterRequest: OrganisationFilterRequest): OrganisationsResource {
        val organisations = database.filter {
            when (filterRequest.hasCustomPrices) {
                true -> {
                    it.value.deal?.prices != null
                }
                false -> {
                    it.value.deal?.prices == null
                }
                else -> {
                    true
                }
            }
        }
            .map { it.value }

        return OrganisationsResource(
            _embedded = OrganisationsWrapper(organisations),
            page = PagedModel.PageMetadata(organisations.size.toLong(), 0, organisations.size.toLong()),
            _links = emptyMap()
        )
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
