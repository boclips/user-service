package com.boclips.users.infrastructure.organisation

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class CustomizedOrganisationRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) :
    CustomizedOrganisationRepository {
    override fun findOrganisations(searchRequest: OrganisationSearchRequest): Page<OrganisationDocument> {
        val page = searchRequest.page ?: 0
        val pageSize = searchRequest.size ?: 30

        val offset = page * pageSize
        val query = Query()

        searchRequest.countryCode?.let {
            query.addCriteria(Criteria.where("country.code").`is`(it))
        }
        searchRequest.organisationTypes?.let {
            query.addCriteria(Criteria.where("type").`in`(searchRequest.organisationTypes))
        }

        if (searchRequest.parentOnly) {
            query.addCriteria(Criteria.where("parentOrganisation").`is`(null))
        }


        query.with(Sort(Sort.Direction.DESC, "accessExpiresOn").and(Sort(Sort.Direction.ASC, "name")))
        query.limit(pageSize)

        query.skip(offset.toLong())

        val totalElements = mongoTemplate.count(query, OrganisationDocument::class.java)
        val results = mongoTemplate.find(query, OrganisationDocument::class.java)

        return PageImpl(results, PageRequest.of(page, pageSize), totalElements)
    }
}

